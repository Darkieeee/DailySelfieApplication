package com.example.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import java.io.IOException;

public class ImageHelper {

    public final static int DEFAULT_TARGET_WIDTH = 150;
    public final static int DEFAULT_TARGET_HEIGHT = 120;

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap scaleBitmap(String imagePath, int targetW, int targetH) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(imagePath, bmOptions);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(bmOptions, targetW, targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inTargetDensity = 1;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        return bitmap;
    }

    public static Bitmap scaleBitmap(String imagePath) {
        return ImageHelper.scaleBitmap(imagePath,
                                       ImageHelper.DEFAULT_TARGET_WIDTH,
                                       ImageHelper.DEFAULT_TARGET_HEIGHT);
    }

    public static Bitmap scaleBitmap(String imagePath, @NonNull ImageView view) {
        int targetW = view.getWidth();
        int targetH = view.getHeight();
        if (targetW == 0)
            targetW = DEFAULT_TARGET_WIDTH;
        if (targetH == 0)
            targetH = DEFAULT_TARGET_HEIGHT;
        return ImageHelper.scaleBitmap(imagePath, targetW, targetH);
    }

    public static Bitmap toLandscape(String photoPath, int targetWidth, int targetHeight) {
        /*
         *  Fix wrong image rotation
         *  Reference: https://stackoverflow.com/a/14066265
         */
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                                 ExifInterface.ORIENTATION_UNDEFINED);
            Bitmap bitmap = ImageHelper.scaleBitmap(photoPath, targetWidth, targetHeight);
            Bitmap rotatedBitmap;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;

            }
            return rotatedBitmap;
        } catch (IOException ioException) {
            return null;
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,
                                   0,
                                   0,
                                   source.getWidth(),
                                   source.getHeight(),
                                   matrix,
                                   true);
    }
}
