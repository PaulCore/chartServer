package com.hhjt.hmp.chatserver;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 * Created by paul on 2015/5/19.
 */
public class Demo {
    public static void main(String[] args) {
//        Random random = new Random();


//        System.out.println(l);

//        System.out.println(s);


        for (int i = 0; i < 2000; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = new Socket("172.16.40.123",8080);
                        long l = (long) (Math.random()*100000000000l);
                        String s = String.format("%011d",l);
                        socket.getOutputStream().write(("##0020CORQ0012"+s+"P\r\n").getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        }
    }
}
