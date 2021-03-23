package com.github.belbli.dto;

import java.io.Serializable;

public class FileRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dir;
    private String extension;

    public FileRequest(String dir, String extension) {
        this.dir = dir;
        this.extension = extension;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "FileRequest{" +
                "dir='" + dir + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }

    public String getDir() {
        return dir;
    }

    public String getExtension() {
        return extension;
    }
}
