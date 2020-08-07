package com.nibou.nibouexpert.actioncable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;
import com.nibou.nibouexpert.models.AppSessionModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;
import com.nibou.nibouexpert.utils.TimerUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class ActionSessionHandler {

    private boolean showLog = true;
    private String CHANNEL_NAME = "UserNotificationChannel";
    private String URL = AppConstant.SOCKET_URL;
    private Subscription subscription;
    private Consumer consumer;
    private static ActionSessionHandler actionSessionHandler;
    private Context context;
    private String ONLINE = "online";
    private String OFFLINE = "offline";


    public static ActionSessionHandler getInstance(Context context) {
        if (actionSessionHandler == null) {
            actionSessionHandler = new ActionSessionHandler(context);
        }
        return actionSessionHandler;
    }

    private ActionSessionHandler() {
    }

    private ActionSessionHandler(Context context) {
        this.context = context;
    }

    public void connectWS() {
        URI uri = null;
        try {
            uri = new URI(URL + LocalPrefences.getInstance().getLocalAccessTokenModel(context).getAccessToken());

            Consumer.Options options = new Consumer.Options();
            options.reconnection = true;
            consumer = ActionCable.createConsumer(uri, options);

            Channel channel = new Channel(CHANNEL_NAME);

            subscription = consumer.getSubscriptions().create(channel);
            subscription.onConnected(() -> {
                showLog("Called when the subscription has been successfully completed");
            }).onRejected(() -> {
                showLog("Called when the subscription is rejected by the server");
            }).onReceived(data -> {
                showLog("Called when the subscription receives data from the server" + data.toString());
                try {
                    AppSessionModel sessionTimeoutModel = new Gson().fromJson(data, AppSessionModel.class);
                    handleAction(sessionTimeoutModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).onDisconnected(() -> {
                if (subscription != null) {
                    showLog(OFFLINE + " Action");
                    subscription.perform(OFFLINE);
                }
                showLog("Called when the subscription has been closed");
            }).onFailed(e -> {
                showLog("Called when the subscription encounters any error" + e.getLocalizedMessage());
            });
            consumer.connect();
            running10SecTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnectWS() {
        if (consumer != null)
            consumer.disconnect();
        dismiss10SecTimer();
    }

    private void running10SecTimer() {
        TimerUtil.getTimerUtil().start10SecTimer(new TimerUtil.TimerCallback() {
            @Override
            public void onTimerCompleted() {

            }

            @Override
            public void onTimerTick(String time, long minute, long second) {
                if (subscription != null) {
                    showLog(ONLINE + " Action");
                    subscription.perform(ONLINE);
                }
            }
        });
    }

    private void dismiss10SecTimer() {
        TimerUtil.getTimerUtil().cancel10SecTimer();
    }

    private void handleAction(AppSessionModel sessionTimeoutModel) {
        if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.NEW_MESSAGE_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                if (!LocalPrefences.getInstance().getLocalProfileModel(context).getData().getId().equals(sessionTimeoutModel.getMessage().getData().getAttributes().getFrom_user_id())) {
                    if (!AppConstant.IGNORE_MESSAGE_TEXT.equals(sessionTimeoutModel.getMessage().getText() != null ? sessionTimeoutModel.getMessage().getText() : "")) {
                        LocalPrefences.getInstance().putInt(context, sessionTimeoutModel.getRoom().getData().getId(), (LocalPrefences.getInstance().getInt(context, sessionTimeoutModel.getRoom().getData().getId()) + 1));
                        Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                        intent.putExtra(AppConstant.APP_SESSION_MODEL, sessionTimeoutModel);
                        context.sendBroadcast(intent);
                    }
                }
            }
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.NEW_ROOM_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                context.sendBroadcast(intent);
            }
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_TIMEOUT_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                LocalPrefences.getInstance().putBoolean(context, AppConstant.DELAY_ALERT + sessionTimeoutModel.getRoom().getData().getId(), true);
                Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                intent.putExtra(AppConstant.SESSION_TIMEOUT_ACTION, true);
                intent.putExtra(AppConstant.ROOM_ID, sessionTimeoutModel.getRoom().getData().getId());
                intent.putExtra("time", sessionTimeoutModel.getTimeout());
                context.sendBroadcast(intent);
            }
        } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_END_ACTION)) {
            if (LocalPrefences.getInstance().isUserLogin(context)) {
                Intent intent = new Intent(AppConstant.NEW_MESSAGE_RECIEVER);
                intent.putExtra(AppConstant.SESSION_END_ACTION, true);
                intent.putExtra(AppConstant.ROOM_ID, sessionTimeoutModel.getRoom().getData().getId());
                context.sendBroadcast(intent);
            }
        }
    }

    private void showLog(String message) {
        if (showLog)
            Log.e("ActionSession", ":" + message);
    }
}
