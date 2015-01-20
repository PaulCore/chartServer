package com.hhjt.hmp.chatserver.entity;

/**
 * Created by paul on 2015/1/6.
 */
public class SessionConfirmRequestMessage extends ResponseMessage {
    private String roomNumber;
    private String sourNumber;
    private String desNumber;
    public SessionConfirmRequestMessage(String command, int paramLength, String roomNumber,
                                        String sourNumber, String desNumber) {
        super(command, paramLength);
        this.roomNumber = roomNumber;
        this.sourNumber = sourNumber;
        this.desNumber = desNumber;
    }

    @Override
    public byte[] pack() {
        excutLength();
        dataStr = head + length + command + paramLengthString +
                roomNumber + sourNumber + desNumber +tail;
        return  dataStr.getBytes();
    }
}
