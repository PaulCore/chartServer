package com.hhjt.hmp.chatserver.socketHandler;

import com.hhjt.hmp.chatserver.entity.QuitMessage;
import com.hhjt.hmp.chatserver.entity.RequestMessage;
import com.hhjt.hmp.chatserver.entity.ConnectRequestMessage;
import com.hhjt.hmp.chatserver.queues.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by paul on 2014/12/30.
 */
public class SocketReceiver extends  Thread {
    private static Logger logger = Logger.getLogger(SocketReceiver.class);
    private Socket socket;
    private SocketSender socketSender;
    private String registeNumber;
    private byte[] buf = new byte[6];
    private int length = 6;
    private boolean isHead = true;

    public SocketReceiver(Socket socket){
        logger.debug("receiver thread start");
        this.socket = socket;
        try {
            socket.setKeepAlive(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            while (input.read(buf,0,length)!=-1){
                packageCheck();
            }
//            logger.debug(socket+"receive线程关闭");
            logger.info(registeNumber+"退出登陆");

        } catch (IOException e) {
            logger.info(socket+"异常关闭");
            e.printStackTrace();
            return;
        }
        finally {
            if (socketSender!=null)
                socketSender.exit = true;
        }

        logger.debug(socket+"正常关闭");
        //1.若该用户的注册号不为空需要删除队列
        if (registeNumber!=null)
            ServerMessageQueue.addMessage(new QuitMessage("QUIT",null,this,registeNumber));
    }


    private void packageCheck( ){
        //首先判断此次接收的是包头还是数据段
        if (isHead){
            if ((buf[0]==35)&&buf[1]==35){
                logger.debug(new String(buf));
                //计算出下次该接收的数据段长度
                length = (buf[2]-48)*1000+(buf[3]-48)*100+(buf[4]-48)*10+buf[5]-48;
                //设置下次接收的相应的参数
                isHead = false;
                length = length+2;//+包尾的长度
                buf = new byte[length];
            }else {
                logger.error("报文头错误直接扔掉");
            }
        }else {//本次接收的为数据段
           if((buf[length-2]==13)&&(buf[length-1]==10)){//判断结束符
               //对数据段进行处理，生成相应的实例
               packageHandler(buf);
               logger.debug(new String(buf));
            }else {
               logger.error("包尾错误直接扔掉");
           }

            //设置下次接收的相应参数
            isHead = true;
            length = 6;
            //由于数据段长度>6所以不用新生成byte[]
        }
    }

    private void packageHandler(byte[] data){
        RequestMessage message = MessageFactory.getMessage(data,this);
        ServerMessageQueue.addMessage(message);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocketSender(SocketSender socketSender) {
        this.socketSender = socketSender;
    }

    public void setRegisteNumber(String registeNumber) {
        this.registeNumber = registeNumber;
    }

    public String getRegisteNumber() {
        return registeNumber;
    }
}
