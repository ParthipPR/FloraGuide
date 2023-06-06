package com.example.FloraGuide;

import android.content.Context;

import com.example.FloraGuide.R;

import java.util.List;

public class addPlantSpinnerOptionHandler {

    private Context context;
    private List<Item> itemList;
    private CustomListAdapter adapter;

    public addPlantSpinnerOptionHandler(Context context, List<Item> itemList, CustomListAdapter adapter) {
        this.context = context;
        this.itemList = itemList;
        this.adapter = adapter;
    }

    public void handleOption(String selectedOption) {
        switch (selectedOption) {
            case "Carrot":
                itemList.add(new Item(R.drawable.carrot, "Carrot", "Description for Carrot", "imageUrl"));
                break;
            case "Tomato":
                itemList.add(new Item(R.drawable.tomato, "Tomato", "Description for Tomato", "imageUrl"));
                break;
            case "Green Chilli":
                itemList.add(new Item(R.drawable.greenchilli, "Green Chilli", "Description for Green Chilli", "imageUrl"));
                break;
            default:
                // Handle unknown option or add appropriate behavior
                break;
        }
    }

}
