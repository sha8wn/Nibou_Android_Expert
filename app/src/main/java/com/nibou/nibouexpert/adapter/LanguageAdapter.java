package com.nibou.nibouexpert.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.nibou.nibouexpert.models.LanguageModel;

import java.util.List;

public class LanguageAdapter extends ArrayAdapter<LanguageModel> {

        private LayoutInflater inflater;

        public LanguageAdapter(Context context, List<LanguageModel> data){
            super(context, 0, data);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
//            ViewHolder viewHolder;
//            if (view == null) {
//                view = inflater.inflate(  R.layout.custom_row_layout_design, null);
//                viewHolder = new ViewHolder(view);
//                view.setTag(viewHolder);
//            }
//            else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            //Retrieve your object
//            YourClassData data = (YourClassData) getItem(position);
//
//            viewHolder.txt.setTypeface(m_Font);
//            viewHolder.txt.setText(data.text);
//            viewHolder.img.setImageBitmap(BitmapFactory.decodeFile(data.imageAddr));

            return view;

        }

        private class ViewHolder
        {
//            private final TextView txt;
//            private final ImageView img;
//
//            private ViewHolder(View view)
//            {
//                txt = (TextView) view.findViewById(R.id.txt);
//                img = (ImageView) view.findViewById(R.id.img);
//            }
        }
    }