package com.hhjt.hmp.chatserver.queues;

import com.hhjt.hmp.chatserver.Command;
import com.hhjt.hmp.chatserver.entity.*;
import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;
import org.apache.log4j.Logger;

import java.net.Socket;

/**
 * Created by paul on 2015/1/5.
 */
public class MessageFactory {
    private static Logger logger = Logger.getLogger(MessageFactory.class);
    public static RequestMessage getMessage(byte[] data, SocketReceiver receiver){
        //取出前四位命令
        String commandStr = new String(data,0,4);
        Command command = Command.valueOf(commandStr);
        logger.debug(command);
        switch (command){
            case CORQ://请求连接
                return new ConnectRequestMessage(commandStr,data,receiver);
            case APRQ://请求会话
                return new SessionRequestMessage(commandStr,data,receiver);
            case JOAS://会话要求回复
                return new SessionConfirmResponseMessage(commandStr,data,receiver);
            case SEND://会话发送
                return new ContextSendMessage(commandStr,data,receiver);
            case EXRQ:
                logger.debug("生成SessionExitRequestMessage实例");
                return new SessionExitRequestMessage(commandStr,data,receiver);
            case RCRQ:
                return new ReConnectRequestMessage(commandStr,data,receiver);
            case TEST:
                return new Test(commandStr,data,receiver);
            default:
                logger.info("报文命令出错，丢弃");
                return null;
        }
    }
}
