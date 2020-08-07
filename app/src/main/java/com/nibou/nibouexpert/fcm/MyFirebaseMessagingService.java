package com.nibou.nibouexpert.fcm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.nibou.nibouexpert.R;
import com.nibou.nibouexpert.activitys.SplashActivity;
import com.nibou.nibouexpert.models.AppSessionModel;
import com.nibou.nibouexpert.models.RoomModel;
import com.nibou.nibouexpert.utils.AppConstant;
import com.nibou.nibouexpert.utils.AppUtil;
import com.nibou.nibouexpert.utils.LocalPrefences;

import java.util.HashSet;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        AppUtil.showLogError("Token", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            AppUtil.showLogError("Push", remoteMessage.getData().toString());
            handleAction(new Gson().fromJson(remoteMessage.getData().toString(), AppSessionModel.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAction(AppSessionModel sessionTimeoutModel) {
        if (LocalPrefences.getInstance().isUserLogin(getApplicationContext())) {
            if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.NEW_MESSAGE_ACTION)) {
                if (!LocalPrefences.getInstance().getLocalProfileModel(getApplicationContext()).getData().getId().equals(sessionTimeoutModel.getMessage().getFrom_user_id()) && !AppConstant.IGNORE_MESSAGE_TEXT.equals(sessionTimeoutModel.getNotification().getBody())) {
                    LocalPrefences.getInstance().putInt(getApplicationContext(), sessionTimeoutModel.getRoom_id(), (LocalPrefences.getInstance().getInt(getApplicationContext(), sessionTimeoutModel.getRoom_id()) + 1));
                    if (sessionTimeoutModel.getNotification().getBody() != null) {
                        showUiNotification(null, sessionTimeoutModel.getNotification().getTitle(), sessionTimeoutModel.getNotification().getBody());
                    } else {
                        showUiNotification(null, sessionTimeoutModel.getNotification().getTitle(), getString(R.string.image));
                    }
                }
            } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.NEW_ROOM_ACTION)) {
                showUiNotification(null, sessionTimeoutModel.getNotification().getTitle(), sessionTimeoutModel.getNotification().getBody());
            } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_TIMEOUT_ACTION)) {
                showUiNotification(sessionTimeoutModel.getRoom_id(), sessionTimeoutModel.getNotification().getTitle(), sessionTimeoutModel.getNotification().getBody());
            } else if (sessionTimeoutModel.getAction().equalsIgnoreCase(AppConstant.SESSION_END_ACTION)) {
                showUiNotification(null, sessionTimeoutModel.getNotification().getTitle(), sessionTimeoutModel.getNotification().getBody());
            }
        }
    }

    private void showUiNotification(String roomId, String title, String message) {
        int notificationId = (int) System.currentTimeMillis();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        resultIntent.putExtra(AppConstant.NOTIFICATION_ID, String.valueOf(notificationId));
        if (roomId != null)
            resultIntent.putExtra(AppConstant.ROOM_ID, roomId);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(message);
//                .setStyle(new Notification.BigTextStyle().bigText(message));

        mBuilder.setSmallIcon(R.drawable.icon_notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
        mBuilder.setContentIntent(PendingIntent.getActivity(this, notificationId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        mBuilder.setDefaults(-1);
        mBuilder.setVibrate(new long[]{0, 1000, 1000, 1000, 1000});

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(getString(R.string.fcm_channel_id), getString(R.string.notifications), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.group));
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(getString(R.string.fcm_channel_id));
        }
        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
