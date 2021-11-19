package android.support.design.internal;

import android.support.p000v4.view.animation.FastOutSlowInInterpolator;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.view.ViewGroup;

class BottomNavigationAnimationHelperIcs extends BottomNavigationAnimationHelperBase {
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115;
    private final TransitionSet mSet = new AutoTransition();

    BottomNavigationAnimationHelperIcs() {
        this.mSet.setOrdering(0);
        this.mSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        this.mSet.setInterpolator(new FastOutSlowInInterpolator());
        this.mSet.addTransition(new TextScale());
    }

    /* access modifiers changed from: package-private */
    public void beginDelayedTransition(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, this.mSet);
    }
}
