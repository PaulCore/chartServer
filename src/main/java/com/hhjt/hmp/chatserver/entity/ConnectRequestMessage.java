package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * 连接申请
 * Created by paul on 2015/1/5.
 */
public class ConnectRequestMessage extends RequestMessage {
    private String registerNumber;//注册号
    private byte level;//级别50H客户，20H医师
    public ConnectRequestMessage(String command, byte[] data, SocketReceiver receiver){
        super(command,data,receiver);
        unpackage();
    }
    @Override
    public void unpackage() {
        registerNumber = new String(data,8,11);
        level = data[19];
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public byte getLevel() {
        return level;
    }
}
