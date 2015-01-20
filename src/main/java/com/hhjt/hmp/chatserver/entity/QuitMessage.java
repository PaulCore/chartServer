package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

/**
 * Created by paul on 2015/1/16.
 */
public class QuitMessage extends RequestMessage {
    private String registerNumber;
    public QuitMessage(String command, byte[] data, SocketReceiver receiver, String registeNumber) {
        super(command, data, receiver);
        this.receiver = receiver;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    @Override
    public void unpackage() {

    }
}
