package android.support.p000v4.app;

import android.os.Bundle;
import android.support.p000v4.content.Loader;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: android.support.v4.app.LoaderManager */
public abstract class LoaderManager {

    /* renamed from: android.support.v4.app.LoaderManager$LoaderCallbacks */
    public interface LoaderCallbacks<D> {
        Loader<D> onCreateLoader(int i, Bundle bundle);

        void onLoadFinished(Loader<D> loader, D d);

        void onLoaderReset(Loader<D> loader);
    }

    public abstract void destroyLoader(int i);

    public abstract void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    public abstract <D> Loader<D> getLoader(int i);

    public abstract <D> Loader<D> initLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public abstract <D> Loader<D> restartLoader(int i, Bundle bundle, LoaderCallbacks<D> loaderCallbacks);

    public static void enableDebugLogging(boolean enabled) {
        LoaderManagerImpl.DEBUG = enabled;
    }

    public boolean hasRunningLoaders() {
        return false;
    }
}
