package com.example.dailyselfie;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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

}
