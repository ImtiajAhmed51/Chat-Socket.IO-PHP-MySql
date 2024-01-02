package com.example.chat.server;

import android.app.Application;

import com.example.chat.utils.Config;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager extends Application {
    private static Socket mSocket;
    private static SocketManager socketManager = null;

    private SocketManager() {
        try {
            Socket socket = IO.socket(Config.SOCKET_URL);
            mSocket = socket;
            socket.connect();
        } catch (URISyntaxException e) {
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static SocketManager getInstance() {
        if (socketManager == null) {
            socketManager = new SocketManager();
        }
        return socketManager;
    }

    public Emitter getEmitterListener(String event, Emitter.Listener listener) {
        return getSocket().on(event, listener);
    }
}