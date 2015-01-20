package com.hhjt.hmp.chatserver.entity;

import javax.print.attribute.SetOfIntegerSyntax;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by paul on 2015/1/12.
 */
public class StateSendMessage extends ResponseMessage {
    private char state;
    private String registeNumber;
    public StateSendMessage(String command, int paramLength,String registeNumber, char state) {
        super(command, paramLength);
        this.state = state;
        this.registeNumber = registeNumber;
    }


    @Override
    public byte[] pack() {
        excutLength();
        dataStr = head + length + command + paramLengthString + registeNumber + state + tail;
        return dataStr.getBytes();
    }
}
