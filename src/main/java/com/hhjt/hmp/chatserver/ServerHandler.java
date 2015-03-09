package com.hhjt.hmp.chatserver;

import com.hhjt.hmp.chatserver.entity.*;
import com.hhjt.hmp.chatserver.queues.*;
import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;
import com.hhjt.hmp.chatserver.socketHandler.SocketSender;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by paul on 2014/12/31.
 */
public class ServerHandler extends Thread{
    private static Logger logger = Logger.getLogger(ServerHandler.class);
    public ServerHandler(){
        logger.debug("ServerHandler begin:");
    }
    @Override
    public void run() {
        RequestMessage requestMessage = null;
        while (true){
            logger.debug("消息队列暂时为空，waiting。。。");
            if((requestMessage = ServerMessageQueue.getMessage())!=null){
                logger.debug("Server handler 取出消息开始处理。。。");
                String commandStr = requestMessage.getCommand();
                Command command = Command.valueOf(commandStr);
                switch (command){
                    case CORQ://连接申请命令
                        logger.debug("处理连接申请命令");
                        ConnectRequestMessage connectRequestMessage = (ConnectRequestMessage)requestMessage;
                        connectionRequestHandler(connectRequestMessage);
                        break;
                    case APRQ://会话申请命令
                        logger.debug("处理会话申请命令");
                        SessionRequestMessage sessionRequestMessage = (SessionRequestMessage)requestMessage;
                        sessionRequestHandler(sessionRequestMessage);
                        break;
                    case JOAS://会话要求回复
                        SessionConfirmResponseMessage sessionConfirmResponseMessage = (SessionConfirmResponseMessage) requestMessage;
                        sessionConfirmResponseHandler(sessionConfirmResponseMessage);
                        break;
                    case EXRQ:
                        logger.debug("处理会话退出申请");
                        sessionExitRequestHandler((SessionExitRequestMessage)requestMessage);
                        break;
                    case RCRQ:
                        logger.debug("处理会话重连");
                        reconnectRequestHandler((ReConnectRequestMessage) requestMessage);
                        break;
                    case SEND://会话发送
                        logger.debug("处理发送命令");
                        ContextSendMessage contextSendMessage = (ContextSendMessage) requestMessage;
                        SessionQueue.sendMessage(contextSendMessage.getRoomNumber(), contextSendMessage.getSender(), contextSendMessage);
                        break;
                    case QUIT:
                        QuitMessage quitMessage = (QuitMessage) requestMessage;
                        quitHandler(quitMessage);
                    case STAT:
                        break;
                    default:
                        logger.error("没有该命令");
                }

            }
        }

    }

