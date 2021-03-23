package com.github.belbli.client;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;
import com.github.belbli.receiver.MessageDeserializer;
import com.github.belbli.sender.MessageSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class Client {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private ByteBuffer buffer;
    private SocketChannel channel;
    private InetSocketAddress address;
    private MessageSerializer<FileRequest> serializer = new MessageSerializer<>();
    private MessageDeserializer<FileRequestResponse> deserializer = new MessageDeserializer<>();

    public void stop() throws IOException {
        channel.close();
        buffer = null;
    }

    public Client(InetSocketAddress address) {
        this.address = address;
        this.buffer = ByteBuffer.allocate(1024 * 32);
    }

    public void connect() {
        try {
            channel = SocketChannel.open(address);
            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileRequestResponse sendInfoMessage(FileRequest request) {
        buffer = serializer.serialize(request);
        try {
            channel.write(buffer);
            buffer.clear();

            buffer = readFromSocket(channel, 1024 * 8);

            FileRequestResponse response = deserializer.deserialize(buffer);
            logger.info("received from server : " + response);

            buffer.clear();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void downloadFiles(FileRequest request, FileRequestResponse response) throws IOException {
        buffer = serializer.serialize(request);
        channel.write(buffer);
        buffer.clear();

        response.getFilesInfo().entrySet().stream()
                .forEach((file) -> {
                    ByteBuffer buffer = readFromSocket(channel, Math.toIntExact(file.getValue()));
                    fileChannelWrite(buffer, file.getKey());
                });
    }

    private ByteBuffer readFromSocket(SocketChannel channel, int bufferSize) {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        try {
            int bytesRead = channel.read(buffer); //read into buffer.
            while (bytesRead != -1) {

                //buffer.flip();  //make buffer ready for read
                if (bytesRead == bufferSize) {
                    System.out.println("all file data was read");
                    break;
                }
                buffer.clear(); //make buffer ready for writing

                bytesRead = channel.read(buffer);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buffer;
    }

    public void fileChannelWrite(ByteBuffer byteBuffer, String filename) {
        filename += System.currentTimeMillis();
        System.out.println("writing into file : " + new String(byteBuffer.array()));
        try (FileChannel fc = new FileOutputStream(filename).getChannel()) {
            byteBuffer.flip();
            fc.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
