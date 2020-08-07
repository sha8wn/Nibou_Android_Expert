package com.nibou.nibouexpert.utils;

public  class AppListner {

    public interface chatListItemClick{

        void onItemClick(int position) ;

    }

    public interface onTimePickerClick{

        void onTimeSelected(int hour , int min) ;
    }
}
