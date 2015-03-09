package com.hhjt.hmp.chatserver.queues;

import com.hhjt.hmp.chatserver.entity.Response;
import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;
import org.apache.log4j.Logger;

import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by paul on 2015/1/4.
 */
public class ClientQueue {
    private static Logger logger = Logger.getLogger(ClientQueue.class);
    private static Hashtable<String,Client> clientMessageQueue = new Hashtable<String, Client>();

    //添加一个新用户，为其建立队列
    public static void addClient(String registerNumber, byte level, SocketReceiver receiver){
        Client client = new Client(level,receiver);
        logger.debug("目前的客户端队列为"+clientMessageQueue);
        clientMessageQueue.put(registerNumber,client);
        logger.debug("加入当前用户后客户端队列为"+clientMessageQueue);
    }
    //删除队列
    public static void removeClient(String registerNumber){
        clientMessageQueue.remove(registerNumber);
    }
    public static Client getClient(String number){
        return clientMessageQueue.get(number);
    }

    //判断用户是否已存在
    public static boolean isContain(String registerNumber){
        return clientMessageQueue.containsKey(registerNumber);
    }

    //获取用户的消息队列
    public static Response getMessage(String registerNumber){
        return clientMessageQueue.get(registerNumber).getMessage();
    }
    public static void addMessage(String registerNumber, Response message){
        clientMessageQueue.get(registerNumber).setMessages(message);
    }
    //判断用户队列是否有消息
    public static boolean isEmpty(String registerNumber){
        return clientMessageQueue.get(registerNumber).getMessages().isEmpty();
    }

}
