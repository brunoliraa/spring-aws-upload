package com.br.springawsuploadimage.bucket;

public enum Bucket {

    UPLOAD_IMAGE("nome-bucket");

    private final String name;

    Bucket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
