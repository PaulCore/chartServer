package com.hhjt.hmp.chatserver;

import com.hhjt.hmp.chatserver.socketHandler.SocketSender;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by paul on 2015/1/13.
 */
public class Reader extends Thread {
    private Socket socket;
    public Reader(Socket socket){
        this.socket = socket;
        this.start();
    }
    @Override
    public void run() {
        try {
            byte[] b = new byte[100];
            InputStream inputStream = socket.getInputStream();
            while (inputStream.read(b)!=-1){
                System.out.print(Arrays.toString(b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
