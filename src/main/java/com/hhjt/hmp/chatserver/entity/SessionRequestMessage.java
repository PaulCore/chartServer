package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * 会话申请报文对象
 * Created by paul on 2015/1/5.
 */
public class SessionRequestMessage extends RequestMessage {
    private String sourNumber;
    private String desNumber;
    public SessionRequestMessage(String command, byte[] data, SocketReceiver receiver) {

        super(command, data, receiver);
        unpackage();
    }

    @Override
    public void unpackage() {
        sourNumber = new String(data,8,11);
        desNumber = new String(data,19,11);
    }

    public String getDesNumber() {
        return desNumber;
    }

    public String getSourNumber() {
        return sourNumber;
    }
}
