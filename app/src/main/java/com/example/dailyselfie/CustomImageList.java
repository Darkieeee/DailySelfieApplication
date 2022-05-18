package com.example.dailyselfie;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class CustomImageList extends BaseAdapter {

    private ArrayList<SelfieImage> images;
    private final Activity activity;
    private LayoutInflater inflater;

    public CustomImageList(Activity activity, ArrayList<SelfieImage> imageItems) {
        images = new ArrayList<>();
        this.activity = activity;
        inflater = LayoutInflater.from(this.activity);
        addItems(imageItems);
    }

    public void addItem(SelfieImage selfieImage) {
        images.add(selfieImage);
        this.notifyDataSetChanged();
    }

    public void removeItemAt(int index) {
        images.remove(index);
        this.notifyDataSetChanged();
    }

    public void addItems(ArrayList<SelfieImage> imageItems) {
        clearAllItems();
        images.addAll(imageItems);
    }

    public void clearAllItems() {
        images.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public SelfieImage getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_image_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            viewHolder.imageDescription = convertView.findViewById(R.id.imageDescription);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        SelfieImage currentPic = images.get(position);
        viewHolder.position = position;
        viewHolder.imageDescription.setText(currentPic.getName());

        Glide.with(activity)
             .load(currentPic.getImagePath())
             .into(viewHolder.imageView);

        return convertView;
    }

    private static class ViewHolder {
        int position;
        ImageView imageView;
        TextView imageDescription;
    }

    class LoadImageTask extends AsyncTask<String, Bitmap, Bitmap> {

        private int position;
        private String path;
        private ViewHolder viewHolder;

        public LoadImageTask(int position, @NonNull ViewHolder viewHolder) {
            super();
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            path = strings[0];
            Bitmap bm = null;
            if (!isCancelled()) {
                if (path != null) {
                    bm = ImageHelper.scaleBitmap(path, ImageHelper.DEFAULT_TARGET_WIDTH, ImageHelper.DEFAULT_TARGET_HEIGHT);
                }
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (viewHolder.position == position) {
                if (result != null) {
                    viewHolder.imageView.setImageBitmap(result);
                }
            }
        }
    }
}
