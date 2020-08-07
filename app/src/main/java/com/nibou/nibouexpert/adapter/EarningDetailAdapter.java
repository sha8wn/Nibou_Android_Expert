package com.nibou.nibouexpert.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.activitys.EarningDetailsActivity;
import com.nibou.nibouexpert.models.ActiveChatSessionModel;
import com.nibou.nibouexpert.models.PaymentListModel;
import com.nibou.nibouexpert.models.RoomModel;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.DateFormatUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;


public class EarningDetailAdapter extends RecyclerView.Adapter<EarningDetailAdapter.MyViewHolder> {


    private Context context;
    private PaymentListModel paymentListModel;

    public interface PaginationCallback {
        void OnPageClick();
    }

    public EarningDetailAdapter(Context context, PaymentListModel paymentListModel) {
        this.context = context;
        this.paymentListModel = paymentListModel;
    }

    @NonNull
    @Override
    public EarningDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.earning_detail_item, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningDetailAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.date.setText(DateFormatUtil.getRequiredDateFormat(DateFormatUtil.getServerMilliSeconds(paymentListModel.getData().get(i).getAttributes().getCreated_at()), "dd MMMM , yyyy"));
        myViewHolder.amount.setText((AppUtil.getAmountSign(context) + paymentListModel.getData().get(i).getAttributes().getAmount()));
        if (paymentListModel.getData().get(i).getAttributes().getTotalSeconds() != null) {
            myViewHolder.time.setText(((Long.parseLong(paymentListModel.getData().get(i).getAttributes().getTotalSeconds()) % 3600) / 60) + " " + context.getString(R.string.minutes));
        }
        myViewHolder.name.setText(getName(paymentListModel.getData().get(i).getAttributes().getRoom()));
        myViewHolder.room.setText(getRoomName(paymentListModel.getData().get(i).getAttributes().getRoom()));
        myViewHolder.txn_id.setText((paymentListModel.getData().get(i).getId()));
        if (paymentListModel.getData().get(i).getAttributes().isPayed()) {
            myViewHolder.paid_status.setText(context.getString(R.string.paid));
            myViewHolder.paid_status.setTextColor(ContextCompat.getColor(context, R.color.paid_color));
            ViewCompat.setBackgroundTintList(myViewHolder.paid_label, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.paid_color)));
        } else {
            myViewHolder.paid_status.setText(context.getString(R.string.unpaid));
            myViewHolder.paid_status.setTextColor(ContextCompat.getColor(context, R.color.unpaid_color));
            ViewCompat.setBackgroundTintList(myViewHolder.paid_label, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.unpaid_color)));
        }
        if (i == getItemCount() - 1) {
            if (context != null && context instanceof PaginationCallback)
                ((PaginationCallback) context).OnPageClick();
        }
    }

    private String getTransactionStatus() {
        return context.getString(R.string.paid);
    }

    private String getName(RoomModel data) {
        for (int i = 0; i < data.getData().getAttributes().getUsers().size(); i++) {
            if (!(data.getData().getAttributes().getUsers().get(i).getData().getId().equals(LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId()))) {
                return data.getData().getAttributes().getUsers().get(i).getData().getAttributes().getUsername();
            }
        }
        return "";
    }

    private String getRoomName(RoomModel data) {
        String roomname = "";
        for (int i = 0; i < data.getData().getAttributes().getExpertises().size(); i++) {
            if (i == data.getData().getAttributes().getExpertises().size() - 1) {
                roomname = roomname + data.getData().getAttributes().getExpertises().get(i).getData().getAttributes().getTitle();
            } else {
                roomname = roomname + data.getData().getAttributes().getExpertises().get(i).getData().getAttributes().getTitle() + ", ";
            }
        }
        return roomname;
    }


    @Override
    public int getItemCount() {
        if (paymentListModel != null && paymentListModel.getData() != null) {
            return paymentListModel.getData().size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView date, name, amount, room, paid_label, paid_status, txn_id, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            name = itemView.findViewById(R.id.name);
            amount = itemView.findViewById(R.id.amount);
            room = itemView.findViewById(R.id.room);
            txn_id = itemView.findViewById(R.id.txn_id);
            time = itemView.findViewById(R.id.time);
            paid_status = itemView.findViewById(R.id.paid_status);
            paid_label = itemView.findViewById(R.id.paid_label);
        }
    }


}
