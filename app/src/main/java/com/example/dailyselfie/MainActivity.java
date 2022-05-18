package com.example.dailyselfie;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Author Darkieeeee
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    ActivityResultLauncher<Intent> startActivityForResult;
    ListView imageList;
    CustomImageList customImageList;

    private String currentPhotoPath;
    private String imageFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageList = findViewById(R.id.imageList);
        loadImages();
        registerActivityResult();
    }

    @Override
    protected void onStart() {
         super.onStart();
         Log.d(TAG, "on Start");
         Context context = getApplicationContext();

         //Cancel the alarm when we are in app
         Intent broadcastIntent = new Intent(context, DailySelfieReceiver.class);
         PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                                 0,
                                                                 broadcastIntent,
                                                                 PendingIntent.FLAG_NO_CREATE);
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         if (alarmManager != null && pendingIntent != null) {
             alarmManager.cancel(pendingIntent);
         }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "on Stop");
        Context context = getApplicationContext();
        createAlarm(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on Destroy");
    }

    private void loadImages() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(storageDir != null && storageDir.length() > 0) {
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
            customImageList = new CustomImageList(MainActivity.this, imageItems);
            imageList.setAdapter(customImageList);
            imageList.setOnItemClickListener((parent, view, position, id) -> {

                SelfieImage currentPic = (SelfieImage) parent.getItemAtPosition(position);
                Intent displayImageIntent = new Intent(this, FullScreenImageActivity.class);
                displayImageIntent.putExtra("photo_path", currentPic.getImagePath());
                startActivity(displayImageIntent);

            });
            imageList.setOnItemLongClickListener((parent, view, position, id) -> {
                SelfieImage currentPic = (SelfieImage) parent.getItemAtPosition(position);
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:

                                // When the user selects "Yes", the photo file will be deleted. But I will move it to the "image_deleted" directory
                                // in order to read it when needed
                                File photo = new File(currentPic.getImagePath());
                                File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                                                     + AppConstants.Directory.DEFAULT_IMAGE_DELETED_DIRECTORY);

                                //Variable "deleted" that checks if the file has been moved successfully or not
                                boolean deleted = moveFile(photo, dir);
                                if(deleted) {
                                    customImageList.removeItemAt(position);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Couldn't delete image file", Toast.LENGTH_SHORT)
                                         .show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("Are you sure to delete this image?")
                           .setPositiveButton("Yes", dialogClickListener)
                           .setNegativeButton("No" , dialogClickListener)
                           .show();
                return true;
            });
        }
    }

    private void registerActivityResult() {
        startActivityForResult = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult(), (result) -> {
                int resultCode = result.getResultCode();
                switch(resultCode) {
                    case RESULT_OK: {
                        File photoFile = new File(currentPhotoPath);
                        if(photoFile.exists()) {
                            File renamedFile = new File(photoFile.getParent(),
                                                        imageFileName.concat(AppConstants.ImageFile.JPG_SUFFIX));
                            boolean fileRenamed = photoFile.renameTo(renamedFile);
                            if (fileRenamed) {
                                SelfieImage selfieImage = new SelfieImage(renamedFile);
                                customImageList.addItem(selfieImage);
                            }
                            else {
                                boolean fileDeleted = photoFile.delete();
                                if (!fileDeleted) {
                                    Log.e(TAG, "Couldn't delete file " + photoFile.getName() + " in path " + photoFile.getPath());
                                }
                            }
                        }
                        break;
                    }
                    case RESULT_CANCELED: {
                        File photoFile = new File(currentPhotoPath);
                        boolean fileDeleted = photoFile.delete();
                        if (!fileDeleted) {
                            Log.e(TAG, "Couldn't delete file " + photoFile.getName() + " in path " + photoFile.getPath());
                        }
                        break;
                    }
                }
            }
        );
    }

    private File createImageFile() {
        File image = null;
        try {
            // Create an image file name
            imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,
                    AppConstants.ImageFile.JPG_SUFFIX,
                    storageDir
            );

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.getAbsolutePath();
        } catch (java.io.IOException ioException) {
            ioException.printStackTrace();
        }
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.camera:
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePhoto.resolveActivity(getPackageManager()) != null) {
                    File photoFile = createImageFile();
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                                                  "com.example.android.fileprovider",
                                                                   photoFile);
                        takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult.launch(takePhoto);
                    }
                }
                break;
            case R.id.recentlyDeletedPhoto:
                Intent intent = new Intent(getApplicationContext(), PhotosDeletedActivity.class);
                startActivity(intent);
                break;
            case R.id.close:
                finishAndRemoveTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Move file from current directory to another directory
     * @param f   the file want to move
     * @param dir the directory for move progress
     * @return    true if move the file successfully to another directory.
     *            Otherwise return false.
     */
    private boolean moveFile(File f, @NonNull File dir) {
        if(!dir.exists()) {
            boolean success = dir.mkdir();
            if(!success) {
                return false;
            }
        }
        return f.renameTo(new File(dir.getPath() + "/" + f.getName()));
    }

    private void createAlarm(Context context) {
        Intent broadcastIntent = new Intent(context, DailySelfieReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                                0,
                                                                 broadcastIntent,
                                                                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
               Since API level 19 (Android Marshmello 6.0), setRepeating() will repeat inexactly time
               because of the Doze mode. So in this case, I use setInexactRepeating() instead
               Reference: https://developer.android.com/reference/android/app/AlarmManager#setRepeating(int,%20long,%20long,%20android.app.PendingIntent)
             */
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                                             System.currentTimeMillis() + AppConstants.Alarm.INTERVAL_TWO_MINUTES_CLOCK,
                                             AppConstants.Alarm.INTERVAL_TWO_MINUTES_CLOCK,
                                             pendingIntent);
        }
        else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                                      System.currentTimeMillis() + AppConstants.Alarm.INTERVAL_TWO_MINUTES_CLOCK,
                                      AppConstants.Alarm.INTERVAL_TWO_MINUTES_CLOCK,
                                      pendingIntent);
        }
    }
}