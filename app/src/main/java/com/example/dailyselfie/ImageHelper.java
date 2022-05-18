package com.example.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.ImageView;

public class ImageHelper {

    public static int DEFAULT_TARGET_WIDTH = 150;
    public static int DEFAULT_TARGET_HEIGHT = 120;

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
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @SuppressWarnings("deprecation")
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
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.setDensity(Bitmap.DENSITY_NONE);

        return rotatedBitmap;
    }

    public static Bitmap scaleBitmap(String imagePath) {
        return ImageHelper.scaleBitmap(imagePath,
                                       ImageHelper.DEFAULT_TARGET_WIDTH,
                                       ImageHelper.DEFAULT_TARGET_HEIGHT);
    }

    public static Bitmap scaleBitmap(String imagePath, ImageView view) {
        int targetW = view.getWidth();
        int targetH = view.getHeight();
        if (targetW == 0)
            targetW = DEFAULT_TARGET_WIDTH;
        if (targetH == 0)
            targetH = DEFAULT_TARGET_HEIGHT;
        return ImageHelper.scaleBitmap(imagePath, targetW, targetH);
    }
}
