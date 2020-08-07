package com.nibou.nibouexpert.actioncable;

import android.content.Context;
import com.nibou.nibouexpert.api.ApiClient;
import com.nibou.nibouexpert.api.ApiEndPoint;
import com.nibou.nibouexpert.api.ApiHandler;
import com.nibou.nibouexpert.models.MessageModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.LocalPrefences;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.List;

public class MessageApiCall {
    private String taskId;
    private Context context;

    public MessageApiCall(Context context, String taskId) {
        this.taskId = taskId;
        this.context = context;
    }

    public void sendMessageNetworkCall(String room_id, RequestBody text, List<MultipartBody.Part> images, Callback callback) {
        ApiHandler.requestService(context, ApiClient.getClient().create(ApiEndPoint.class).sendMessage(LocalPrefences.getInstance().getString(context, AppConstant.APP_LANGUAGE), AppConstant.BEARER + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken(), room_id, text, images), new ApiHandler.CallBack() {
            @Override
            public void success(boolean isSuccess, Object data) {
                if (isSuccess) {
                    if (callback != null)
                        callback.success(taskId, (MessageModel) data);
                } else {
                    if (callback != null)
                        callback.failed(taskId);
                }
            }

            @Override
            public void failed() {
                if (callback != null)
                    callback.failed(taskId);
            }
        });
    }

    public interface Callback {
        void success(String taskId, MessageModel messageModel);

        void failed(String taskId);
    }
}