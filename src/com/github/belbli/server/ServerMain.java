package com.github.belbli.server;

import java.net.InetSocketAddress;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        new Server(new InetSocketAddress("localhost", 5454));
    }
}
