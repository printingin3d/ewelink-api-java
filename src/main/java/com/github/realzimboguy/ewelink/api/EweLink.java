package com.github.realzimboguy.ewelink.api;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.realzimboguy.ewelink.api.errors.EweDeviceNotFoundException;
import com.github.realzimboguy.ewelink.api.errors.EweException;
import com.github.realzimboguy.ewelink.api.errors.EweLoginException;
import com.github.realzimboguy.ewelink.api.errors.EweResponseException;
import com.github.realzimboguy.ewelink.api.errors.EweSecurityException;
import com.github.realzimboguy.ewelink.api.model.DeviceStatus;
import com.github.realzimboguy.ewelink.api.model.StatusChange;
import com.github.realzimboguy.ewelink.api.model.home.Homepage;
import com.github.realzimboguy.ewelink.api.model.home.OutletSwitch;
import com.github.realzimboguy.ewelink.api.model.home.Params;
import com.github.realzimboguy.ewelink.api.model.home.Thing;
import com.github.realzimboguy.ewelink.api.model.login.LoginRequest;
import com.github.realzimboguy.ewelink.api.model.login.LoginResponse;
import com.github.realzimboguy.ewelink.api.wss.WssLogin;
import com.github.realzimboguy.ewelink.api.wss.WssResponse;
import com.google.gson.Gson;


public class EweLink implements Closeable {
    private static final String HMAC_SHA256 = "HmacSHA256";
    
