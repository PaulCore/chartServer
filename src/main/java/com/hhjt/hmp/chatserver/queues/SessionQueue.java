package com.hhjt.hmp.chatserver.queues;

import com.hhjt.hmp.chatserver.entity.Response;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by paul on 2015/1/5.
 */
public class SessionQueue {
    private volatile static long count = 0;
    //String为聊天室编号，set为聊天室成员
    private static Hashtable<String,Set<String>> queue = new Hashtable<String, Set<String>>();
    private static Logger logger = Logger.getLogger(SessionQueue.class);

    /**
     * 创建聊天室
     * @param creater 创建者
     * @param invitee 被邀请者
     * @return
     */
    public static String createSession(String creater, String invitee){
        String sessionId = String.format("%06d",++count);
        Set<String> members = new HashSet<String>();
        members.add(creater);
        members.add(invitee);
        //将创建的会话室加入队列中
        queue.put(sessionId,members);
        //设置相应用户对应的会话室
        ClientQueue.getClient(creater).setSessionId(sessionId);
        ClientQueue.getClient(invitee).setSessionId(sessionId);
        logger.debug("现有聊天室："+ queue);
        return sessionId;
    }
    public static Set<String> getMembers(String sessionId){
        return queue.get(sessionId);
    }

    //会话室是否存在
    public static boolean isContain(String sessionId){
        return queue.containsKey(sessionId);
    }

    //删除成员
    public static boolean removeMember(String sessionId, String registerNumber){
        boolean b = false;
        Set<String> members = queue.get(sessionId);
        if (members!=null){
            b = members.remove(registerNumber);
            //将该用户的会话室成员变量设置为空
            ClientQueue.getClient(registerNumber).setSessionId(null);
            logger.info(registerNumber + "成功从会话室" +sessionId+ "中删除");
        }else {
            logger.error(registerNumber + "从会话室" +sessionId+ "中删除时出错");
            //错误处理。。。
        }
        return b;
    }

    //判断是否需要删除会话室,若需要直接删除返回true，否则返回false
    public static boolean isDeleteSession(String sessionId){
        boolean b = false;
        Set<String> set = queue.get(sessionId);
        if (set!=null&&set.size()<2){
            logger.info("会话室"+sessionId+"由于人数小于2，删除");
            queue.remove(sessionId);
            for (String s:set){
                Client client = ClientQueue.getClient(s);
                client.setState('1');
                client.setSessionId(null);
//                ClientQueue.getClient(s).setState('1');
            }
            b = true;
        }
        return b;
    }

    //删除会话室，会话室存在并删除返回true,不存在返回false
    public static boolean deleteSession(String sessionId){
        boolean b = false;
        if (queue.containsKey(sessionId)){

            //将会话室成员的会话编号都设为空
            Set<String> set = SessionQueue.getMembers(sessionId);
            for (String s: set){
                ClientQueue.getClient(s).setSessionId(null);
            }
            queue.remove(sessionId);
            logger.info("会话室"+sessionId+"被删除");
            b = true;
        }
        return b;
    }

    //转发消息
    public static void sendMessage(String sessionId, String sender, Response message){
        Iterator<String> members = queue.get(sessionId).iterator();
        while (members.hasNext()){
            String client = members.next();
            //转发给除去发送者之外的其他成员
            if (!(client.equals(sender))){
                ClientQueue.addMessage(client, message);
            }
        }
    }

}
