package com.github.realzimboguy.ewelink.api;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.realzimboguy.ewelink.api.wss.WssResponse;
import com.github.realzimboguy.ewelink.api.wss.wssrsp.WssRspMsg;
import com.google.gson.Gson;

import net.jodah.expiringmap.ExpiringMap;

public class EweLinkWebSocketClient extends WebSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(EweLinkWebSocketClient.class);

    private WssResponse wssResponse;
    private String wssLogin;

    private final Map<String, WssRspMsg> map = ExpiringMap.builder()
            .maxSize(100)
            .expiration(60, TimeUnit.SECONDS)
            .build();

    private final Gson gson = new Gson();

    public void setWssResponse(WssResponse wssResponse) {
        this.wssResponse = wssResponse;
    }

    public void setWssLogin(String wssLogin) {
        this.wssLogin = wssLogin;
    }

    public EweLinkWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(wssLogin);
    }

    @Override
    public void onMessage(String s) {
        if (s!= null && s.equalsIgnoreCase("pong")){
            //swallow this as its just a ping/pong
            LOGGER.debug(s);
        }else {
            parseMessage(s);
            if (wssResponse!=null) {
                wssResponse.onMessage(s);
            }
        }
    }

    private void parseMessage(String s) {
        if (s!=null) {
            WssRspMsg rsp = gson.fromJson(s, WssRspMsg.class);
            if (rsp.getSequence()!= null) {
                map.put(rsp.getSequence(), rsp);
            }
    
            if (wssResponse!=null) {
                wssResponse.onMessageParsed(rsp);
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if (i!=CloseFrame.NORMAL) {
            LOGGER.warn("WS onCloseCalled, system will self-recover {} {} {}",i,s,b);
        }
    }

    @Override
    public void onError(Exception e) {
        if (wssResponse!=null) {
            wssResponse.onError(e.getMessage());
        }
    }

    public boolean sendAndWait(String text, String sequence) throws InterruptedException {
        send(text);

        //waits a total of 15 seconds

        for (int i = 0; i < 30; i++) {
            //wait 1 second
            Thread.sleep(500);

            if (map.containsKey(sequence)){
                WssRspMsg s = map.remove(sequence);
                return s.getError() != null && s.getError().intValue()==0;
            }
        }
        return false;
    }
}
