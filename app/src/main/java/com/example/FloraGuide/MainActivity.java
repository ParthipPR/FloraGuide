package com.example.FloraGuide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.plant_monitor.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add_plantFab, remove_plantFab;
    ExtendedFloatingActionButton menuFab;
    TextView add_plantText, remove_plantText;
    List<Item> itemList = new ArrayList<>();
    RelativeLayout layout;
    //To check whether the sub fab is visible or not
    boolean isAllFabVisible;

    // Create an instance of the custom adapter and set it as the adapter for the ListView
    CustomListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Adding custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.icon); // Set the logo image
        // Apply custom text style with Bradley Hand ITC font
        toolbar.setTitleTextAppearance(this, R.style.CustomTextStyle);

        // Get the ListView from the layout
        ListView listView = findViewById(R.id.listview);

        // Create an instance of the custom adapter and set it as the adapter for the ListView
        adapter = new CustomListAdapter(this, itemList);
        listView.setAdapter(adapter);

        add_plantFab = findViewById(R.id.add_plant);
        remove_plantFab = findViewById(R.id.remove_plant);
        menuFab = findViewById(R.id.menu);

        add_plantText = findViewById(R.id.add_plant_text);
        remove_plantText = findViewById(R.id.remove_plant_text);

        add_plantFab.setVisibility(View.GONE);
        remove_plantFab.setVisibility(View.GONE);
        add_plantText.setVisibility(View.GONE);
        remove_plantText.setVisibility(View.GONE);

        isAllFabVisible = false;
        menuFab.shrink();


        //menu Click listener
        menuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabVisible) {
                    add_plantFab.show();
                    remove_plantFab.show();
                    add_plantText.setVisibility(View.VISIBLE);
                    remove_plantText.setVisibility(View.VISIBLE);

                    //extend menuFAB
                    menuFab.extend();
                    isAllFabVisible = true;
                }
                else {
                    add_plantFab.hide();
                    remove_plantFab.hide();
                    add_plantText.setVisibility(View.GONE);
                    remove_plantText.setVisibility(View.GONE);

                    menuFab.shrink();
                    isAllFabVisible = false;
                    }

            }
        });

        add_plantFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddplantPopupWindow();



            }
        });

        remove_plantFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item itemToRemove = null;
                for (Item item : itemList) {
                    if (item.getTitle().equals("Carrot") ) {
                        itemToRemove = item;
                        break;
                    }
                }
                if (itemToRemove != null) {
                    itemList.remove(itemToRemove);
                    adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
                    Toast.makeText(MainActivity.this, "Plant Removed", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void createAddplantPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.addplantpopup, null);

        // After inflating the popUpView in the createPopupWindow() method

        Spinner spinner = popUpView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.plant_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);


        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUpView,width,height,focusable);
        ViewGroup layout = findViewById(android.R.id.content);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.BOTTOM,0,0);

            }
        });
        TextView Skip ,Add ;
        Skip=popUpView.findViewById(R.id.Skip);
        Add=popUpView.findViewById(R.id.Add);
        Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Add plants
                String selectedPlant = spinner.getSelectedItem().toString();
                addPlantSpinnerOptionHandler optionHandler = new addPlantSpinnerOptionHandler(MainActivity.this, itemList, adapter);
                optionHandler.handleOption(selectedPlant);
                adapter.notifyDataSetChanged();
                //Toast.makeText(MainActivity.this, "Plant Added", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
        // and if you want to close popup when touch Screen
        popUpView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }


        });


    }
}