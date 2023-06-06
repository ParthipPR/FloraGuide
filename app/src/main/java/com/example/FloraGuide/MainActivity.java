package com.example.FloraGuide;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton add_plantFab, remove_plantFab;
    ExtendedFloatingActionButton menuFab;
    TextView add_plantText, remove_plantText;
    List<Item> itemList = new ArrayList<>();
    RelativeLayout layout;
    Handler handler = new Handler();

    //To check whether the sub fab is visible or not
    boolean isAllFabVisible;

    // Create an instance of the custom adapter and set it as the adapter for the ListView
    CustomListAdapter adapter;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;


    private plantNetApiClient plantNetApiClient; // Instance of the plantNetApiClient

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check and request camera permission
        if (!checkCameraPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        // Check and request read storage permission
        if (!checkReadStoragePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
        }
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



        // Initialize the camera launcher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bundle extras = data.getExtras();
                                if (extras != null) {
                                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                                    String imageFilePath = saveImageToFile(imageBitmap);
                                    identifyPlant(imageFilePath);
                                }
                            }
                        }
                    }
                });

        // Initialize the gallery launcher
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Uri imageUri = data.getData();
                                if (imageUri != null) {
                                    String imageFilePath = getImageFilePath(imageUri);
                                    identifyPlant(imageFilePath);
                                }
                            }
                        }
                    }
                });
        // Initialize the plantNetApiClient
        plantNetApiClient = new plantNetApiClient();

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
                } else {
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
                    if (item.getTitle().equals("Carrot")) {
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

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 1002;

// ...

    private boolean checkCameraPermission() {
        int cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private boolean checkReadStoragePermission() {
        int readStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with camera action
                // For example, you can show the camera intent here
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable camera functionality)
                Toast.makeText(MainActivity.this, "Camera permission denied " , Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with gallery action
                // For example, you can show the gallery intent here
            } else {
                // Permission denied, handle accordingly (e.g., show a message or disable gallery functionality)
                Toast.makeText(MainActivity.this, "Gallery permission denied " , Toast.LENGTH_SHORT).show();
            }
        }
    }


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;

    private void createAddplantPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.addplantpopup, null);

        // After inflating the popUpView in the createPopupWindow() method


        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        PopupWindow popupWindow = new PopupWindow(popUpView, width, height, focusable);
        ViewGroup layout = findViewById(android.R.id.content);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.TOP, 0, 0);
            }
        });
        TextView Gallery, Camera;
        Gallery = popUpView.findViewById(R.id.Gallery);
        Camera = popUpView.findViewById(R.id.Camera);
        Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkReadStoragePermission()) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (galleryIntent.resolveActivity(getPackageManager()) != null) {
                        galleryLauncher.launch(galleryIntent);
                    }
                }
                popupWindow.dismiss();
            }
        });
        Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkCameraPermission()) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        cameraLauncher.launch(cameraIntent);
                    }
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    // Save the image to a file or process it directly
                    // For simplicity, we assume the image is saved as "image.jpg"
                    String imageFilePath = saveImageToFile(imageBitmap);
                    Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
                    identifyPlant(imageFilePath);
                }
            } else if (requestCode == REQUEST_IMAGE_GALLERY && data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    // Get the image file path from the image URI
                    String imageFilePath = getImageFilePath(imageUri);
                    identifyPlant(imageFilePath);
                }
            }
        }
    }


    private String saveImageToFile(Bitmap imageBitmap) {
        // Save the image bitmap to a file and return the file path
        File imagesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (imagesDir != null) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            File imageFile = new File(imagesDir, imageFileName);
            try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                return imageFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getImageFilePath(Uri imageUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }

    private void identifyPlant(String imageFilePath) {
        if (imageFilePath != null) {
            // Identify the plant using plantNetApiClient
            plantNetApiClient.identifyPlant(imageFilePath, new plantNetApiClient.PlantIdentificationListener() {

                public void onPlantIdentificationSuccess(List<PlantNetResponse> responses) {
                    // Check if the responses list is null
                    if (responses != null && !responses.isEmpty()) {
                        // Assuming you want to access the first response in the list
                        PlantNetResponse firstResponse = responses.get(0);
                        String scientificName = firstResponse.getScientificName();
                        String commonName = firstResponse.getCommonName();
                        String imageUrl = firstResponse.getImageUrl();

                        // Show toast message on the main/UI thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Plant identified: " + scientificName, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Create a new Item based on the identified plant
                        Item newItem = new Item(0, scientificName, commonName, imageUrl);

                        // Add the new item to the list and update the adapter
                        itemList.add(newItem);
                        adapter.notifyDataSetChanged();
                    }
                }


                private void downloadAndDisplayImage(String imageUrl, String scientificName, String commonName) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(imageUrl)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle image download failure
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                // Convert the response body to a byte array
                                byte[] imageBytes = response.body().bytes();

                                // Decode the byte array to a Bitmap
                                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                // Convert Bitmap to a base64 encoded string
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Add the identified plant to the list with the downloaded image
                                        Item newItem = new Item(0,scientificName, commonName, encodedImage);
                                        itemList.add(newItem);
                                        adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
                                        Toast.makeText(MainActivity.this, "Plant Added", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // Handle image download failure
                                // You can display a placeholder image or show an error message
                                Toast.makeText(MainActivity.this, "image download failure", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                @Override
                public void onPlantIdentificationSuccess(PlantIdentificationResult result) {

                }

                @Override
                public void onPlantIdentificationFailure(String errorMessage) {
                    // Handle plant identification failure
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Plant identification failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

}

