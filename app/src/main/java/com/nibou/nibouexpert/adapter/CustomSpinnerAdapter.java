package com.nibou.nibouexpert.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.nibou.nibouexpert.R;

public class CustomSpinnerAdapter  extends BaseAdapter implements SpinnerAdapter {

    String[] company;
    Context context;
    String[] colors = {"#13edea","#e20ecd","#15ea0d"};
    String[] colorsback = {"#FF000000","#FFF5F1EC","#ea950d"};

    public CustomSpinnerAdapter(Context context, String[] company) {
        this.company = company;
        this.context = context;
    }

    @Override
    public int getCount() {
        return company.length;
    }

    @Override
    public Object getItem(int position) {
        return company[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view =  View.inflate(context, R.layout.spinner_item, null);
        TextView textView = (TextView) view.findViewById(R.id.item);
        textView.setText(company[position]);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view;
        view =  View.inflate(context, R.layout.spinner_item, null);
        final TextView textView = (TextView) view.findViewById(R.id.item);
        textView.setText(company[position]);

        if (position == 0) {
            textView.setTextColor(Color.LTGRAY);
        } else {
            textView.setTextColor(Color.BLACK);
        }


        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position==0){
            return false;
        }else {
            return true;
        }
    }
}