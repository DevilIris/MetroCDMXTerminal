package android.support.p000v4.app;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@TargetApi(16)
@RequiresApi(16)
/* renamed from: android.support.v4.app.TaskStackBuilderJellybean */
class TaskStackBuilderJellybean {
    TaskStackBuilderJellybean() {
    }

    public static PendingIntent getActivitiesPendingIntent(Context context, int requestCode, Intent[] intents, int flags, Bundle options) {
        return PendingIntent.getActivities(context, requestCode, intents, flags, options);
    }
}
