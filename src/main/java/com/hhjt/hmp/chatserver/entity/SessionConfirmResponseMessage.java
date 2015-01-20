package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

import java.net.Socket;

/**
 * Created by paul on 2015/1/6.
 */
public class SessionConfirmResponseMessage extends RequestMessage {
    private String result;
    private String sessionId;
    private String requestNumber;
    private String requiredNumber;
    private String error;

    public SessionConfirmResponseMessage(String command, byte[] data, SocketReceiver receiver) {
        super(command, data, receiver);
        unpackage();
    }

    @Override
    public void unpackage() {
        paramLength = new String(data,4,4);
        result = new String(data,8,1);
        sessionId = new String(data,9,6);
        requestNumber = new String(data,15,11);
        requiredNumber = new String(data,26,11);
        int lengthValue = Integer.parseInt(paramLength);
        if (lengthValue>29){
            error = new String(data,37,lengthValue-29);
        }

    }

    public String getResult() {
        return result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public String getRequiredNumber() {
        return requiredNumber;
    }

    public String getError() {
        return error;
    }
}
