package com.hhjt.hmp.chatserver;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by paul on 2015/1/13.
 */
public class Writer extends Thread {
    private Socket socket;
    public Writer(Socket socket){
        this.socket = socket;
        this.start();
    }
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            socket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
