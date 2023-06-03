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

public class CustomListAdapter extends ArrayAdapter<Item> {

    private Context context;
    private List<Item> itemList;

    public CustomListAdapter(Context context, List<Item> itemList) {
        super(context, 0, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if convertView is null and inflate the layout if needed
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        // Get references to the views in the custom layout
        ImageView itemImage = convertView.findViewById(R.id.item_image);
        TextView itemTitle = convertView.findViewById(R.id.item_title);
        TextView itemDescription = convertView.findViewById(R.id.item_description);

        // Set data for the views based on the item at the current position
        Item currentItem = getItem(position);
        if (currentItem != null) {
            itemImage.setImageResource(currentItem.getImageResId());
            itemTitle.setText(currentItem.getTitle());
            itemDescription.setText(currentItem.getDescription());
        }

        return convertView;
    }
}
