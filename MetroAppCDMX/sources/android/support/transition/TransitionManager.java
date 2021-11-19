package android.support.transition;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

public class TransitionManager {
    private static TransitionManagerStaticsImpl sImpl;
    private TransitionManagerImpl mImpl;

    static {
        if (Build.VERSION.SDK_INT < 19) {
            sImpl = new TransitionManagerStaticsIcs();
        } else {
            sImpl = new TransitionManagerStaticsKitKat();
        }
    }

    public TransitionManager() {
        if (Build.VERSION.SDK_INT < 19) {
            this.mImpl = new TransitionManagerIcs();
        } else {
            this.mImpl = new TransitionManagerKitKat();
        }
    }

    /* renamed from: go */
    public static void m2go(@NonNull Scene scene) {
        sImpl.mo1182go(scene.mImpl);
    }

    /* renamed from: go */
    public static void m3go(@NonNull Scene scene, @Nullable Transition transition) {
        sImpl.mo1183go(scene.mImpl, transition == null ? null : transition.mImpl);
    }

    public static void beginDelayedTransition(@NonNull ViewGroup sceneRoot) {
        sImpl.beginDelayedTransition(sceneRoot);
    }

    public static void beginDelayedTransition(@NonNull ViewGroup sceneRoot, @Nullable Transition transition) {
        sImpl.beginDelayedTransition(sceneRoot, transition == null ? null : transition.mImpl);
    }

    public void setTransition(@NonNull Scene scene, @Nullable Transition transition) {
        this.mImpl.setTransition(scene.mImpl, transition == null ? null : transition.mImpl);
    }

    public void setTransition(@NonNull Scene fromScene, @NonNull Scene toScene, @Nullable Transition transition) {
        this.mImpl.setTransition(fromScene.mImpl, toScene.mImpl, transition == null ? null : transition.mImpl);
    }

    public void transitionTo(@NonNull Scene scene) {
        this.mImpl.transitionTo(scene.mImpl);
    }
}
