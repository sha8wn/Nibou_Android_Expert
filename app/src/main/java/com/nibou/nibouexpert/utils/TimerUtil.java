package com.nibou.nibouexpert.utils;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class TimerUtil {

    private static TimerUtil timerUtil;
    private CountDownTimer countDownTimer;
    private CountDownTimer countDown10SecTimer;

    private TimerUtil() {
    }

    public static TimerUtil getTimerUtil() {
        if (timerUtil == null)
            timerUtil = new TimerUtil();
        return timerUtil;
    }


    public interface TimerCallback {
        void onTimerCompleted();

        void onTimerTick(String time, long minute, long second);
    }

    public void startTimer(long intialDuration, TimerCallback timerCallback) {
        countDownTimer = new CountDownTimer(System.currentTimeMillis(), 1000) {

            private long currentTime = System.currentTimeMillis();

            @Override
            public void onTick(long tickValue) {
                if (timerCallback != null) {
                    long time = ((currentTime - tickValue) / 1000) + intialDuration;
                    timerCallback.onTimerTick(formatSeconds(time), (long) Math.floor(time % 3600 / 60), time);
                }
            }

            @Override
            public void onFinish() {
                if (timerCallback != null) {
                    timerCallback.onTimerCompleted();
                }
            }
        };
        countDownTimer.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public static String formatSeconds(long timeInSeconds) {
        long secondsLeft = timeInSeconds % 3600 % 60;
        long minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        long hours = (int) Math.floor(timeInSeconds / 3600);
        String HH = hours < 10 ? "0" + hours : "" + hours;
        String MM = minutes < 10 ? "0" + minutes : "" + minutes;
        String SS = secondsLeft < 10 ? "0" + secondsLeft : "" + secondsLeft;
        if (hours > 0)
            return HH + ":" + MM + ":" + SS;
        else
            return MM + ":" + SS;
    }

    public void start10SecTimer(TimerCallback timerCallback) {
        countDown10SecTimer = new CountDownTimer(System.currentTimeMillis(), (1 * 1000)) {

            private long currentTime = System.currentTimeMillis();

            @Override
            public void onTick(long tickValue) {
                if (timerCallback != null) {
                    long time = ((currentTime - tickValue) / 1000);
                    timerCallback.onTimerTick(formatSeconds(time), (long) Math.floor(time % 3600 / 60), time);
                }
            }

            @Override
            public void onFinish() {
                if (timerCallback != null) {
                    timerCallback.onTimerCompleted();
                }
            }
        };
        countDown10SecTimer.start();
    }

    public void cancel10SecTimer() {
        if (countDown10SecTimer != null) {
            countDown10SecTimer.cancel();
        }
    }


}
