package com.github.realzimboguy.ewelink.api;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

    private void dologin() throws Exception {
        if (!isLoggedIn || lastActivity + (activityTimer * 60 * 1000) < new Date().getTime()) {
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
                LOGGER.info("Login Response Raw: {}", response);
    
                LoginResponse loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
    
                if (loginResponse.getError() > 0){
                    //something wrong with login, throw exception back up with msg
                    throw new Exception(loginResponse.getMsg());
                }
                else {
                    accessToken = loginResponse.getData().getAt();
                    apiKey = loginResponse.getData().getUser().getApikey();
                    LOGGER.info("accessToken: {}", accessToken);
                    LOGGER.info("apiKey: {}", apiKey);
    
                    isLoggedIn = true;
                    lastActivity = new Date().getTime();
                }
            }
            createClient();
        }
    }
    
    private void createClient() throws Exception {
        if (eweLinkWebSocketClient!=null) {
            eweLinkWebSocketClient.close();
        }
        
        eweLinkWebSocketClient = new EweLinkWebSocketClient(new URI("wss://"+ region+"-pconnect3.coolkit.cc:8080/api/ws"));
        eweLinkWebSocketClient.setWssResponse(clientWssResponse);
        eweLinkWebSocketClient.setWssLogin(gson.toJson(new WssLogin(accessToken, apiKey, APP_ID, Util.getNonce())));
        eweLinkWebSocketClient.connect();

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
    
    public List<Thing> getThings() throws Exception {
        return getHomePage().getData().getThingInfo().getThingList();
    }
    
    public Optional<Thing> getThing(String deviceId) throws Exception {
        return getHomePage().getData().getThingInfo().getThingList().stream()
                .filter(t -> deviceId.equals(t.getItemData().getDeviceid()))
                .findAny();
    }

    public Homepage getHomePage() throws Exception {
        dologin();

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
                throw new Exception("getHome Error:" + gson.toJson(homepage));

            }
            else {
                LOGGER.info("getHome: {}", gson.toJson(homepage));
                lastActivity = new Date().getTime();
                return homepage;
            }
        }
    }

    public boolean setDeviceStatusByName(String name, DeviceStatus status) throws Exception{
        dologin();

        String selectedDeviceId = null;
        for (Thing thing : getThings()) {
            if (thing.getItemData().getName().equalsIgnoreCase(name)){
                selectedDeviceId = thing.getItemData().getDeviceid();
            }
        }

        if (selectedDeviceId == null) {
            throw new Exception("No Device id Found for Device Name:" + name);
        }

        return setDeviceStatus(selectedDeviceId,status);
    }

    public boolean setDeviceStatus(String deviceId, DeviceStatus status) throws Exception{
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
     * @throws Exception
     */
    public boolean setMultiDeviceStatus(String deviceId, List<OutletSwitch> outletSwitches) throws Exception{
        dologin();

        LOGGER.info("Setting device {} status on multi output {}",deviceId,gson.toJson(outletSwitches));

        StatusChange statusChange = new StatusChange();
        statusChange.setSequence(new Date().getTime() + "");
        statusChange.setUserAgent("app");
        statusChange.setAction("update");
        statusChange.setDeviceid(deviceId);
        statusChange.setApikey(apiKey);
        statusChange.setSelfApikey(apiKey);
        Params params = new Params();
        params.setSwitches(outletSwitches);
        statusChange.setParams(params);

        LOGGER.debug("StatusChange WS Request:{}",gson.toJson(statusChange));

        return eweLinkWebSocketClient.sendAndWait(gson.toJson(statusChange),statusChange.getSequence());
    }

    private static String getAuthMac (String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256_HMAC = null;

        byte[] byteKey = APP_SECRET.getBytes("UTF-8");
        final String HMAC_SHA256 = "HmacSHA256";
        sha256_HMAC = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
        sha256_HMAC.init(keySpec);
        byte[] mac_data = sha256_HMAC.
                doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(mac_data);
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
