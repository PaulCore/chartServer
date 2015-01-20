package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * Created by paul on 2015/1/5.
 */
public abstract class RequestMessage implements Request{
    protected String command;
    protected byte[] data;
    protected String paramLength;
    protected SocketReceiver receiver;

    public RequestMessage(String command, byte[] data, SocketReceiver receiver){
        this.command = command;
        this.data = data;
        this.receiver = receiver;
        paramLength = new String(data,4,4);
    }
    public abstract void  unpackage();

    public String getCommand() {
        return command;
    }

    public byte[] getData() {
        return data;
    }

    public SocketReceiver getReceiver() {
        return receiver;
    }
}
