package com.example.mad_project;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NewAppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateWidgetFromDatabase(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidgetFromDatabase(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        DatabaseReference userWidgetsRef = FirebaseDatabase.getInstance().getReference().child("userWidgets");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userWidgetsRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String widgetText = dataSnapshot.getValue(String.class);
                if (widgetText != null) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, widgetText);
                } else {
                    updateAppWidget(context, appWidgetManager, appWidgetId, "users text");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String widgetText) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}
