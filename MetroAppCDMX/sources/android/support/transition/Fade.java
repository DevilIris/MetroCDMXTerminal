package android.support.transition;

import android.animation.Animator;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

public class Fade extends Visibility {

    /* renamed from: IN */
    public static final int f4IN = 1;
    public static final int OUT = 2;

    public Fade(int fadingMode) {
        super(true);
        if (Build.VERSION.SDK_INT >= 19) {
            if (fadingMode > 0) {
                this.mImpl = new FadeKitKat(this, fadingMode);
            } else {
                this.mImpl = new FadeKitKat(this);
            }
        } else if (fadingMode > 0) {
            this.mImpl = new FadeIcs(this, fadingMode);
        } else {
            this.mImpl = new FadeIcs(this);
        }
    }

    public Fade() {
        this(-1);
    }

    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        this.mImpl.captureEndValues(transitionValues);
    }

    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        this.mImpl.captureStartValues(transitionValues);
    }

    @Nullable
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @NonNull TransitionValues startValues, @NonNull TransitionValues endValues) {
        return this.mImpl.createAnimator(sceneRoot, startValues, endValues);
    }
}
