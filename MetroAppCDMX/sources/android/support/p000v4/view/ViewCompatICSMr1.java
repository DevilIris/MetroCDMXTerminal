package android.support.p000v4.view;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;

@TargetApi(15)
@RequiresApi(15)
/* renamed from: android.support.v4.view.ViewCompatICSMr1 */
class ViewCompatICSMr1 {
    ViewCompatICSMr1() {
    }

    public static boolean hasOnClickListeners(View v) {
        return v.hasOnClickListeners();
    }
}