    //申请连接处理
    private void connectionRequestHandler(ConnectRequestMessage connectRequestMessage){
        //1.查看用户队列是否存在，若不存在，该用户建立队列，并开启发送线程,返回正确报文，并挂上其队列
        //2.若用户队列已存在，判断此次和之前是否为同一个socket，若是，返回错误报文，并挂其队列上
        //3.若不是同一个socket，关闭原socket，将新的值付给用户，为其开启新的发送线程，返回错误报文，并挂其队列上
        String registerNumber = connectRequestMessage.getRegisterNumber();
        SocketReceiver receiver = connectRequestMessage.getReceiver();
        receiver.setRegisteNumber(registerNumber);
        ConnectResponseMessage responseMessage = null;
        if (ClientQueue.isContain(registerNumber)){//此用户队列应经存在
            logger.debug(registerNumber+"用户队列已存在");
            //生成相应报文,错误类型代码，1代表重复申请，2代表断开后重连申请
            responseMessage = new ConnectResponseMessage("COAS",1,0);
            Client client = ClientQueue.getClient(registerNumber);
            if (client.getReceiver().getSocket()==receiver.getSocket()){//
                logger.debug(registerNumber+"用户为同一个socket");
                responseMessage.setError("1");//设置错误代码
            }else {
                responseMessage.setError("2");
                logger.debug(registerNumber+"用户为不同socket");
                try {
                    client.getReceiver().getSocket().close();
//                    receiver.setRegisteNumber(registerNumber);
                    createSocketSender(receiver,registerNumber,client);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else {//此用户队列不存在
            logger.debug(registerNumber+"用户队列不存在，为其创建队列");
//            receiver.setRegisteNumber(registerNumber);
            //建队列
            ClientQueue.addClient(registerNumber,connectRequestMessage.getLevel(),receiver);
            //建立发送线程
            SocketSender sender = new SocketSender(receiver.getSocket(),registerNumber);
            receiver.setSocketSender(sender);
            sender.start();
            responseMessage = new ConnectResponseMessage("COAS",1,1);
        }
        ClientQueue.addMessage(registerNumber,responseMessage);
    }

    //会话申请处理
    private void sessionRequestHandler(SessionRequestMessage sessionRequestMessage){
        //1.查看被申请者的状态，若为不在线，掉线，离线，忙，则直接返回错误报文
        //错误代码为相应的状态码，不在线为0，2离线，3掉线，4忙，9被医师拒绝
        //2.若被申请者状态为在线，生成会话室，向被申请者发送会话要求报文，为防止其他用户再次
        //对此用户发起申请，将被申请者的状态置为忙
        String fromNumber = sessionRequestMessage.getSourNumber();//申请会话者
        String toNumber = sessionRequestMessage.getDesNumber();//被申请的医师
        SessionResponseMessage sessionResponseMessage = null;
        //若果toNumber不在线直接返回错误
        if(!(ClientQueue.isContain(toNumber))){
            logger.info(fromNumber+"申请会话的"+toNumber+"不在线");
            sessionResponseMessage = new SessionResponseMessage("APAS",1,0);
            sessionResponseMessage.setError("0");
            ClientQueue.addMessage(fromNumber, sessionResponseMessage);
        }else {
            char state = ClientQueue.getClient(toNumber).getState();
            switch (state){
                case '1':
                    String sessionId = SessionQueue.createSession(fromNumber, toNumber);
                    logger.debug("新创会话室为：" + sessionId);
//                    //设置用户对应的会话室
//                    ClientQueue.getClient(fromNumber).setSessionId(sessionId);
                    //准备向医师发送会话要求，生成相应的实例
                    SessionConfirmRequestMessage sessionConfirmRequestMessage =
                            new SessionConfirmRequestMessage("JORQ",28,sessionId,fromNumber,toNumber);
                    //将其挂到相应医师的队列中
                    ClientQueue.addMessage(toNumber, sessionConfirmRequestMessage);
                    break;
                case '2':
                    sessionResponseMessage = new SessionResponseMessage("APAS", 1, 0);
                    sessionResponseMessage.setError("2");
                    ClientQueue.addMessage(fromNumber, sessionResponseMessage);
                    break;
                case '3':
                    sessionResponseMessage = new SessionResponseMessage("APAS", 1, 0);
                    sessionResponseMessage.setError("3");
                    ClientQueue.addMessage(fromNumber, sessionResponseMessage);
                    break;
                case '4':
                    sessionResponseMessage = new SessionResponseMessage("APAS", 1, 0);
                    sessionResponseMessage.setError("4");
                    ClientQueue.addMessage(fromNumber, sessionResponseMessage);
                    break;
                default:
            }

        }

    }

    //会话重连处理
    private void reconnectRequestHandler(ReConnectRequestMessage reConnectRequestMessage){
        String registerNumber = reConnectRequestMessage.getRegisterNumber();
        String seesionId = reConnectRequestMessage.getSessionId();
        //1.取出该registerNumber的原接收线程对应的socket,并将其关闭，以此达到关闭原接收与发送线程
        //2.将registerNumber对应的socket的接收线程设置为此次携带的socket的接收线程
        //3.为新的socket建立发送线程，并将其设到相应的接收线程中
        //4.开启新的发送线程
        Client client = ClientQueue.getClient(registerNumber);
        Socket socket = client.getReceiver().getSocket();
        try {
            socket.close();
            reConnectRequestMessage.getReceiver().setRegisteNumber(registerNumber);
            createSocketSender(reConnectRequestMessage.getReceiver(), registerNumber, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //会话退出处理方法
    private void sessionExitRequestHandler(SessionExitRequestMessage message){
        String sessionId = message.getSessionId();
        String registerNumber = message.getRegisterNumber();
        //1.将registerNumber从sessionId中删除
        //2.生成sessionExitResponseMessage实例，并挂到registerNumber队列中
        //3.更改registerNumber的状态
        //4.生成状态发送对应实例
        //5.若会话室人数小于2，删除此会话室
        if (SessionQueue.removeMember(sessionId, registerNumber)){
            /*
            此过程在实现上可以优化
             */
            SessionExitResponseMessage responseMessage = new SessionExitResponseMessage("EXAS",1,1);
            ClientQueue.addMessage(registerNumber, responseMessage);
            ClientQueue.getClient(registerNumber).setState('1');
            StateSendMessage stateSendMessage = new StateSendMessage("STAT",12,registerNumber,'1');
            SessionQueue.sendMessage(sessionId, "Server", stateSendMessage);
            SessionQueue.isDeleteSession(sessionId);
        }else {//发生错误
            SessionExitResponseMessage responseMessage = new SessionExitResponseMessage("EXAS",1,0);
            ClientQueue.addMessage(registerNumber, responseMessage);

        }
    }

    //会话要求回复处理
    private void sessionConfirmResponseHandler(SessionConfirmResponseMessage sessionConfirmResponseMessage){
        SessionResponseMessage sessionResponseMessage;
        //若该会话室已不存在直接扔掉，否则走流程
        if(SessionQueue.isContain(sessionConfirmResponseMessage.getSessionId())){
            if (sessionConfirmResponseMessage.getResult().equals("1")){//会话要求回复成功
                //将该医师的状态置为忙
                //ClientQueue.getClient(sessionConfirmResponseMessage.getRequiredNumber()).setState('4');
                //生成相应的会话回复实例
                sessionResponseMessage = new SessionResponseMessage("APAS",7,1);
                //将请求者的状态也变为忙
                ClientQueue.getClient(sessionConfirmResponseMessage.getRequestNumber()).setState('4');
                sessionResponseMessage.setRoomNumber(sessionConfirmResponseMessage.getSessionId());
            }else {//会话要求回复失败，生成失败实例
                sessionResponseMessage = new SessionResponseMessage("APAS",1,0);
                //将医师的状态改为在线
                ClientQueue.getClient(sessionConfirmResponseMessage.getRequiredNumber()).setState('1');
                //将错误值设为9
                sessionResponseMessage.setError("9");
                //将会话室从会话队列删除
                SessionQueue.deleteSession(sessionConfirmResponseMessage.getSessionId());
            }
            ClientQueue.addMessage(sessionConfirmResponseMessage.getRequestNumber(), sessionResponseMessage);
        }else {
            logger.debug("会话室" + sessionConfirmResponseMessage.getSessionId() + "已不存在，该条会话要求回复无效");
        }

    }

    //退出连接处理
    //删除用户队列
    //暂未向会话室成员跟新状态
    private void quitHandler(QuitMessage quitMessage){
        String regsiterNumber = quitMessage.getRegisterNumber();
        //若果用户在摸个会话室中，则退出该会话室
        Client client = ClientQueue.getClient(regsiterNumber);
        SessionQueue.removeMember(client.getSessionId(),quitMessage.getRegisterNumber());
        SessionQueue.isContain(client.getSessionId());
        //删除用户队列
        ClientQueue.removeClient(regsiterNumber);
    }

    //为用户开启发送线程
    private void createSocketSender(SocketReceiver receiver, String registeNumber, Client client){
        //1.生成发送线程实例
        //2.将此发送线程和相应的接收线程建立联系
        //3.将此接收线程与相应的用户建立联系
        //4.启动发送线程
        SocketSender sender = new SocketSender(receiver.getSocket(),registeNumber);
        receiver.setSocketSender(sender);
        client.setReceiver(receiver);
        sender.start();
    }
}
