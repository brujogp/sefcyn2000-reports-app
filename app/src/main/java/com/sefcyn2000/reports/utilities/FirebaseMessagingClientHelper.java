package com.sefcyn2000.reports.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.*;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sefcyn2000.reports.R;
import com.sefcyn2000.reports.ui.activities.MainActivity;
import com.sefcyn2000.reports.ui.activities.reports.ReportsActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FirebaseMessagingClientHelper extends FirebaseMessagingService {
    public void messaging() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TEST-T", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    Log.d("TEST-T", token);
                });

        FirebaseMessaging.getInstance().subscribeToTopic("reports-finished")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("TEST-T", "Suscrito");
                    }
                });
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().size() > 0) {
            sendNotification(
                    remoteMessage.getData().get("message"),
                    remoteMessage.getData().get("thumbnail"),
                    remoteMessage.getData().get("technical-name"),
                    remoteMessage.getData().get("unit-name")
            );
        }
    }

    private void sendNotification(String messageBody, String urlThumbnail, String technicalName, String unitName) {
        Intent intent = new Intent(this, ReportsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Executor executor = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        final Bitmap[] bitmap = new Bitmap[1];

        executor.execute(() -> {

            try {
                Log.d("TEST-T", "La Url es: " + urlThumbnail);

                URL urlImage = new URL(urlThumbnail);

                HttpURLConnection httpURLConnection = (HttpURLConnection) urlImage.openConnection();

                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();

                bitmap[0] = BitmapFactory.decodeStream(inputStream);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, channelId)
                                .setSmallIcon(R.drawable.ic_logo)
                                .setContentTitle("Hay un nuevo reporte para la unidad: " + unitName)
                                .setContentText("Con el técnico: " + technicalName)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_STATUS)
                                .setAutoCancel(true)
                                .setLargeIcon(bitmap[0])
                                .setSound(defaultSoundUri)
                                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap[0]).bigLargeIcon(null))
                                .setContentIntent(pendingIntent);


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId,
                            "Notificaciones para reportes",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            });

        });


    }
}
