package com.hhjt.hmp.chatserver.entity;

/**
 * Created by paul on 2015/1/8.
 */
public class SessionExitResponseMessage extends ResponseMessage {

    public SessionExitResponseMessage(String command, int paramLength, int result) {
        super(command, paramLength,result);
    }
//    public SessionExitResponseMessage(Stri)

    @Override
    public byte[] pack() {
        excutLength();
       dataStr = head + length + command + paramLengthString + result + error + tail;
        return dataStr.getBytes();
    }
}
