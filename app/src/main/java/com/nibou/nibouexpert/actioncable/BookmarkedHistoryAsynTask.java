package com.nibou.nibouexpert.actioncable;

import android.content.Context;
import android.os.AsyncTask;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.models.BookmarkHistoryModel;
import com.nibou.nibouexpert.models.MessageHistoryModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.LocalPrefences;

public class BookmarkedHistoryAsynTask extends AsyncTask<Void, Void, Boolean> {
    private MessageHistoryCallback historyCallback;
    private String room_id;
    private Context context;
    private BookmarkHistoryModel messageHistoryModel;

    public interface MessageHistoryCallback {
        void success(BookmarkHistoryModel messageHistoryModel);
    }

    public BookmarkedHistoryAsynTask(Context context, String room_Id, MessageHistoryCallback historyCallback) {
        this.context = context;
        this.room_id = room_Id;
        this.historyCallback = historyCallback;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).getBookmarked(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    messageHistoryModel = (BookmarkHistoryModel) data;
                    onPostExecute(true);
                }
            }

            @Override
            public void failed() {
                onPostExecute(false);
            }
        });
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && historyCallback != null && messageHistoryModel != null && messageHistoryModel.getData().size() > 0) {
            historyCallback.success(messageHistoryModel);
        }
    }
}