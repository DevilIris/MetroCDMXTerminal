package android.support.design.widget;

import android.support.design.widget.ValueAnimatorCompat;
import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator {
    private final ValueAnimatorCompat.AnimatorListener mAnimationListener = new ValueAnimatorCompat.AnimatorListenerAdapter() {
        public void onAnimationEnd(ValueAnimatorCompat animator) {
            if (StateListAnimator.this.mRunningAnimator == animator) {
                StateListAnimator.this.mRunningAnimator = null;
            }
        }
    };
    private Tuple mLastMatch = null;
    ValueAnimatorCompat mRunningAnimator = null;
    private final ArrayList<Tuple> mTuples = new ArrayList<>();

    StateListAnimator() {
    }

    public void addState(int[] specs, ValueAnimatorCompat animator) {
        Tuple tuple = new Tuple(specs, animator);
        animator.addListener(this.mAnimationListener);
        this.mTuples.add(tuple);
    }

    /* access modifiers changed from: package-private */
    public void setState(int[] state) {
        Tuple match = null;
        int count = this.mTuples.size();
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            Tuple tuple = this.mTuples.get(i);
            if (StateSet.stateSetMatches(tuple.mSpecs, state)) {
                match = tuple;
                break;
            }
            i++;
        }
        if (match != this.mLastMatch) {
            if (this.mLastMatch != null) {
                cancel();
            }
            this.mLastMatch = match;
            if (match != null) {
                start(match);
            }
        }
    }

    private void start(Tuple match) {
        this.mRunningAnimator = match.mAnimator;
        this.mRunningAnimator.start();
    }

    private void cancel() {
        if (this.mRunningAnimator != null) {
            this.mRunningAnimator.cancel();
            this.mRunningAnimator = null;
        }
    }

    public void jumpToCurrentState() {
        if (this.mRunningAnimator != null) {
            this.mRunningAnimator.end();
            this.mRunningAnimator = null;
        }
    }

    static class Tuple {
        final ValueAnimatorCompat mAnimator;
        final int[] mSpecs;

        Tuple(int[] specs, ValueAnimatorCompat animator) {
            this.mSpecs = specs;
            this.mAnimator = animator;
        }
    }
}
