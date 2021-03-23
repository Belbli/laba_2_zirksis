package com.github.belbli.handler;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MessageHandler {
    public FileRequestResponse handle(FileRequest request) {

        File dir = new File(request.getDir());
        return new FileRequestResponse(
                Arrays.stream(
                        dir.listFiles((dir1, name) -> name.endsWith(request.getExtension()))
                ).collect(Collectors.toMap(File::getName, File::length))
        );
    }
}
