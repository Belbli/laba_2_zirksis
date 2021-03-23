package com.github.belbli.receiver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class MessageDeserializer<T extends Serializable> {
    public T deserialize(ByteBuffer data) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data.array())) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                T message = (T) objectInputStream.readObject();
                return message;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
