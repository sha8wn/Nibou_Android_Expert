package com.nibou.nibouexpert.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookmarkHistoryModel implements Serializable {

    @SerializedName("data")
    private List<MessageModel.Data> data;

    public List<MessageModel.Data> getData() {
        return data;
    }

    public void setData(List<MessageModel.Data> data) {
        this.data = data;
    }
}
