package com.example.dailyselfie;

import java.io.File;

public class SelfieImage {

    private File imageFile;

    public SelfieImage(File file) {
        this.imageFile = file;
    }

    public String getName() {
        return imageFile.getName();
    }

    public String getImagePath() {
        return imageFile.getPath();
    }

    public boolean isNotImage() {
        return (!imageFile.canRead() || imageFile.isDirectory() || !imageFile.exists());
    }
}
