package com.hhjt.hmp.chatserver.socketHandler;

import com.hhjt.hmp.chatserver.entity.Response;
import com.hhjt.hmp.chatserver.queues.Client;
import com.hhjt.hmp.chatserver.queues.ClientQueue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by paul on 2015/1/4.
 */
public class SocketSender extends Thread{
    private static Logger logger = Logger.getLogger(SocketSender.class);
    public volatile boolean exit = false;
    private Socket socket;
    private String number;
    public SocketSender(Socket socket, String number){
        this.socket = socket;
        this.number = number;
        logger.debug(number+"发送线程开启");
    }

    @Override
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            Response data;
            while (!exit){
                if (ClientQueue.isContain(number)){
//                    if ((data= ClientQueue.getMessage(number))!=null){
                    if (!(ClientQueue.isEmpty(number))){
//                    logger.debug(number+"队列暂无消息，等待。。。");
//                        data= ClientMessageQueue.getMessage(number);
//                        logger.debug("进入发送进程");
                        if (!socket.isClosed()){
                            data = ClientQueue.getMessage(number);
                            outputStream.write(data.pack());
                            logger.debug(number+"发送消息成功");
//                            ClientQueue.addMessage(number,data);
                        }else {

                        }
                    }
                }else{
                    logger.info(number+"队列已删除send线程正常退出"+socket);
                    break;
                }

            }
            logger.info(number+"socket已关闭，sender线程退出"+socket);

        } catch (IOException e) {
            logger.error(number+"send线程异常退出"+socket);
            e.printStackTrace();
        }
    }
}
