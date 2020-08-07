package com.nibou.nibouexpert.models;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import com.nibou.nibouexpert.utils.DateFormatUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveChatSessionModel implements Serializable {

    public ActiveChatSessionModel() {
        data = new ArrayList<>();
    }

    @SerializedName("data")
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public void sort() {
        if (getData() != null)
            Collections.sort(getData());
    }


    public static class Data implements Serializable, Comparable<Data> {
        @SerializedName("attributes")
        private Attributes attributes;

        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;


        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        @Override
        public int compareTo(@NonNull Data o) {
            try {
                if (o.getAttributes().getLast_message() != null && getAttributes().getLast_message() != null) {
                    if (Long.parseLong(DateFormatUtil.getServerMilliSeconds(o.getAttributes().getLast_message().getData().getAttributes().getCreated_at())) > Long.parseLong(DateFormatUtil.getServerMilliSeconds(getAttributes().getLast_message().getData().getAttributes().getCreated_at()))) {
                        return 1;
                    } else if (Long.parseLong(DateFormatUtil.getServerMilliSeconds(o.getAttributes().getLast_message().getData().getAttributes().getCreated_at())) == Long.parseLong(DateFormatUtil.getServerMilliSeconds(getAttributes().getLast_message().getData().getAttributes().getCreated_at()))) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }
    }

    public class Attributes implements Serializable {
        @SerializedName("users")
        private List<ProfileModel> users;

        @SerializedName("expertises")
        private List<ExpertiseModel> expertises;

        @SerializedName("last_message")
        private MessageModel last_message;

        @SerializedName("is_private")
        private boolean is_private;

        @SerializedName("duration")
        private long duration;

        @SerializedName("active")
        private boolean active;

        public List<ExpertiseModel> getExpertises() {
            return expertises;
        }

        public void setExpertises(List<ExpertiseModel> expertises) {
            this.expertises = expertises;
        }

        public boolean isIs_private() {
            return is_private;
        }

        public void setIs_private(boolean is_private) {
            this.is_private = is_private;
        }

        public List<ProfileModel> getUsers() {
            return users;
        }

        public void setUsers(List<ProfileModel> users) {
            this.users = users;
        }

        public MessageModel getLast_message() {
            return last_message;
        }

        public void setLast_message(MessageModel last_message) {
            this.last_message = last_message;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }

}
