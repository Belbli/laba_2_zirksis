package com.github.belbli.handler;

import com.github.belbli.dto.FileRequest;
import com.github.belbli.dto.FileRequestResponse;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class MessageHandler {
    public FileRequestResponse handle(FileRequest request) {
        File dir = new File(request.getDir());
        LinkedHashMap<String, Long> filesInfo = new LinkedHashMap<>();
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(request.getExtension()));
        Arrays.asList(files).forEach(file -> filesInfo.put(file.getName(), file.length()));
        return new FileRequestResponse(filesInfo);
    }
}
