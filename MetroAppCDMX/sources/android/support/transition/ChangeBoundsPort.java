package android.support.transition;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;
import android.view.View;

@TargetApi(14)
@RequiresApi(14)
class ChangeBoundsPort extends TransitionPort {
    private static final String LOG_TAG = "ChangeBounds";
    private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
    private static final String PROPNAME_PARENT = "android:changeBounds:parent";
    private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
    private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
    private static RectEvaluator sRectEvaluator = new RectEvaluator();
    private static final String[] sTransitionProperties = {PROPNAME_BOUNDS, PROPNAME_PARENT, PROPNAME_WINDOW_X, PROPNAME_WINDOW_Y};
    boolean mReparent = false;
    boolean mResizeClip = false;
    int[] tempLocation = new int[2];

    ChangeBoundsPort() {
    }

    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    public void setResizeClip(boolean resizeClip) {
        this.mResizeClip = resizeClip;
    }

    public void setReparent(boolean reparent) {
        this.mReparent = reparent;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;
        values.values.put(PROPNAME_BOUNDS, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
        values.values.put(PROPNAME_PARENT, values.view.getParent());
        values.view.getLocationInWindow(this.tempLocation);
        values.values.put(PROPNAME_WINDOW_X, Integer.valueOf(this.tempLocation[0]));
        values.values.put(PROPNAME_WINDOW_Y, Integer.valueOf(this.tempLocation[1]));
    }

    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0036, code lost:
        r46 = r33.getId();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.animation.Animator createAnimator(android.view.ViewGroup r52, android.support.transition.TransitionValues r53, android.support.transition.TransitionValues r54) {
        /*
            r51 = this;
            if (r53 == 0) goto L_0x0004
            if (r54 != 0) goto L_0x0006
        L_0x0004:
            r5 = 0
        L_0x0005:
            return r5
        L_0x0006:
            r0 = r53
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r34 = r0
            r0 = r54
            java.util.Map<java.lang.String, java.lang.Object> r15 = r0.values
            java.lang.String r46 = "android:changeBounds:parent"
            r0 = r34
            r1 = r46
            java.lang.Object r33 = r0.get(r1)
            android.view.ViewGroup r33 = (android.view.ViewGroup) r33
            java.lang.String r46 = "android:changeBounds:parent"
            r0 = r46
            java.lang.Object r14 = r15.get(r0)
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            if (r33 == 0) goto L_0x002a
            if (r14 != 0) goto L_0x002c
        L_0x002a:
            r5 = 0
            goto L_0x0005
        L_0x002c:
            r0 = r54
            android.view.View r0 = r0.view
            r44 = r0
            r0 = r33
            if (r0 == r14) goto L_0x0044
            int r46 = r33.getId()
            int r47 = r14.getId()
            r0 = r46
            r1 = r47
            if (r0 != r1) goto L_0x01ac
        L_0x0044:
            r24 = 1
        L_0x0046:
            r0 = r51
            boolean r0 = r0.mReparent
            r46 = r0
            if (r46 == 0) goto L_0x0050
            if (r24 == 0) goto L_0x02e0
        L_0x0050:
            r0 = r53
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:bounds"
            java.lang.Object r29 = r46.get(r47)
            android.graphics.Rect r29 = (android.graphics.Rect) r29
            r0 = r54
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:bounds"
            java.lang.Object r10 = r46.get(r47)
            android.graphics.Rect r10 = (android.graphics.Rect) r10
            r0 = r29
            int r0 = r0.left
            r32 = r0
            int r13 = r10.left
            r0 = r29
            int r0 = r0.top
            r36 = r0
            int r0 = r10.top
            r17 = r0
            r0 = r29
            int r0 = r0.right
            r35 = r0
            int r0 = r10.right
            r16 = r0
            r0 = r29
            int r0 = r0.bottom
            r28 = r0
            int r9 = r10.bottom
            int r37 = r35 - r32
            int r31 = r28 - r36
            int r18 = r16 - r13
            int r12 = r9 - r17
            r22 = 0
            if (r37 == 0) goto L_0x00be
            if (r31 == 0) goto L_0x00be
            if (r18 == 0) goto L_0x00be
            if (r12 == 0) goto L_0x00be
            r0 = r32
            if (r0 == r13) goto L_0x00a8
            int r22 = r22 + 1
        L_0x00a8:
            r0 = r36
            r1 = r17
            if (r0 == r1) goto L_0x00b0
            int r22 = r22 + 1
        L_0x00b0:
            r0 = r35
            r1 = r16
            if (r0 == r1) goto L_0x00b8
            int r22 = r22 + 1
        L_0x00b8:
            r0 = r28
            if (r0 == r9) goto L_0x00be
            int r22 = r22 + 1
        L_0x00be:
            if (r22 <= 0) goto L_0x0436
            r0 = r51
            boolean r0 = r0.mResizeClip
            r46 = r0
            if (r46 != 0) goto L_0x01b0
            r0 = r22
            android.animation.PropertyValuesHolder[] r0 = new android.animation.PropertyValuesHolder[r0]
            r25 = r0
            r26 = 0
            r0 = r32
            if (r0 == r13) goto L_0x00db
            r0 = r44
            r1 = r32
            r0.setLeft(r1)
        L_0x00db:
            r0 = r36
            r1 = r17
            if (r0 == r1) goto L_0x00e8
            r0 = r44
            r1 = r36
            r0.setTop(r1)
        L_0x00e8:
            r0 = r35
            r1 = r16
            if (r0 == r1) goto L_0x00f5
            r0 = r44
            r1 = r35
            r0.setRight(r1)
        L_0x00f5:
            r0 = r28
            if (r0 == r9) goto L_0x0100
            r0 = r44
            r1 = r28
            r0.setBottom(r1)
        L_0x0100:
            r0 = r32
            if (r0 == r13) goto L_0x0445
            int r27 = r26 + 1
            java.lang.String r46 = "left"
            r47 = 2
            r0 = r47
            int[] r0 = new int[r0]
            r47 = r0
            r48 = 0
            r47[r48] = r32
            r48 = 1
            r47[r48] = r13
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofInt(r46, r47)
            r25[r26] = r46
        L_0x011e:
            r0 = r36
            r1 = r17
            if (r0 == r1) goto L_0x0140
            int r26 = r27 + 1
            java.lang.String r46 = "top"
            r47 = 2
            r0 = r47
            int[] r0 = new int[r0]
            r47 = r0
            r48 = 0
            r47[r48] = r36
            r48 = 1
            r47[r48] = r17
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofInt(r46, r47)
            r25[r27] = r46
            r27 = r26
        L_0x0140:
            r0 = r35
            r1 = r16
            if (r0 == r1) goto L_0x0162
            int r26 = r27 + 1
            java.lang.String r46 = "right"
            r47 = 2
            r0 = r47
            int[] r0 = new int[r0]
            r47 = r0
            r48 = 0
            r47[r48] = r35
            r48 = 1
            r47[r48] = r16
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofInt(r46, r47)
            r25[r27] = r46
            r27 = r26
        L_0x0162:
            r0 = r28
            if (r0 == r9) goto L_0x0441
            int r26 = r27 + 1
            java.lang.String r46 = "bottom"
            r47 = 2
            r0 = r47
            int[] r0 = new int[r0]
            r47 = r0
            r48 = 0
            r47[r48] = r28
            r48 = 1
            r47[r48] = r9
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofInt(r46, r47)
            r25[r27] = r46
        L_0x0180:
            r0 = r44
            r1 = r25
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r0, r1)
            android.view.ViewParent r46 = r44.getParent()
            r0 = r46
            boolean r0 = r0 instanceof android.view.ViewGroup
            r46 = r0
            if (r46 == 0) goto L_0x0005
            android.view.ViewParent r23 = r44.getParent()
            android.view.ViewGroup r23 = (android.view.ViewGroup) r23
            android.support.transition.ChangeBoundsPort$1 r43 = new android.support.transition.ChangeBoundsPort$1
            r0 = r43
            r1 = r51
            r0.<init>()
            r0 = r51
            r1 = r43
            r0.addListener(r1)
            goto L_0x0005
        L_0x01ac:
            r24 = 0
            goto L_0x0046
        L_0x01b0:
            r0 = r37
            r1 = r18
            if (r0 == r1) goto L_0x01c7
            r0 = r37
            r1 = r18
            int r46 = java.lang.Math.max(r0, r1)
            int r46 = r46 + r13
            r0 = r44
            r1 = r46
            r0.setRight(r1)
        L_0x01c7:
            r0 = r31
            if (r0 == r12) goto L_0x01da
            r0 = r31
            int r46 = java.lang.Math.max(r0, r12)
            int r46 = r46 + r17
            r0 = r44
            r1 = r46
            r0.setBottom(r1)
        L_0x01da:
            r0 = r32
            if (r0 == r13) goto L_0x01ec
            int r46 = r32 - r13
            r0 = r46
            float r0 = (float) r0
            r46 = r0
            r0 = r44
            r1 = r46
            r0.setTranslationX(r1)
        L_0x01ec:
            r0 = r36
            r1 = r17
            if (r0 == r1) goto L_0x0200
            int r46 = r36 - r17
            r0 = r46
            float r0 = (float) r0
            r46 = r0
            r0 = r44
            r1 = r46
            r0.setTranslationY(r1)
        L_0x0200:
            int r46 = r13 - r32
            r0 = r46
            float r0 = (float) r0
            r41 = r0
            int r46 = r17 - r36
            r0 = r46
            float r0 = (float) r0
            r42 = r0
            int r45 = r18 - r37
            int r21 = r12 - r31
            r22 = 0
            r46 = 0
            int r46 = (r41 > r46 ? 1 : (r41 == r46 ? 0 : -1))
            if (r46 == 0) goto L_0x021c
            int r22 = r22 + 1
        L_0x021c:
            r46 = 0
            int r46 = (r42 > r46 ? 1 : (r42 == r46 ? 0 : -1))
            if (r46 == 0) goto L_0x0224
            int r22 = r22 + 1
        L_0x0224:
            if (r45 != 0) goto L_0x0228
            if (r21 == 0) goto L_0x022a
        L_0x0228:
            int r22 = r22 + 1
        L_0x022a:
            r0 = r22
            android.animation.PropertyValuesHolder[] r0 = new android.animation.PropertyValuesHolder[r0]
            r25 = r0
            r26 = 0
            r46 = 0
            int r46 = (r41 > r46 ? 1 : (r41 == r46 ? 0 : -1))
            if (r46 == 0) goto L_0x043d
            int r27 = r26 + 1
            java.lang.String r46 = "translationX"
            r47 = 2
            r0 = r47
            float[] r0 = new float[r0]
            r47 = r0
            r48 = 0
            float r49 = r44.getTranslationX()
            r47[r48] = r49
            r48 = 1
            r49 = 0
            r47[r48] = r49
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofFloat(r46, r47)
            r25[r26] = r46
        L_0x0258:
            r46 = 0
            int r46 = (r42 > r46 ? 1 : (r42 == r46 ? 0 : -1))
            if (r46 == 0) goto L_0x0439
            int r26 = r27 + 1
            java.lang.String r46 = "translationY"
            r47 = 2
            r0 = r47
            float[] r0 = new float[r0]
            r47 = r0
            r48 = 0
            float r49 = r44.getTranslationY()
            r47[r48] = r49
            r48 = 1
            r49 = 0
            r47[r48] = r49
            android.animation.PropertyValuesHolder r46 = android.animation.PropertyValuesHolder.ofFloat(r46, r47)
            r25[r27] = r46
        L_0x027e:
            if (r45 != 0) goto L_0x0282
            if (r21 == 0) goto L_0x02a6
        L_0x0282:
            android.graphics.Rect r40 = new android.graphics.Rect
            r46 = 0
            r47 = 0
            r0 = r40
            r1 = r46
            r2 = r47
            r3 = r37
            r4 = r31
            r0.<init>(r1, r2, r3, r4)
            android.graphics.Rect r46 = new android.graphics.Rect
            r47 = 0
            r48 = 0
            r0 = r46
            r1 = r47
            r2 = r48
            r3 = r18
            r0.<init>(r1, r2, r3, r12)
        L_0x02a6:
            r0 = r44
            r1 = r25
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r0, r1)
            android.view.ViewParent r46 = r44.getParent()
            r0 = r46
            boolean r0 = r0 instanceof android.view.ViewGroup
            r46 = r0
            if (r46 == 0) goto L_0x02d0
            android.view.ViewParent r23 = r44.getParent()
            android.view.ViewGroup r23 = (android.view.ViewGroup) r23
            android.support.transition.ChangeBoundsPort$2 r43 = new android.support.transition.ChangeBoundsPort$2
            r0 = r43
            r1 = r51
            r0.<init>()
            r0 = r51
            r1 = r43
            r0.addListener(r1)
        L_0x02d0:
            android.support.transition.ChangeBoundsPort$3 r46 = new android.support.transition.ChangeBoundsPort$3
            r0 = r46
            r1 = r51
            r0.<init>()
            r0 = r46
            r5.addListener(r0)
            goto L_0x0005
        L_0x02e0:
            r0 = r53
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:windowX"
            java.lang.Object r46 = r46.get(r47)
            java.lang.Integer r46 = (java.lang.Integer) r46
            int r38 = r46.intValue()
            r0 = r53
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:windowY"
            java.lang.Object r46 = r46.get(r47)
            java.lang.Integer r46 = (java.lang.Integer) r46
            int r39 = r46.intValue()
            r0 = r54
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:windowX"
            java.lang.Object r46 = r46.get(r47)
            java.lang.Integer r46 = (java.lang.Integer) r46
            int r19 = r46.intValue()
            r0 = r54
            java.util.Map<java.lang.String, java.lang.Object> r0 = r0.values
            r46 = r0
            java.lang.String r47 = "android:changeBounds:windowY"
            java.lang.Object r46 = r46.get(r47)
            java.lang.Integer r46 = (java.lang.Integer) r46
            int r20 = r46.intValue()
            r0 = r38
            r1 = r19
            if (r0 != r1) goto L_0x0334
            r0 = r39
            r1 = r20
            if (r0 == r1) goto L_0x0436
        L_0x0334:
            r0 = r51
            int[] r0 = r0.tempLocation
            r46 = r0
            r0 = r52
            r1 = r46
            r0.getLocationInWindow(r1)
            int r46 = r44.getWidth()
            int r47 = r44.getHeight()
            android.graphics.Bitmap$Config r48 = android.graphics.Bitmap.Config.ARGB_8888
            android.graphics.Bitmap r6 = android.graphics.Bitmap.createBitmap(r46, r47, r48)
            android.graphics.Canvas r7 = new android.graphics.Canvas
            r7.<init>(r6)
            r0 = r44
            r0.draw(r7)
            android.graphics.drawable.BitmapDrawable r8 = new android.graphics.drawable.BitmapDrawable
            r8.<init>(r6)
            r46 = 4
            r0 = r44
            r1 = r46
            r0.setVisibility(r1)
            android.support.transition.ViewOverlay r46 = android.support.transition.ViewOverlay.createFrom(r52)
            r0 = r46
            r0.add(r8)
            android.graphics.Rect r30 = new android.graphics.Rect
            r0 = r51
            int[] r0 = r0.tempLocation
            r46 = r0
            r47 = 0
            r46 = r46[r47]
            int r46 = r38 - r46
            r0 = r51
            int[] r0 = r0.tempLocation
            r47 = r0
            r48 = 1
            r47 = r47[r48]
            int r47 = r39 - r47
            r0 = r51
            int[] r0 = r0.tempLocation
            r48 = r0
            r49 = 0
            r48 = r48[r49]
            int r48 = r38 - r48
            int r49 = r44.getWidth()
            int r48 = r48 + r49
            r0 = r51
            int[] r0 = r0.tempLocation
            r49 = r0
            r50 = 1
            r49 = r49[r50]
            int r49 = r39 - r49
            int r50 = r44.getHeight()
            int r49 = r49 + r50
            r0 = r30
            r1 = r46
            r2 = r47
            r3 = r48
            r4 = r49
            r0.<init>(r1, r2, r3, r4)
            android.graphics.Rect r11 = new android.graphics.Rect
            r0 = r51
            int[] r0 = r0.tempLocation
            r46 = r0
            r47 = 0
            r46 = r46[r47]
            int r46 = r19 - r46
            r0 = r51
            int[] r0 = r0.tempLocation
            r47 = r0
            r48 = 1
            r47 = r47[r48]
            int r47 = r20 - r47
            r0 = r51
            int[] r0 = r0.tempLocation
            r48 = r0
            r49 = 0
            r48 = r48[r49]
            int r48 = r19 - r48
            int r49 = r44.getWidth()
            int r48 = r48 + r49
            r0 = r51
            int[] r0 = r0.tempLocation
            r49 = r0
            r50 = 1
            r49 = r49[r50]
            int r49 = r20 - r49
            int r50 = r44.getHeight()
            int r49 = r49 + r50
            r0 = r46
            r1 = r47
            r2 = r48
            r3 = r49
            r11.<init>(r0, r1, r2, r3)
            java.lang.String r46 = "bounds"
            android.support.transition.RectEvaluator r47 = sRectEvaluator
            r48 = 2
            r0 = r48
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r48 = r0
            r49 = 0
            r48[r49] = r30
            r49 = 1
            r48[r49] = r11
            r0 = r46
            r1 = r47
            r2 = r48
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofObject(r8, r0, r1, r2)
            android.support.transition.ChangeBoundsPort$4 r46 = new android.support.transition.ChangeBoundsPort$4
            r0 = r46
            r1 = r51
            r2 = r52
            r3 = r44
            r0.<init>(r2, r8, r3)
            r0 = r46
            r5.addListener(r0)
            goto L_0x0005
        L_0x0436:
            r5 = 0
            goto L_0x0005
        L_0x0439:
            r26 = r27
            goto L_0x027e
        L_0x043d:
            r27 = r26
            goto L_0x0258
        L_0x0441:
            r26 = r27
            goto L_0x0180
        L_0x0445:
            r27 = r26
            goto L_0x011e
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.transition.ChangeBoundsPort.createAnimator(android.view.ViewGroup, android.support.transition.TransitionValues, android.support.transition.TransitionValues):android.animation.Animator");
    }
}
