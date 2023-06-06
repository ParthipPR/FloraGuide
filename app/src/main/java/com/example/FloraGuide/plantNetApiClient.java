package com.example.FloraGuide;

import okhttp3.*;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

public class plantNetApiClient {
    private static final String PLANTNET_API_URL = "https://my-api.plantnet.org/v2/identify/all";

    public interface PlantIdentificationListener {
        void onPlantIdentificationSuccess(PlantIdentificationResult result);
        void onPlantIdentificationFailure(String errorMessage);
    }

    public void identifyPlant(String imageFilePath, PlantIdentificationListener listener) {
        OkHttpClient client = new OkHttpClient();

        // Build the multipart request body with the image file
        MediaType mediaType = MediaType.parse("image/jpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("organs", "leaf")
                .addFormDataPart("images", "image.jpg", RequestBody.create(new File(imageFilePath), mediaType))
                .build();

        // Build the API request
        Request request = new Request.Builder()
                .url(PLANTNET_API_URL)
                .post(requestBody)
                .build();

        // Send the API request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle request failure
                listener.onPlantIdentificationFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Parse the JSON response
                    Gson gson = new Gson();
                    PlantIdentificationResult result = gson.fromJson(response.body().string(), PlantIdentificationResult.class);

                    // Process the identification result
                    listener.onPlantIdentificationSuccess(result);
                } else {
                    // Handle API response error
                    listener.onPlantIdentificationFailure(response.message());
                }
            }
        });
    }
}
