package com.example.tpc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import fragments.u_contests;

public class eventChange extends Service {
    public eventChange() {
    }
    public static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction().equals("te")) {
            startServiceWithNotification();
        }
        else stopMyService();

        return START_STICKY;
    }

    private void startServiceWithNotification() {

        if (isServiceRunning) return;
        isServiceRunning = true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,@Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("testinggg", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            pushNotification("Event Added: " + dc.getDocument().get("name"));
                            break;
                        case MODIFIED:
                            pushNotification("Event Updated: "+ dc.getDocument().get("name"));
                            break;
                        case REMOVED:
                            pushNotification("Event Deleted: " + dc.getDocument().get("name"));
                            break;
                    }
                }

            }
        });
    }

//    @Override
//    public void onDestroy() {
//        isServiceRunning = false;
//        super.onDestroy();
////        Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
//    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void pushNotification(String title){
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DESCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText("Open the dashboard to know more!")// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), u_contests.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }
}