package com.nibou.nibouexpert.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExpertiseModel implements Serializable {

    @SerializedName("data")
    private SurveyModel.Data data;

    public SurveyModel.Data getData() {
        return data;
    }

    public void setData(SurveyModel.Data data) {
        this.data = data;
    }
}
