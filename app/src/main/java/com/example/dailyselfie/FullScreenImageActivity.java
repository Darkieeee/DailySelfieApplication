package com.example.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView fullScreenImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenImage = new ImageView(this);
        setContentView(fullScreenImage);
        String photoPath = getIntent().getStringExtra("photo_path");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new LoadImage(fullScreenImage).execute(photoPath);
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

    class LoadImage extends AsyncTask<String, Bitmap, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;

        public LoadImage(ImageView imageView) {
            imageViewWeakReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String URI = strings[0];

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap sourceBitmap = BitmapFactory.decodeFile(URI);

            return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            ImageView imageView = imageViewWeakReference.get();
            if(imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
