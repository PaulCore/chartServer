package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * Created by paul on 2015/1/6.
 */
public class ContextSendMessage extends RequestMessage implements Response {
    private String roomNumber;//会话室编号
    private String sender;
    public ContextSendMessage(String command, byte[] data, SocketReceiver receiver) {
        super(command, data, receiver);
        unpackage();
    }

    @Override
    public void unpackage() {
        roomNumber = new String(data,8,6);
        sender = new String(data,14,11);
    }

    @Override
    public byte[] pack() {
        //计算数据段的长度，data包含了包尾，所以-2
        int length = data.length-2;
        //包头+数据段长度
        String front = "##" + String.format("%04d",length);
        byte[] f = front.getBytes();
        byte[] res = new byte[6+data.length];//最后结果
        //组包，将f和data copy到res中
        System.arraycopy(f,0,res,0,6);
        System.arraycopy(data,0,res,6,data.length);
        return res;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getSender() {
        return sender;
    }
}
