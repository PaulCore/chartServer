package com.hhjt.hmp.chatserver;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by paul on 2014/12/30.
 */
public class Server {
    private static Logger logger = Logger.getLogger(Server.class);

    private ServerSocket serverSocket;
    public Server(){
        try {
            serverSocket = new ServerSocket(8080);
//            serverSocket = new ServerSocket();
//            InetAddress address = InetAddress.getLocalHost();
//            InetSocketAddress socketAddress = new InetSocketAddress(address,5000);
//            serverSocket.bind(socketAddress);
            logger.info(serverSocket);
//            logger.info(InetAddress.getLocalHost().getHostAddress()+":"+serverSocket.getLocalPort());
            logger.info("服务器启动...");

        } catch (IOException e) {
            logger.error("服务器启动失败");
            e.printStackTrace();
        }
    }

    public void start(){
        try {

            while (true){
                Socket socket = serverSocket.accept();
                logger.info("一个新的socket连接：" + socket);
                new SocketReceiver(socket).start();
            }

        } catch (IOException e) {
            logger.error("服务器start方法内出错");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        //启动处理线程
        new ServerHandler().start();
        new Server().start();

    }
}
