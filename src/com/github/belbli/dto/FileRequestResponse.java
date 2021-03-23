package com.github.belbli.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

public class FileRequestResponse implements Serializable {
    static final long serialVersionUID = 2L;

    private Map<String, Long> filesInfo;

    public FileRequestResponse(Map<String, Long> filesInfo) {
        this.filesInfo = filesInfo;
    }

    @Override
    public String toString() {
        return filesInfo.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining("; "));
    }

    public Map<String, Long> getFilesInfo() {
        return filesInfo;
    }
}
