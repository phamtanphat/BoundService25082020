package com.example.boundservice25082020;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    int mCount = 0;
    NotificationManager mNotificationManager;
    Notification mNotification;
    String MY_CHANNEL = "MY_CHANNEL";
    Context mContext = null;
    OnListenerCount mOnListenerCount;
    boolean mCreate = false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = createNotification(this,mCount).build();
        startForeground(1 , mNotification);
        mContext = this;
        mCreate = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mCreate){
            CountDownTimer countDownTimer = new CountDownTimer(100000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mCount++;
                    mNotification = createNotification(mContext,mCount).build();
                    mNotificationManager.notify(1,mNotification);
                    if (mOnListenerCount != null){
                        mOnListenerCount.onCount(mCount);
                    }

                }

                @Override
                public void onFinish() {

                }
            };
            countDownTimer.start();
            mCreate = false;
        }
        return START_NOT_STICKY;
    }

    private NotificationCompat.Builder createNotification(Context context , int count){
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, MY_CHANNEL)
                        .setContentTitle("Đếm giá trị")
                        .setContentText("Count " + count)
                        .setShowWhen(true)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(
                            MY_CHANNEL,
                            "CHANNEL",
                            NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        return notification;
    }

    class MyBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }
    public void setOnListenerCount(OnListenerCount onListenerCount){
        this.mOnListenerCount = onListenerCount;
    }
}
