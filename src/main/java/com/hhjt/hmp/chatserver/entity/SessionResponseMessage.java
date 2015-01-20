package com.hhjt.hmp.chatserver.entity;

/**
 * Created by paul on 2015/1/5.
 */
public class SessionResponseMessage extends ResponseMessage {
    private int result;
    private String roomNumber;
    private String error;
    public SessionResponseMessage(String command, int paramLength, int result) {
        super(command, paramLength);
        this.result = result;
    }

    public void setRoomNumber(int number) {
        roomNumber = String.format("%06d",number);
    }
    public void setRoomNumber(String number) {
        roomNumber = number;
    }

    public void setError(String error)
    {
        this.error = error;
        paramLengthInt = paramLengthInt + error.length();
    }

    @Override
    public byte[] pack() {
        excutLength();

        //成功
        if (result==1){
                dataStr = head + length+command+paramLengthString+result+roomNumber+tail;

        }else{//失败
                dataStr = head + length+command+paramLengthString+result+error+tail;
        }
        return dataStr.getBytes();
    }
}
