package com.example.remotecarcontrol;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SockelConnection {

    private static Socket ws;
    private static SockelConnection instance;

    private SockelConnection(String ip) throws URISyntaxException {

        ws = IO.socket("http://" + ip);
        ws.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

            }
        });
        ws.connect();
    }

    public static SockelConnection getInstance(String ip) throws URISyntaxException {
        if(instance == null)
            instance = new SockelConnection(ip);
        return instance;
    }


    public void send(String msg){
        ws.send(msg);
    }

}
