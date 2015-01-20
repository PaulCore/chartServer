package com.hhjt.hmp.chatserver.entity;

import java.nio.charset.Charset;

/**
 * 连接申请回复
 * Created by paul on 2015/1/5.
 */
public class ConnectResponseMessage extends ResponseMessage {
    /**
     * 不含错误信息
     * @param command
     * @param paramLength
     * @param result
     */
    public ConnectResponseMessage(String command, int paramLength, int result) {
        super(command,paramLength,result);
    }



    @Override
    public byte[] pack() {
        excutLength();
        //格式化
        dataStr = head + length+command+paramLengthString+result+error+tail;
        return dataStr.getBytes();
    }
}
