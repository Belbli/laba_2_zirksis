package com.github.belbli.client;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;
import com.github.belbli.dto.MessageType;

import java.net.InetSocketAddress;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        Thread.sleep(1500);
        Client localhost = new Client(new InetSocketAddress("localhost", 5454));
        localhost.connect();
        FileRequest request = new FileRequest("E:\\labs\\6sem\\Zirksis\\laba_2\\dataForTest",
                ".txt", MessageType.INFO);
        FileRequestResponse response = localhost.sendInfoMessage(
                request
        );
        request.setType(MessageType.DOWNLOAD);

        localhost.connect();
        localhost.downloadFiles(request, response);


        System.out.println(response);
    }


}
