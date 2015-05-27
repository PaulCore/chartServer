package com.hhjt.hmp.chatserver.entity;

import com.hhjt.hmp.chatserver.socketHandler.SocketReceiver;

/**
 * Created by paul on 2015/5/21.
 */
public class Test extends RequestMessage {
    public Test(String command, byte[] data, SocketReceiver receiver) {
        super(command, data, receiver);
    }

    @Override
    public void unpackage() {

    }
}
