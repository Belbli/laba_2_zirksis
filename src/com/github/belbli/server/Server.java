package com.github.belbli.server;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;
import com.github.belbli.dto.MessageType;
import com.github.belbli.handler.MessageHandler;
import com.github.belbli.receiver.MessageDeserializer;
import com.github.belbli.sender.FileSender;
import com.github.belbli.sender.MessageSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class Server {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private FileSender fileSender = new FileSender();
    private MessageHandler messageHandler = new MessageHandler();
    private MessageDeserializer<FileRequest> deserializer = new MessageDeserializer<>();
    private MessageSerializer<FileRequestResponse> serializer = new MessageSerializer<>();

    public Server(InetSocketAddress address) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(address);
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("waiting for connection...");
        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    receive(key);
                }
                iter.remove();
            }
        }
    }

    private void receive(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        ByteBuffer buffer = readFromSocket(client);

        FileRequest deserialize = deserializer.deserialize(buffer);

        if (deserialize.getType() == MessageType.INFO) {
            FileRequestResponse fileRequestResponse = messageHandler.handle(deserialize);

            System.out.println("received : " + deserialize);
            buffer.clear();
            buffer.flip();
            client.write(serializer.serialize(fileRequestResponse));
            buffer.clear();
        } else {
            fileSender.sendFiles(client, deserialize);
        }


        client.close();
    }

    private void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        SocketChannel client = serverSocket.accept();
        logger.info("connected from : " + client.getRemoteAddress());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

    private ByteBuffer readFromSocket(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 32);
        int bytesRead = channel.read(buffer); //read into buffer.
        while (bytesRead > 0) {

            buffer.flip();  //make buffer ready for read

            while (buffer.hasRemaining()) {
                buffer.get();
                //System.out.print((char) buffer.get()); // read 1 byte at a time
            }

            buffer.clear(); //make buffer ready for writing
            bytesRead = channel.read(buffer);
        }
        return buffer;
    }
}
