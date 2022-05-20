package com.example.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView fullScreenImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImage = new ImageView(this);
        setContentView(fullScreenImage);
        String photoPath = getIntent().getStringExtra("photo_path");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bitmap bitmap = ImageHelper.toLandscape(photoPath,
                                                500,
                                                500);
        fullScreenImage.setImageBitmap(bitmap);
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
