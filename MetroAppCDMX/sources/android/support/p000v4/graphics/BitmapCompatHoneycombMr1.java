package android.support.p000v4.graphics;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.support.annotation.RequiresApi;

@TargetApi(12)
@RequiresApi(12)
/* renamed from: android.support.v4.graphics.BitmapCompatHoneycombMr1 */
class BitmapCompatHoneycombMr1 {
    BitmapCompatHoneycombMr1() {
    }

    static int getAllocationByteCount(Bitmap bitmap) {
        return bitmap.getByteCount();
    }
}
