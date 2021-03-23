package com.github.belbli.client;

import com.github.belbli.dto.FileRequest;

import java.net.InetSocketAddress;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        Client localhost = new Client(new InetSocketAddress("localhost", 5454));
        String s = localhost.sendMessage(
                new FileRequest("E:\\labs\\6sem\\Zirksis\\laba_2\\dataForTest",
                        ".txt")
        );
        //localhost.stop();
        System.out.println(s);
    }
}
