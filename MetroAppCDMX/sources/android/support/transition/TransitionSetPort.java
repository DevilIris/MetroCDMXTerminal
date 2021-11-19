package android.support.transition;

import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.transition.TransitionPort;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(14)
@RequiresApi(14)
class TransitionSetPort extends TransitionPort {
    public static final int ORDERING_SEQUENTIAL = 1;
    public static final int ORDERING_TOGETHER = 0;
    int mCurrentListeners;
    private boolean mPlayTogether = true;
    boolean mStarted = false;
    ArrayList<TransitionPort> mTransitions = new ArrayList<>();

    public int getOrdering() {
        return this.mPlayTogether ? 0 : 1;
    }

    public TransitionSetPort setOrdering(int ordering) {
        switch (ordering) {
            case 0:
                this.mPlayTogether = true;
                break;
            case 1:
                this.mPlayTogether = false;
                break;
            default:
                throw new AndroidRuntimeException("Invalid parameter for TransitionSet ordering: " + ordering);
        }
        return this;
    }

    public TransitionSetPort addTransition(TransitionPort transition) {
        if (transition != null) {
            this.mTransitions.add(transition);
            transition.mParent = this;
            if (this.mDuration >= 0) {
                transition.setDuration(this.mDuration);
            }
        }
        return this;
    }

    public TransitionSetPort setDuration(long duration) {
        super.setDuration(duration);
        if (this.mDuration >= 0) {
            int numTransitions = this.mTransitions.size();
            for (int i = 0; i < numTransitions; i++) {
                this.mTransitions.get(i).setDuration(duration);
            }
        }
        return this;
    }

    public TransitionSetPort setStartDelay(long startDelay) {
        return (TransitionSetPort) super.setStartDelay(startDelay);
    }

    public TransitionSetPort setInterpolator(TimeInterpolator interpolator) {
        return (TransitionSetPort) super.setInterpolator(interpolator);
    }

    public TransitionSetPort addTarget(View target) {
        return (TransitionSetPort) super.addTarget(target);
    }

    public TransitionSetPort addTarget(int targetId) {
        return (TransitionSetPort) super.addTarget(targetId);
    }

    public TransitionSetPort addListener(TransitionPort.TransitionListener listener) {
        return (TransitionSetPort) super.addListener(listener);
    }

    public TransitionSetPort removeTarget(int targetId) {
        return (TransitionSetPort) super.removeTarget(targetId);
    }

    public TransitionSetPort removeTarget(View target) {
        return (TransitionSetPort) super.removeTarget(target);
    }

    public TransitionSetPort removeListener(TransitionPort.TransitionListener listener) {
        return (TransitionSetPort) super.removeListener(listener);
    }

    public TransitionSetPort removeTransition(TransitionPort transition) {
        this.mTransitions.remove(transition);
        transition.mParent = null;
        return this;
    }

    private void setupStartEndListeners() {
        TransitionSetListener listener = new TransitionSetListener(this);
        Iterator<TransitionPort> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().addListener(listener);
        }
        this.mCurrentListeners = this.mTransitions.size();
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        Iterator<TransitionPort> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().createAnimators(sceneRoot, startValues, endValues);
        }
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void runAnimators() {
        if (this.mTransitions.isEmpty()) {
            start();
            end();
            return;
        }
        setupStartEndListeners();
        if (!this.mPlayTogether) {
            for (int i = 1; i < this.mTransitions.size(); i++) {
                final TransitionPort nextTransition = this.mTransitions.get(i);
                this.mTransitions.get(i - 1).addListener(new TransitionPort.TransitionListenerAdapter() {
                    public void onTransitionEnd(TransitionPort transition) {
                        nextTransition.runAnimators();
                        transition.removeListener(this);
                    }
                });
            }
            TransitionPort firstTransition = this.mTransitions.get(0);
            if (firstTransition != null) {
                firstTransition.runAnimators();
                return;
            }
            return;
        }
        Iterator<TransitionPort> it = this.mTransitions.iterator();
        while (it.hasNext()) {
            it.next().runAnimators();
        }
    }

    public void captureStartValues(TransitionValues transitionValues) {
        int targetId = transitionValues.view.getId();
        if (isValidTarget(transitionValues.view, (long) targetId)) {
            Iterator<TransitionPort> it = this.mTransitions.iterator();
            while (it.hasNext()) {
                TransitionPort childTransition = it.next();
                if (childTransition.isValidTarget(transitionValues.view, (long) targetId)) {
                    childTransition.captureStartValues(transitionValues);
                }
            }
        }
    }

    public void captureEndValues(TransitionValues transitionValues) {
        int targetId = transitionValues.view.getId();
        if (isValidTarget(transitionValues.view, (long) targetId)) {
            Iterator<TransitionPort> it = this.mTransitions.iterator();
            while (it.hasNext()) {
                TransitionPort childTransition = it.next();
                if (childTransition.isValidTarget(transitionValues.view, (long) targetId)) {
                    childTransition.captureEndValues(transitionValues);
                }
            }
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void pause(View sceneRoot) {
        super.pause(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).pause(sceneRoot);
        }
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void resume(View sceneRoot) {
        super.resume(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).resume(sceneRoot);
        }
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public void cancel() {
        super.cancel();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).cancel();
        }
    }

    /* access modifiers changed from: package-private */
    public TransitionSetPort setSceneRoot(ViewGroup sceneRoot) {
        super.setSceneRoot(sceneRoot);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setSceneRoot(sceneRoot);
        }
        return this;
    }

    /* access modifiers changed from: package-private */
    public void setCanRemoveViews(boolean canRemoveViews) {
        super.setCanRemoveViews(canRemoveViews);
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            this.mTransitions.get(i).setCanRemoveViews(canRemoveViews);
        }
    }

    /* access modifiers changed from: package-private */
    public String toString(String indent) {
        String result = super.toString(indent);
        for (int i = 0; i < this.mTransitions.size(); i++) {
            result = result + "\n" + this.mTransitions.get(i).toString(indent + "  ");
        }
        return result;
    }

    public TransitionSetPort clone() {
        TransitionSetPort clone = (TransitionSetPort) super.clone();
        clone.mTransitions = new ArrayList<>();
        int numTransitions = this.mTransitions.size();
        for (int i = 0; i < numTransitions; i++) {
            clone.addTransition(this.mTransitions.get(i).clone());
        }
        return clone;
    }

    static class TransitionSetListener extends TransitionPort.TransitionListenerAdapter {
        TransitionSetPort mTransitionSet;

        TransitionSetListener(TransitionSetPort transitionSet) {
            this.mTransitionSet = transitionSet;
        }

        public void onTransitionStart(TransitionPort transition) {
            if (!this.mTransitionSet.mStarted) {
                this.mTransitionSet.start();
                this.mTransitionSet.mStarted = true;
            }
        }

        public void onTransitionEnd(TransitionPort transition) {
            TransitionSetPort transitionSetPort = this.mTransitionSet;
            transitionSetPort.mCurrentListeners--;
            if (this.mTransitionSet.mCurrentListeners == 0) {
                this.mTransitionSet.mStarted = false;
                this.mTransitionSet.end();
            }
            transition.removeListener(this);
        }
    }
}
