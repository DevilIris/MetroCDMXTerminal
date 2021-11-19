package android.support.p000v4.app;

import android.app.Service;
import android.support.annotation.RestrictTo;
import android.support.p000v4.p002os.BuildCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* renamed from: android.support.v4.app.ServiceCompat */
public final class ServiceCompat {
    static final ServiceCompatImpl IMPL;
    public static final int START_STICKY = 1;
    public static final int STOP_FOREGROUND_DETACH = 2;
    public static final int STOP_FOREGROUND_REMOVE = 1;

    /* renamed from: android.support.v4.app.ServiceCompat$ServiceCompatImpl */
    interface ServiceCompatImpl {
        void stopForeground(Service service, int i);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.support.v4.app.ServiceCompat$StopForegroundFlags */
    public @interface StopForegroundFlags {
    }

    private ServiceCompat() {
    }

    /* renamed from: android.support.v4.app.ServiceCompat$BaseServiceCompatImpl */
    static class BaseServiceCompatImpl implements ServiceCompatImpl {
        BaseServiceCompatImpl() {
        }

        public void stopForeground(Service service, int flags) {
            service.stopForeground((flags & 1) != 0);
        }
    }

    /* renamed from: android.support.v4.app.ServiceCompat$Api24ServiceCompatImpl */
    static class Api24ServiceCompatImpl implements ServiceCompatImpl {
        Api24ServiceCompatImpl() {
        }

        public void stopForeground(Service service, int flags) {
            ServiceCompatApi24.stopForeground(service, flags);
        }
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new Api24ServiceCompatImpl();
        } else {
            IMPL = new BaseServiceCompatImpl();
        }
    }

    public static void stopForeground(Service service, int flags) {
        IMPL.stopForeground(service, flags);
    }
}
