package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * Created by paul on 2015/1/8.
 */
public class SessionExitRequestMessage extends RequestMessage{
    private String sessionId;
    private String registerNumber;
    public SessionExitRequestMessage(String command, byte[] data, SocketReceiver receiver) {
        super(command, data, receiver);
        unpackage();
    }

    @Override
    public void unpackage() {
        sessionId = new String(data,8,6);
        registerNumber = new String(data,14,11);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }
}
