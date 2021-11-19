package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

@TargetApi(14)
@RequiresApi(14)
class TransitionManagerStaticsIcs extends TransitionManagerStaticsImpl {
    TransitionManagerStaticsIcs() {
    }

    /* renamed from: go */
    public void mo1182go(SceneImpl scene) {
        TransitionManagerPort.m4go(((SceneIcs) scene).mScene);
    }

    /* renamed from: go */
    public void mo1183go(SceneImpl scene, TransitionImpl transition) {
        TransitionManagerPort.m5go(((SceneIcs) scene).mScene, transition == null ? null : ((TransitionIcs) transition).mTransition);
    }

    public void beginDelayedTransition(ViewGroup sceneRoot) {
        TransitionManagerPort.beginDelayedTransition(sceneRoot);
    }

    public void beginDelayedTransition(ViewGroup sceneRoot, TransitionImpl transition) {
        TransitionManagerPort.beginDelayedTransition(sceneRoot, transition == null ? null : ((TransitionIcs) transition).mTransition);
    }
}
