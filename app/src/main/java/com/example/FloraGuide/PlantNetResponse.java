package com.example.FloraGuide;

import java.util.logging.Handler;

public class PlantNetResponse {
    private String scientificName;
    private String commonName;
    private String imageUrl;
    //private Handler handler;

    public PlantNetResponse(String scientificName, String commonName, String imageUrl) {
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.imageUrl = imageUrl;

    }

    public String getScientificName() {
        return scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

