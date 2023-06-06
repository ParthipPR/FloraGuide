package com.example.FloraGuide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import com.bumptech.glide.Glide;


public class CustomListAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> itemList;

    public CustomListAdapter(Context context, List<Item> itemList) {
        super(context, 0, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.item_image);
        TextView itemTitle = convertView.findViewById(R.id.item_title);
        TextView itemDescription = convertView.findViewById(R.id.item_description);

        Item currentItem = getItem(position);
        if (currentItem != null) {
            // Use imageUrl instead of imageResId
            String imageUrl = currentItem.getImageUrl();
            // Load the image using your preferred image loading library
            // For example, using Glide:
            Glide.with(context).load(imageUrl).into(itemImage);

            itemTitle.setText(currentItem.getTitle());
            itemDescription.setText(currentItem.getDescription());
        }

        return convertView;
    }
}
