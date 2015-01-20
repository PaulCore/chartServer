package com.hhjt.hmp.chatserver.entity;

/**
 * Created by paul on 2015/1/5.
 */
public abstract class ResponseMessage implements Response{
    protected String command;//命令
    protected int paramLengthInt;//参数长度十进制
    protected String paramLengthString;//格式化后参数长度
    protected String head = "##";//包头
    protected String tail = "\r\n";//包尾
    protected String length;//数据段长度
    protected String dataStr = "";//结果字符串
    protected int result;//返回结果值，0失败，1成功
    protected String error = "";//错误信息
    public ResponseMessage(String command, int paramLength){
        this.command = command;
        this.paramLengthInt = paramLength;
    }
    public ResponseMessage(String command, int paramLengthInt, int result){
        this(command,paramLengthInt);
        this.result = result;
    }

    public void setError(String error) {
        this.error = error;
        paramLengthInt = paramLengthInt + error.length();
    }

    public void excutLength(){
        paramLengthString = String.format("%04d",paramLengthInt);
        //数据段长度为参数长度+4+4
        length =  String.format("%04d",paramLengthInt+8);
    }
    public abstract byte[] pack();
}
