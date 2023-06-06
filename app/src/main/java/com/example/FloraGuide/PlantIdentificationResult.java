package com.example.FloraGuide;

import java.util.List;

public class PlantIdentificationResult {
    private String scientificName;
    private List<String> commonNames;
    private double confidence;
    private String imageUrl;

    // Constructor, getters, and setters

    public String getImageUrl() {
        return imageUrl;
    }

    public PlantIdentificationResult(String scientificName, List<String> commonNames, double confidence) {
        this.scientificName = scientificName;
        this.commonNames = commonNames;
        this.confidence = confidence;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public List<String> getCommonNames() {
        return commonNames;
    }

    public void setCommonNames(List<String> commonNames) {
        this.commonNames = commonNames;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
}
