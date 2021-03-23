package com.github.belbli.sender;

import com.github.belbli.dto.FileRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileSender {
    ByteBuffer buffer = ByteBuffer.allocate(0);

    public void sendFiles(SocketChannel channel, FileRequest request) throws IOException {
        File dir = new File(request.getDir());

        List<File> collect = Arrays.stream(
                dir.listFiles((dir1, name) -> name.endsWith(request.getExtension()))
        ).collect(Collectors.toList());

        collect.forEach(this::writeOne);

        System.out.println("buffer without flip : " + new String(buffer.array()));
        buffer.flip();
        System.out.println("buffer after flip : " + new String(buffer.array()));
        channel.write(buffer);
        buffer = ByteBuffer.allocate(0);
        buffer.clear();
    }

    private void writeOne(File file) {
        try {
            RandomAccessFile randomAccessFile =
                    new RandomAccessFile(file.getAbsoluteFile(), "rw");

            FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer allocate = ByteBuffer.allocate(buffer.capacity() + Math.toIntExact(fileChannel.size()));
            buffer.flip();
            allocate.put(buffer);
            buffer = allocate;
            ByteBuffer byteBuffer = ByteBuffer.allocate(Math.toIntExact(fileChannel.size()));
            Charset charset = Charset.forName("US-ASCII");
            while (fileChannel.read(byteBuffer) > 0) {
                byteBuffer.flip();
                buffer.put(byteBuffer);
                byteBuffer.clear();
            }
            fileChannel.close();
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