    public static final String APP_ID = "Uw83EKZFxdif7XFXEsrpduz5YyjP7nTl";
    private static final String APP_SECRET = "mXLOjea0woSMvK9gw7Fjsy7YlFO4iSu6";
    private static final int TIMEOUT = 5000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EweLink.class);

    private final String region;
    private final String email;
    private final String password;
    private final String countryCode;
    private final int activityTimer;
    private final String baseUrl;
    private final ScheduledExecutorService executor;
    private final boolean executorShouldBeStopped;
    
    private final Gson gson = new Gson();
    
    private boolean isLoggedIn = false;
    private long lastActivity = 0L;

    private String accessToken;
    private String apiKey;
    private WssResponse clientWssResponse;

    private EweLinkWebSocketClient eweLinkWebSocketClient = null;
    private ScheduledFuture<?> monitorThread;

    private EweLink(String region, String email, String password, String countryCode, int activityTimer,
            ScheduledExecutorService executor, boolean executorShouldBeStopped) {
        this.region = region;
        this.email = email;
        this.password = password;
        this.countryCode = countryCode;
        this.executor = executor;
        this.executorShouldBeStopped = executorShouldBeStopped;
        
        this.baseUrl = "https://"+Objects.requireNonNull(region)+"-apia.coolkit.cc/v2/";
        if (activityTimer < 30) {
            activityTimer = 30;
        }
        this.activityTimer = activityTimer;
        
        LOGGER.info("EweLinkApi startup params : {} {}", region,email);
    }
    
    public EweLink(String region, String email, String password,String countryCode, int activityTimer) {
        this(region, email, password, countryCode, activityTimer, Executors.newScheduledThreadPool(1), true);
    }
    
    public EweLink(String region, String email, String password, String countryCode, int activityTimer,
            ScheduledExecutorService executor) {
        this(region, email, password, countryCode, activityTimer, executor, false);
    }
    
    public synchronized void logout() {
        accessToken = null;
        apiKey = null;

        isLoggedIn = false;
        lastActivity = 0L;
    }

    private void dologin() throws EweException {
        if (!isLoggedIn || lastActivity + (activityTimer * 60 * 1000) < new Date().getTime()) {
            try {
                synchronized (this) {
                    URL url = new URL(baseUrl + "user/login");
            
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
            
                    conn.setConnectTimeout(TIMEOUT);
                    conn.setReadTimeout(TIMEOUT);
                    
                    LoginRequest loginRequest = new LoginRequest(email, password, countryCode, "en");
            
                    conn.setRequestProperty("Content-Type","application/json" );
                    conn.setRequestProperty("Authorization","Sign " +getAuthMac(gson.toJson(loginRequest)));
                    conn.setRequestProperty("X-Ck-Nonce",Util.getNonce());
                    conn.setRequestProperty("X-Ck-Appid",APP_ID);
            
                    LOGGER.info("Login Request: {}", loginRequest);
            
                    try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                        wr.writeBytes(gson.toJson(loginRequest));
                
                        wr.flush();
                    }
                    int responseCode = conn.getResponseCode();
            
                    LOGGER.info("Login Response Code :"+ responseCode);
            
                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        LOGGER.debug("Login Response Raw: {}", response);
            
                        LoginResponse loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
            
                        if (loginResponse.getError() > 0){
                            //something wrong with login, throw exception back up with msg
                            throw new EweLoginException(loginResponse.getMsg());
                        }
                        accessToken = loginResponse.getData().getAt();
                        apiKey = loginResponse.getData().getUser().getApikey();
                        LOGGER.debug("accessToken: {}", accessToken);
                        LOGGER.debug("apiKey: {}", apiKey);
        
                        isLoggedIn = true;
                        lastActivity = new Date().getTime();
                    }
                    createClient();
                }
            }
            catch (IOException e) {
                throw new EweLoginException("Exception caught during login", e);
            }
        }
    }
    
    private void createClient() throws IOException {
        if (eweLinkWebSocketClient!=null) {
            eweLinkWebSocketClient.close();
        }
        
        try {
            eweLinkWebSocketClient = new EweLinkWebSocketClient(new URI("wss://"+ region+"-pconnect3.coolkit.cc:8080/api/ws"));
            eweLinkWebSocketClient.setWssResponse(clientWssResponse);
            eweLinkWebSocketClient.setWssLogin(gson.toJson(new WssLogin(accessToken, apiKey, APP_ID, Util.getNonce())));
            
            if (!eweLinkWebSocketClient.connectBlocking()) {
                throw new EweException("Cannot make websocket connection");
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new EweException(e);
        }

        if (monitorThread!=null) {
            monitorThread.cancel(false);
        }
        
        monitorThread = executor.scheduleAtFixedRate(new WebSocketMonitor(), 30, 30, TimeUnit.SECONDS);
    }

    public void setWssResponse(WssResponse wssResponse) {
        clientWssResponse = wssResponse;
        if (eweLinkWebSocketClient!=null) {
            eweLinkWebSocketClient.setWssResponse(clientWssResponse);
        }
    }
    
    public List<Thing> getThings() throws EweException {
        return getHomePage().getData().getThingInfo().getThingList();
    }
    
    public Optional<Thing> getThing(String deviceId) throws EweException {
        return getHomePage().getData().getThingInfo().getThingList().stream()
                .filter(t -> deviceId.equals(t.getItemData().getDeviceid()))
                .findAny();
    }

    public Homepage getHomePage() throws EweException {
        dologin();

        try {
            URL url = new URL(baseUrl + "homepage");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json" );
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization","Bearer " +accessToken);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);
    
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.writeBytes("{\n" +
                        "  \"getFamily\": {},\n" +
                        "  \"getThing\": {\n" +
                        "    \"num\": 300\n" +
                        "  },\n" +
                        "  \"lang\": \"en\"\n" +
                        "}");
        
                wr.flush();
            }
    
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                LOGGER.debug("getHome Response Raw: {}", response.toString());
    
                Homepage homepage = gson.fromJson(response.toString(), Homepage.class);
    
                LOGGER.debug("getHome Response: {}", gson.toJson(homepage));
    
                if (homepage.getError() > 0) {
                    //something wrong with login, throw exception back up with msg
                    throw new EweResponseException("getHome Error:" + gson.toJson(homepage));
    
                }
                LOGGER.info("getHome: {}", gson.toJson(homepage));
                lastActivity = new Date().getTime();
                return homepage;
            }
        }
        catch (IOException e) {
            throw new EweException(e);
        }
    }

    public boolean setDeviceStatusByName(String name, DeviceStatus status) throws EweException {
        dologin();

        String selectedDeviceId = null;
        for (Thing thing : getThings()) {
            if (thing.getItemData().getName().equalsIgnoreCase(name)){
                selectedDeviceId = thing.getItemData().getDeviceid();
            }
        }

        if (selectedDeviceId == null) {
            throw new EweDeviceNotFoundException("No Device id Found for Device Name:" + name);
        }

        return setDeviceStatus(selectedDeviceId,status);
    }

    public boolean setDeviceStatus(String deviceId, DeviceStatus status) throws EweException {
        dologin();

        LOGGER.info("Setting device {} status to {}",deviceId,status);

        StatusChange statusChange = new StatusChange();
        statusChange.setSequence(String.valueOf(new Date().getTime()));
        statusChange.setUserAgent("app");
        statusChange.setAction("update");
        statusChange.setDeviceid(deviceId);
        statusChange.setApikey(apiKey);
        statusChange.setSelfApikey(apiKey);
        Params params = new Params();
        params.setSwitch(status.getStringValue());
        statusChange.setParams(params);

        LOGGER.debug("StatusChange WS Request:{}",gson.toJson(statusChange));

        return eweLinkWebSocketClient.sendAndWait(gson.toJson(statusChange),statusChange.getSequence());
    }

    /**
     * you will need to populate the outlet switch based on the type device you have and not just the one you want to change (i think), not sure if this will break if you send more or less outlets than requred,example
     * output
     * "switches": [ { "switch": "off", "outlet": 0 }, { "switch": "on", "outlet": 1 }, { "switch": "off", "outlet": 2 }, { "switch": "off", "outlet": 3 } ]
     * @param deviceId
     * @param outletSwitches
     * @return
     * @throws EweException
     */
    public boolean setMultiDeviceStatus(String deviceId, int outlet, DeviceStatus status) throws EweException {
        dologin();

        LOGGER.info("Setting device {} status on outlet {} to {}",deviceId,outlet,status);

        StatusChange statusChange = new StatusChange();
        statusChange.setSequence(new Date().getTime() + "");
        statusChange.setUserAgent("app");
        statusChange.setAction("update");
        statusChange.setDeviceid(deviceId);
        statusChange.setApikey(apiKey);
        statusChange.setSelfApikey(apiKey);
        Params params = new Params();
        List<OutletSwitch> outletSwitches = Collections.singletonList(
                new OutletSwitch(outlet, status.getStringValue()));
        params.setSwitches(outletSwitches);
        statusChange.setParams(params);

        LOGGER.debug("StatusChange WS Request:{}",gson.toJson(statusChange));

        return eweLinkWebSocketClient.sendAndWait(gson.toJson(statusChange),statusChange.getSequence());
    }

    private static String getAuthMac (String data) throws UnsupportedEncodingException, EweSecurityException {
        byte[] byteKey = APP_SECRET.getBytes("UTF-8");
        Mac sha256_HMAC;
        try {
            sha256_HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
            sha256_HMAC.init(keySpec);
            byte[] mac_data = sha256_HMAC.
                    doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(mac_data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new EweSecurityException(e);
        }
    }

    private class WebSocketMonitor implements Runnable {
        @Override
        public void run() {
            try {
                LOGGER.debug("send websocket ping");
                eweLinkWebSocketClient.send("ping");

            } catch (Exception e) {
                LOGGER.error("Error in sending websocket ping:",e);
                LOGGER.info("Try reconnect to websocket");
                try {
                    createClient();
                }
                catch (Exception c) {
                    LOGGER.error("Error trying to reconnect:",c);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (executorShouldBeStopped) {
            LOGGER.info("Shutting down executor");
            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
        else {
            if (monitorThread!=null) {
                monitorThread.cancel(false);
            }
        }
        LOGGER.info("Executor stopped");
        if (eweLinkWebSocketClient!=null) {
            eweLinkWebSocketClient.close();
            LOGGER.info("WebSocket client closed");
        }
    }
}
