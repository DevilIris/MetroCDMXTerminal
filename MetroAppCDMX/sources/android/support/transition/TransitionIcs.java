package android.support.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.transition.TransitionPort;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(14)
@RequiresApi(14)
class TransitionIcs extends TransitionImpl {
    private CompatListener mCompatListener;
    TransitionInterface mExternalTransition;
    TransitionPort mTransition;

    TransitionIcs() {
    }

    public void init(TransitionInterface external, Object internal) {
        this.mExternalTransition = external;
        if (internal == null) {
            this.mTransition = new TransitionWrapper(external);
        } else {
            this.mTransition = (TransitionPort) internal;
        }
    }

    public TransitionImpl addListener(TransitionInterfaceListener listener) {
        if (this.mCompatListener == null) {
            this.mCompatListener = new CompatListener();
            this.mTransition.addListener(this.mCompatListener);
        }
        this.mCompatListener.addListener(listener);
        return this;
    }

    public TransitionImpl removeListener(TransitionInterfaceListener listener) {
        if (this.mCompatListener != null) {
            this.mCompatListener.removeListener(listener);
            if (this.mCompatListener.isEmpty()) {
                this.mTransition.removeListener(this.mCompatListener);
                this.mCompatListener = null;
            }
        }
        return this;
    }

    public TransitionImpl addTarget(View target) {
        this.mTransition.addTarget(target);
        return this;
    }

    public TransitionImpl addTarget(int targetId) {
        this.mTransition.addTarget(targetId);
        return this;
    }

    public void captureEndValues(TransitionValues transitionValues) {
        this.mTransition.captureEndValues(transitionValues);
    }

    public void captureStartValues(TransitionValues transitionValues) {
        this.mTransition.captureStartValues(transitionValues);
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return this.mTransition.createAnimator(sceneRoot, startValues, endValues);
    }

    public TransitionImpl excludeChildren(View target, boolean exclude) {
        this.mTransition.excludeChildren(target, exclude);
        return this;
    }

    public TransitionImpl excludeChildren(int targetId, boolean exclude) {
        this.mTransition.excludeChildren(targetId, exclude);
        return this;
    }

    public TransitionImpl excludeChildren(Class type, boolean exclude) {
        this.mTransition.excludeChildren(type, exclude);
        return this;
    }

    public TransitionImpl excludeTarget(View target, boolean exclude) {
        this.mTransition.excludeTarget(target, exclude);
        return this;
    }

    public TransitionImpl excludeTarget(int targetId, boolean exclude) {
        this.mTransition.excludeTarget(targetId, exclude);
        return this;
    }

    public TransitionImpl excludeTarget(Class type, boolean exclude) {
        this.mTransition.excludeTarget(type, exclude);
        return this;
    }

    public long getDuration() {
        return this.mTransition.getDuration();
    }

    public TransitionImpl setDuration(long duration) {
        this.mTransition.setDuration(duration);
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mTransition.getInterpolator();
    }

    public TransitionImpl setInterpolator(TimeInterpolator interpolator) {
        this.mTransition.setInterpolator(interpolator);
        return this;
    }

    public String getName() {
        return this.mTransition.getName();
    }

    public long getStartDelay() {
        return this.mTransition.getStartDelay();
    }

    public TransitionImpl setStartDelay(long startDelay) {
        this.mTransition.setStartDelay(startDelay);
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTransition.getTargetIds();
    }

    public List<View> getTargets() {
        return this.mTransition.getTargets();
    }

    public String[] getTransitionProperties() {
        return this.mTransition.getTransitionProperties();
    }

    public TransitionValues getTransitionValues(View view, boolean start) {
        return this.mTransition.getTransitionValues(view, start);
    }

    public TransitionImpl removeTarget(View target) {
        this.mTransition.removeTarget(target);
        return this;
    }

    public TransitionImpl removeTarget(int targetId) {
        this.mTransition.removeTarget(targetId);
        return this;
    }

    public String toString() {
        return this.mTransition.toString();
    }

    private static class TransitionWrapper extends TransitionPort {
        private TransitionInterface mTransition;

        public TransitionWrapper(TransitionInterface transition) {
            this.mTransition = transition;
        }

        public void captureStartValues(TransitionValues transitionValues) {
            this.mTransition.captureStartValues(transitionValues);
        }

        public void captureEndValues(TransitionValues transitionValues) {
            this.mTransition.captureEndValues(transitionValues);
        }

        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
            return this.mTransition.createAnimator(sceneRoot, startValues, endValues);
        }
    }

    private class CompatListener implements TransitionPort.TransitionListener {
        private final ArrayList<TransitionInterfaceListener> mListeners = new ArrayList<>();

        CompatListener() {
        }

        public void addListener(TransitionInterfaceListener listener) {
            this.mListeners.add(listener);
        }

        public void removeListener(TransitionInterfaceListener listener) {
            this.mListeners.remove(listener);
        }

        public boolean isEmpty() {
            return this.mListeners.isEmpty();
        }

        public void onTransitionStart(TransitionPort transition) {
            Iterator<TransitionInterfaceListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionStart(TransitionIcs.this.mExternalTransition);
            }
        }

        public void onTransitionEnd(TransitionPort transition) {
            Iterator<TransitionInterfaceListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionEnd(TransitionIcs.this.mExternalTransition);
            }
        }

        public void onTransitionCancel(TransitionPort transition) {
            Iterator<TransitionInterfaceListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionCancel(TransitionIcs.this.mExternalTransition);
            }
        }

        public void onTransitionPause(TransitionPort transition) {
            Iterator<TransitionInterfaceListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionPause(TransitionIcs.this.mExternalTransition);
            }
        }

        public void onTransitionResume(TransitionPort transition) {
            Iterator<TransitionInterfaceListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionResume(TransitionIcs.this.mExternalTransition);
            }
        }
    }
}
