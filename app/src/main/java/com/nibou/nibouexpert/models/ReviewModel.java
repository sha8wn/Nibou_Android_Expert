package com.nibou.nibouexpert.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewModel implements Serializable {

    @SerializedName("data")
    private List<ProfileModel.Data> data;

    @SerializedName("included")
    private List<ProfileModel.Data> included;

    public List<ProfileModel.Data> getData() {
        return data;
    }

    public void setData(List<ProfileModel.Data> data) {
        this.data = data;
    }

    public List<ProfileModel.Data> getIncluded() {
        return included;
    }

    public void setIncluded(List<ProfileModel.Data> included) {
        this.included = included;
    }

    public void reverseReviewList() {
        if (getData() != null)
            Collections.reverse(getData());
    }
}
