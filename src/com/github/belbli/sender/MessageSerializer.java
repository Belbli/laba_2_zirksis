package com.github.belbli.sender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class MessageSerializer<T extends Serializable> {
    public ByteBuffer serialize(T message) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(message);
                return ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
