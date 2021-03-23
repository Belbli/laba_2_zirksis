package com.github.belbli.client;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;
import com.github.belbli.receiver.MessageDeserializer;
import com.github.belbli.sender.MessageSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class Client {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private ByteBuffer buffer;
    private SocketChannel channel;
    private MessageSerializer<FileRequest> serializer = new MessageSerializer<>();
    private MessageDeserializer<FileRequestResponse> deserializer = new MessageDeserializer<>();

    public void stop() throws IOException {
        channel.close();
        buffer = null;
    }

    public Client(InetSocketAddress address) {
        try {
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
            buffer = ByteBuffer.allocate(512);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(FileRequest request) {
        buffer = serializer.serialize(request);
        try {
            channel.write(buffer);
            buffer.clear();

            buffer = readFromSocket(channel);
            FileRequestResponse response = deserializer.deserialize(buffer);
            logger.info("received from server : " + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "response";
    }

    private ByteBuffer readFromSocket(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 32);
        int bytesRead = channel.read(buffer); //read into buffer.
        while (bytesRead != -1) {

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
