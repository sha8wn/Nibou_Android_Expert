package com.nibou.nibouexpert.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProfileModel extends ErrorModel implements Serializable {

    @SerializedName("data")
    private Data data;

    @SerializedName("included")
    private List<Data> included;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<Data> getIncluded() {
        return included;
    }

    public void setIncluded(List<Data> included) {
        this.included = included;
    }

    public class Data implements Serializable {
        @SerializedName("attributes")
        private Attributes attributes;

        @SerializedName("relationships")
        private Relationships relationships;

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

        public Relationships getRelationships() {
            return relationships;
        }

        public void setRelationships(Relationships relationships) {
            this.relationships = relationships;
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

    }


    public class Attributes implements Serializable {

        @SerializedName("code")
        private String code;

        @SerializedName("title")
        private String title;

        @SerializedName("comment")
        private String comment;

        @SerializedName("value")
        private int value;

        @SerializedName("active")
        private String active;

        @SerializedName("country")
        private String country;

        @SerializedName("city")
        private String city;

        @SerializedName("username")
        private String username;

        @SerializedName("gender")
        private String gender;

        @SerializedName("short_bio")
        private String short_bio;

        @SerializedName("account_type")
        private String account_type;

        @SerializedName("timezone")
        private String timezone;

        @SerializedName("nationality")
        private String nationality;

        @SerializedName("dob")
        private String dob;

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("updated_at")
        private String updated_at;

        @SerializedName("email")
        private String email;

        @SerializedName("name")
        private String name;

        @SerializedName("avatar")
        private Avatar avatar;

        @SerializedName("images")
        private List<Avatar> images;

        @SerializedName("pdf")
        private Pdf pdf;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getActive() {
            return active;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getShort_bio() {
            return short_bio;
        }

        public void setShort_bio(String short_bio) {
            this.short_bio = short_bio;
        }

        public String getAccount_type() {
            return account_type;
        }

        public void setAccount_type(String account_type) {
            this.account_type = account_type;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Avatar getAvatar() {
            return avatar;
        }

        public void setAvatar(Avatar avatar) {
            this.avatar = avatar;
        }

        public List<Avatar> getImages() {
            return images;
        }

        public void setImages(List<Avatar> images) {
            this.images = images;
        }

        public Pdf getPdf() {
            return pdf;
        }

        public void setPdf(Pdf pdf) {
            this.pdf = pdf;
        }
    }

    public class Relationships implements Serializable {
        @SerializedName("customer")
        private Customer customer;

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    }

    public class Customer implements Serializable {
        @SerializedName("data")
        private CustomerData data;

        public CustomerData getData() {
            return data;
        }

        public void setData(CustomerData data) {
            this.data = data;
        }
    }

    public class CustomerData implements Serializable {
        @SerializedName("id")
        private String id;

        @SerializedName("type")
        private String type;

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
    }

    public class Avatar implements Serializable {
        @SerializedName("url")
        private String url;

        @SerializedName("w220")
        private W220 w220;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public W220 getW220() {
            return w220;
        }

        public void setW220(W220 w220) {
            this.w220 = w220;
        }

    }

    public class Pdf implements Serializable {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public class W220 implements Serializable {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}




