package com.hhjt.hmp.chatserver.queues;

import com.hhjt.hmp.chatserver.entity.RequestMessage;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by paul on 2014/12/30.
 */
public class ServerMessageQueue {
    private static LinkedBlockingQueue<RequestMessage> queue = new LinkedBlockingQueue<RequestMessage>();
    public static RequestMessage getMessage(){
        RequestMessage message = null;
        try {
            message = queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }
    public static boolean addMessage(RequestMessage message){
        return queue.offer(message);
    }
    public static int size(){
        return queue.size();
    }

//   public static void  main(String[] args){
//       byte[] bytes = {0,1,2,3,4,5,6,7,8,9};
//       byte[] b = Arrays.copyOfRange(bytes,0,3);
//       System.out.print(Arrays.toString(b));

//        LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
//      byte[] b = queue.poll();
//       System.out.print(b==null);
//        queue.offer("a");
//        queue.offer("b");
//        queue.offer("c");
//        queue.offer("d");
//        System.out.print(queue);
//        System.out.print(queue.poll());
//        System.out.print(queue);
//    }
}
