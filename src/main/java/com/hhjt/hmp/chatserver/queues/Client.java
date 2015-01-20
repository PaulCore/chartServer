package com.hhjt.hmp.chatserver.queues;

import com.hhjt.hmp.chatserver.entity.Response;
import com.hhjt.hmp.chatserver.entity.ResponseMessage;
import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by paul on 2015/1/4.
 */
public class Client {
    private SocketReceiver receiver;//用户所对应的接收线程;
    private char state = '1';//用户状态：1在线，2离线，3掉线,4忙
    private byte level;//用户级别：50H客户，20H医师
    private String sessionId;//用户所在会话室编号，若不在则为null
    private LinkedBlockingQueue<Response> messages;
    public Client(byte level, SocketReceiver receiver){
        this.level = level;
        this.receiver = receiver;
        messages = new LinkedBlockingQueue<Response>();
    }
    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public Response getMessage() {
        return messages.poll();
    }

    public SocketReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(SocketReceiver receiver) {
        this.receiver = receiver;
    }

    public void setMessages(Response messsage) {
        messages.offer(messsage);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "state=" + state +
                ", level=" + level +
                ", messages=" + messages +
                '}';
    }
}
