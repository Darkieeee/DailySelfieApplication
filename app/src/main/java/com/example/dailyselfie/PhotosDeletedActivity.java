package com.example.dailyselfie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PhotosDeletedActivity extends AppCompatActivity {

    ListView imageList;
    CustomImageList customImageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageList = findViewById(R.id.imageList);
        loadImages();
    }

    private void loadImages() {
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                   + AppConstants.Directory.DEFAULT_IMAGE_DELETED_DIRECTORY);
        if(storageDir.exists() && storageDir.length() > 0) {
            File[] images = storageDir.listFiles(fileFilter -> (
                               fileFilter.getName().endsWith(AppConstants.ImageFile.JPG_SUFFIX)
                            || fileFilter.getName().endsWith(AppConstants.ImageFile.PNG_SUFFIX)));
            ArrayList<SelfieImage> imageItems = new ArrayList<>();
            if (images != null) {
                for (File image : images) {
                    if (image != null) {
                        SelfieImage selfieImage = new SelfieImage(image);
                        imageItems.add(selfieImage);
                    }
                }
            }
            customImageList = new CustomImageList(PhotosDeletedActivity.this, imageItems);
            imageList.setAdapter(customImageList);
            imageList.setOnItemClickListener((parent, view, position, id) -> {

                SelfieImage currentPic = (SelfieImage) parent.getItemAtPosition(position);
                Intent displayImageIntent = new Intent(this, FullScreenImageActivity.class);
                displayImageIntent.putExtra("photo_path", currentPic.getImagePath());
                startActivity(displayImageIntent);

            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Intent goBack = new Intent(getApplicationContext(), MainActivity.class);
            goBack.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(goBack);
        }
        return super.onOptionsItemSelected(item);
    }

}
