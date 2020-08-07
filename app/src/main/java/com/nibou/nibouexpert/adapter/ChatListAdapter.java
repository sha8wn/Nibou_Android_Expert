package com.nibou.nibouexpert.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.models.ActiveChatSessionModel;
import com.nibou.nibouexpert.models.ProfileModel;
import com.nibou.nibouexpert.utils.*;

import java.text.SimpleDateFormat;
import java.util.*;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> implements Filterable {

    private AppListner.chatListItemClick chatListItemClickListner;

    private ItemFilter mFilter = new ItemFilter();
    private ActiveChatSessionModel originalData;
    private ActiveChatSessionModel filteredData;

    private Context context;


    public ChatListAdapter(Context context, ActiveChatSessionModel activeChatSessionModel, AppListner.chatListItemClick chatListItemClickListner) {
        this.context = context;
        this.filteredData = activeChatSessionModel;
        this.originalData = activeChatSessionModel;
        this.chatListItemClickListner = chatListItemClickListner;
    }

    public void updateList(ActiveChatSessionModel activeChatSessionModel) {
        this.filteredData = activeChatSessionModel;
        this.originalData = activeChatSessionModel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(getProfileModel(i, filteredData).getData().getAttributes().getUsername());

        if (filteredData.getData().get(i).getAttributes().getLast_message() != null) {

            myViewHolder.time.setText(DateFormatUtil.getLastMessageTime(context, DateFormatUtil.getServerMilliSeconds(filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getCreated_at())));
            if (filteredData.getData().get(i).getAttributes().getLast_message() != null && filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getText() != null && !filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getText().isEmpty()) {
                myViewHolder.message.setText(filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getText());
            } else if (filteredData.getData().get(i).getAttributes().getLast_message() != null && filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getImages() != null && filteredData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getImages().size() > 0) {
                myViewHolder.message.setText(context.getString(R.string.image));
            } else {
                myViewHolder.time.setText("");
                myViewHolder.message.setText("");
            }
        }
        myViewHolder.chat_item.setTag(i);
        myViewHolder.chat_item.setOnClickListener(v -> chatListItemClickListner.onItemClick(Integer.parseInt(v.getTag().toString())));
        if (i == getItemCount() - 1) {
            myViewHolder.line.setVisibility(View.INVISIBLE);
        } else {
            myViewHolder.line.setVisibility(View.VISIBLE);
        }

        if (LocalPrefences.getInstance().getInt(context, filteredData.getData().get(i).getId()) > 0) {
            myViewHolder.message_count.setVisibility(View.VISIBLE);
            myViewHolder.message_count.setText("" + LocalPrefences.getInstance().getInt(context, filteredData.getData().get(i).getId()));
        } else {
            myViewHolder.message_count.setVisibility(View.GONE);
        }

        if (LocalPrefences.getInstance().getBoolean(context, AppConstant.DELAY_ALERT + filteredData.getData().get(i).getId())) {
            myViewHolder.message_count.setBackground(ContextCompat.getDrawable(context, R.drawable.round_background_delay_counter));
        } else {
            myViewHolder.message_count.setBackground(ContextCompat.getDrawable(context, R.drawable.round_background_counter));
        }
    }

    @Override
    public int getItemCount() {
        if (filteredData != null && filteredData.getData() != null && filteredData.getData().size() > 0) {
            return filteredData.getData().size();
        } else {
            return 0;
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, message, message_count, time;
        private LinearLayout chat_item;
        private View line;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            message_count = itemView.findViewById(R.id.message_count);
            time = itemView.findViewById(R.id.time);
            chat_item = itemView.findViewById(R.id.chat_item);
            line = itemView.findViewById(R.id.line);
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint.toString() != null && !constraint.toString().trim().isEmpty()) {
                ActiveChatSessionModel activeChatSessionModel = new ActiveChatSessionModel();
                for (int i = 0; i < originalData.getData().size(); i++) {
                    ProfileModel profileModel = getProfileModel(i, originalData);
                    if (profileModel.getData().getAttributes().getUsername().toLowerCase().startsWith(constraint.toString().toLowerCase()) || constraint.toString().toLowerCase().startsWith(originalData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getText() != null ? originalData.getData().get(i).getAttributes().getLast_message().getData().getAttributes().getText() : "")) {
                        activeChatSessionModel.getData().add(originalData.getData().get(i));
                    }
                }
                results.values = activeChatSessionModel;
                results.count = activeChatSessionModel.getData().size();
            } else {
                results.values = originalData;
                results.count = originalData.getData().size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ActiveChatSessionModel) results.values;
            notifyDataSetChanged();
        }
    }

    private ProfileModel getProfileModel(int pos, ActiveChatSessionModel activeChatSessionModel) {
        for (int i = 0; i < activeChatSessionModel.getData().get(pos).getAttributes().getUsers().size(); i++) {
            if (!activeChatSessionModel.getData().get(pos).getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId())) {
                return activeChatSessionModel.getData().get(pos).getAttributes().getUsers().get(i);
            }
        }
        return null;
    }
}

