package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.C0004R;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.p000v4.p002os.ParcelableCompat;
import android.support.p000v4.p002os.ParcelableCompatCreatorCallbacks;
import android.support.p000v4.view.AbsSavedState;
import android.support.p000v4.view.OnApplyWindowInsetsListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@CoordinatorLayout.DefaultBehavior(Behavior.class)
public class AppBarLayout extends LinearLayout {
    private static final int INVALID_SCROLL_RANGE = -1;
    static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
    static final int PENDING_ACTION_COLLAPSED = 2;
    static final int PENDING_ACTION_EXPANDED = 1;
    static final int PENDING_ACTION_NONE = 0;
    private boolean mCollapsed;
    private boolean mCollapsible;
    private int mDownPreScrollRange;
    private int mDownScrollRange;
    private boolean mHaveChildWithInterpolator;
    private WindowInsetsCompat mLastInsets;
    private List<OnOffsetChangedListener> mListeners;
    private int mPendingAction;
    private final int[] mTmpStatesArray;
    private int mTotalScrollRange;

    public interface OnOffsetChangedListener {
        void onOffsetChanged(AppBarLayout appBarLayout, int i);
    }

    public AppBarLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTotalScrollRange = -1;
        this.mDownPreScrollRange = -1;
        this.mDownScrollRange = -1;
        this.mPendingAction = 0;
        this.mTmpStatesArray = new int[2];
        setOrientation(1);
        ThemeUtils.checkAppCompatTheme(context);
        if (Build.VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
            ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, attrs, 0, C0004R.style.Widget_Design_AppBarLayout);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, C0004R.styleable.AppBarLayout, 0, C0004R.style.Widget_Design_AppBarLayout);
        ViewCompat.setBackground(this, a.getDrawable(C0004R.styleable.AppBarLayout_android_background));
        if (a.hasValue(C0004R.styleable.AppBarLayout_expanded)) {
            setExpanded(a.getBoolean(C0004R.styleable.AppBarLayout_expanded, false));
        }
        if (Build.VERSION.SDK_INT >= 21 && a.hasValue(C0004R.styleable.AppBarLayout_elevation)) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, (float) a.getDimensionPixelSize(C0004R.styleable.AppBarLayout_elevation, 0));
        }
        a.recycle();
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                return AppBarLayout.this.onWindowInsetChanged(insets);
            }
        });
    }

    public void addOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList();
        }
        if (listener != null && !this.mListeners.contains(listener)) {
            this.mListeners.add(listener);
        }
    }

    public void removeOnOffsetChangedListener(OnOffsetChangedListener listener) {
        if (this.mListeners != null && listener != null) {
            this.mListeners.remove(listener);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateScrollRanges();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidateScrollRanges();
        this.mHaveChildWithInterpolator = false;
        int i = 0;
        int z = getChildCount();
        while (true) {
            if (i >= z) {
                break;
            } else if (((LayoutParams) getChildAt(i).getLayoutParams()).getScrollInterpolator() != null) {
                this.mHaveChildWithInterpolator = true;
                break;
            } else {
                i++;
            }
        }
        updateCollapsible();
    }

    private void updateCollapsible() {
        boolean haveCollapsibleChild = false;
        int i = 0;
        int z = getChildCount();
        while (true) {
            if (i >= z) {
                break;
            } else if (((LayoutParams) getChildAt(i).getLayoutParams()).isCollapsible()) {
                haveCollapsibleChild = true;
                break;
            } else {
                i++;
            }
        }
        setCollapsibleState(haveCollapsibleChild);
    }

    private void invalidateScrollRanges() {
        this.mTotalScrollRange = -1;
        this.mDownPreScrollRange = -1;
        this.mDownScrollRange = -1;
    }

    public void setOrientation(int orientation) {
        if (orientation != 1) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
        }
        super.setOrientation(orientation);
    }

    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(this));
    }

    public void setExpanded(boolean expanded, boolean animate) {
        this.mPendingAction = (animate ? 4 : 0) | (expanded ? 1 : 2);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -2);
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (Build.VERSION.SDK_INT >= 19 && (p instanceof LinearLayout.LayoutParams)) {
            return new LayoutParams((LinearLayout.LayoutParams) p);
        }
        if (p instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    /* access modifiers changed from: package-private */
    public boolean hasChildWithInterpolator() {
        return this.mHaveChildWithInterpolator;
    }

    public final int getTotalScrollRange() {
        if (this.mTotalScrollRange != -1) {
            return this.mTotalScrollRange;
        }
        int range = 0;
        int i = 0;
        int z = getChildCount();
        while (true) {
            if (i >= z) {
                break;
            }
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += lp.topMargin + childHeight + lp.bottomMargin;
            if ((flags & 2) != 0) {
                range -= ViewCompat.getMinimumHeight(child);
                break;
            }
            i++;
        }
        int max = Math.max(0, range - getTopInset());
        this.mTotalScrollRange = max;
        return max;
    }

    /* access modifiers changed from: package-private */
    public boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    /* access modifiers changed from: package-private */
    public int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    /* access modifiers changed from: package-private */
    public int getDownNestedPreScrollRange() {
        if (this.mDownPreScrollRange != -1) {
            return this.mDownPreScrollRange;
        }
        int range = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            int flags = lp.mScrollFlags;
            if ((flags & 5) == 5) {
                int range2 = range + lp.topMargin + lp.bottomMargin;
                if ((flags & 8) != 0) {
                    range = range2 + ViewCompat.getMinimumHeight(child);
                } else if ((flags & 2) != 0) {
                    range = range2 + (childHeight - ViewCompat.getMinimumHeight(child));
                } else {
                    range = range2 + childHeight;
                }
            } else if (range > 0) {
                break;
            }
        }
        int max = Math.max(0, range);
        this.mDownPreScrollRange = max;
        return max;
    }

    /* access modifiers changed from: package-private */
    public int getDownNestedScrollRange() {
        if (this.mDownScrollRange != -1) {
            return this.mDownScrollRange;
        }
        int range = 0;
        int i = 0;
        int z = getChildCount();
        while (true) {
            if (i >= z) {
                break;
            }
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            int flags = lp.mScrollFlags;
            if ((flags & 1) == 0) {
                break;
            }
            range += childHeight;
            if ((flags & 2) != 0) {
                range -= ViewCompat.getMinimumHeight(child) + getTopInset();
                break;
            }
            i++;
        }
        int max = Math.max(0, range);
        this.mDownScrollRange = max;
        return max;
    }

    /* access modifiers changed from: package-private */
    public void dispatchOffsetUpdates(int offset) {
        if (this.mListeners != null) {
            int z = this.mListeners.size();
            for (int i = 0; i < z; i++) {
                OnOffsetChangedListener listener = this.mListeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final int getMinimumHeightForVisibleOverlappingContent() {
        int topInset = getTopInset();
        int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight != 0) {
            return (minHeight * 2) + topInset;
        }
        int childCount = getChildCount();
        int lastChildMinHeight = childCount >= 1 ? ViewCompat.getMinimumHeight(getChildAt(childCount - 1)) : 0;
        if (lastChildMinHeight != 0) {
            return (lastChildMinHeight * 2) + topInset;
        }
        return getHeight() / 3;
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int extraSpace) {
        int[] extraStates = this.mTmpStatesArray;
        int[] states = super.onCreateDrawableState(extraStates.length + extraSpace);
        extraStates[0] = this.mCollapsible ? C0004R.attr.state_collapsible : -C0004R.attr.state_collapsible;
        extraStates[1] = (!this.mCollapsible || !this.mCollapsed) ? -C0004R.attr.state_collapsed : C0004R.attr.state_collapsed;
        return mergeDrawableStates(states, extraStates);
    }

    private boolean setCollapsibleState(boolean collapsible) {
        if (this.mCollapsible == collapsible) {
            return false;
        }
        this.mCollapsible = collapsible;
        refreshDrawableState();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setCollapsedState(boolean collapsed) {
        if (this.mCollapsed == collapsed) {
            return false;
        }
        this.mCollapsed = collapsed;
        refreshDrawableState();
        return true;
    }

    @Deprecated
    public void setTargetElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, elevation);
        }
    }

    @Deprecated
    public float getTargetElevation() {
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public int getPendingAction() {
        return this.mPendingAction;
    }

    /* access modifiers changed from: package-private */
    public void resetPendingAction() {
        this.mPendingAction = 0;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public final int getTopInset() {
        if (this.mLastInsets != null) {
            return this.mLastInsets.getSystemWindowInsetTop();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!ViewUtils.objectEquals(this.mLastInsets, newInsets)) {
            this.mLastInsets = newInsets;
            invalidateScrollRanges();
        }
        return insets;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        static final int COLLAPSIBLE_FLAGS = 10;
        static final int FLAG_QUICK_RETURN = 5;
        static final int FLAG_SNAP = 17;
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
        public static final int SCROLL_FLAG_SCROLL = 1;
        public static final int SCROLL_FLAG_SNAP = 16;
        int mScrollFlags = 1;
        Interpolator mScrollInterpolator;

        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ScrollFlags {
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, C0004R.styleable.AppBarLayout_Layout);
            this.mScrollFlags = a.getInt(C0004R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(C0004R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                this.mScrollInterpolator = AnimationUtils.loadInterpolator(c, a.getResourceId(C0004R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0));
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        @RequiresApi(19)
        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        @TargetApi(19)
        @RequiresApi(19)
        public LayoutParams(LayoutParams source) {
            super(source);
            this.mScrollFlags = source.mScrollFlags;
            this.mScrollInterpolator = source.mScrollInterpolator;
        }

        public void setScrollFlags(int flags) {
            this.mScrollFlags = flags;
        }

        public int getScrollFlags() {
            return this.mScrollFlags;
        }

        public void setScrollInterpolator(Interpolator interpolator) {
            this.mScrollInterpolator = interpolator;
        }

        public Interpolator getScrollInterpolator() {
            return this.mScrollInterpolator;
        }

        /* access modifiers changed from: package-private */
        public boolean isCollapsible() {
            return (this.mScrollFlags & 1) == 1 && (this.mScrollFlags & 10) != 0;
        }
    }

    public static class Behavior extends HeaderBehavior<AppBarLayout> {
        private static final int INVALID_POSITION = -1;
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
        private WeakReference<View> mLastNestedScrollingChildRef;
        private ValueAnimatorCompat mOffsetAnimator;
        /* access modifiers changed from: private */
        public int mOffsetDelta;
        private int mOffsetToChildIndexOnLayout = -1;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;
        private DragCallback mOnDragCallback;
        private boolean mSkipNestedPreScroll;
        private boolean mWasNestedFlung;

        public static abstract class DragCallback {
            public abstract boolean canDrag(@NonNull AppBarLayout appBarLayout);
        }

        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public Behavior() {
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
            boolean started = (nestedScrollAxes & 2) != 0 && child.hasScrollableChildren() && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();
            if (started && this.mOffsetAnimator != null) {
                this.mOffsetAnimator.cancel();
            }
            this.mLastNestedScrollingChildRef = null;
            return started;
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {
            int min;
            int max;
            if (dy != 0 && !this.mSkipNestedPreScroll) {
                if (dy < 0) {
                    min = -child.getTotalScrollRange();
                    max = min + child.getDownNestedPreScrollRange();
                } else {
                    min = -child.getUpNestedPreScrollRange();
                    max = 0;
                }
                consumed[1] = scroll(coordinatorLayout, child, dy, min, max);
            }
        }

        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            if (dyUnconsumed < 0) {
                scroll(coordinatorLayout, child, dyUnconsumed, -child.getDownNestedScrollRange(), 0);
                this.mSkipNestedPreScroll = true;
                return;
            }
            this.mSkipNestedPreScroll = false;
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
            if (!this.mWasNestedFlung) {
                snapToChildIfNeeded(coordinatorLayout, abl);
            }
            this.mSkipNestedPreScroll = false;
            this.mWasNestedFlung = false;
            this.mLastNestedScrollingChildRef = new WeakReference<>(target);
        }

        public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {
            boolean flung = false;
            if (!consumed) {
                flung = fling(coordinatorLayout, child, -child.getTotalScrollRange(), 0, -velocityY);
            } else if (velocityY < 0.0f) {
                int targetScroll = (-child.getTotalScrollRange()) + child.getDownNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() < targetScroll) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                    flung = true;
                }
            } else {
                int targetScroll2 = -child.getUpNestedPreScrollRange();
                if (getTopBottomOffsetForScrollingSibling() > targetScroll2) {
                    animateOffsetTo(coordinatorLayout, child, targetScroll2, velocityY);
                    flung = true;
                }
            }
            this.mWasNestedFlung = flung;
            return flung;
        }

        public void setDragCallback(@Nullable DragCallback callback) {
            this.mOnDragCallback = callback;
        }

        private void animateOffsetTo(CoordinatorLayout coordinatorLayout, AppBarLayout child, int offset, float velocity) {
            int duration;
            int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - offset);
            float velocity2 = Math.abs(velocity);
            if (velocity2 > 0.0f) {
                duration = Math.round(1000.0f * (((float) distance) / velocity2)) * 3;
            } else {
                duration = (int) ((1.0f + (((float) distance) / ((float) child.getHeight()))) * 150.0f);
            }
            animateOffsetWithDuration(coordinatorLayout, child, offset, duration);
        }

        private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout, final AppBarLayout child, int offset, int duration) {
            int currentOffset = getTopBottomOffsetForScrollingSibling();
            if (currentOffset != offset) {
                if (this.mOffsetAnimator == null) {
                    this.mOffsetAnimator = ViewUtils.createAnimator();
                    this.mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                    this.mOffsetAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimatorCompat animator) {
                            Behavior.this.setHeaderTopBottomOffset(coordinatorLayout, child, animator.getAnimatedIntValue());
                        }
                    });
                } else {
                    this.mOffsetAnimator.cancel();
                }
                this.mOffsetAnimator.setDuration((long) Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
                this.mOffsetAnimator.setIntValues(currentOffset, offset);
                this.mOffsetAnimator.start();
            } else if (this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning()) {
                this.mOffsetAnimator.cancel();
            }
        }

        private int getChildIndexOnOffset(AppBarLayout abl, int offset) {
            int count = abl.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = abl.getChildAt(i);
                if (child.getTop() <= (-offset) && child.getBottom() >= (-offset)) {
                    return i;
                }
            }
            return -1;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
            int newOffset;
            int offset = getTopBottomOffsetForScrollingSibling();
            int offsetChildIndex = getChildIndexOnOffset(abl, offset);
            if (offsetChildIndex >= 0) {
                View offsetChild = abl.getChildAt(offsetChildIndex);
                int flags = ((LayoutParams) offsetChild.getLayoutParams()).getScrollFlags();
                if ((flags & 17) == 17) {
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();
                    if (offsetChildIndex == abl.getChildCount() - 1) {
                        snapBottom += abl.getTopInset();
                    }
                    if (checkFlag(flags, 2)) {
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, 5)) {
                        int seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }
                    if (offset < (snapBottom + snapTop) / 2) {
                        newOffset = snapBottom;
                    } else {
                        newOffset = snapTop;
                    }
                    animateOffsetTo(coordinatorLayout, abl, MathUtils.constrain(newOffset, -abl.getTotalScrollRange(), 0), 0.0f);
                }
            }
        }

        private static boolean checkFlag(int flags, int check) {
            return (flags & check) == check;
        }

        public boolean onMeasureChild(CoordinatorLayout parent, AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            if (((CoordinatorLayout.LayoutParams) child.getLayoutParams()).height != -2) {
                return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            }
            parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, View.MeasureSpec.makeMeasureSpec(0, 0), heightUsed);
            return true;
        }

        public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
            int offset;
            boolean handled = super.onLayoutChild(parent, abl, layoutDirection);
            int pendingAction = abl.getPendingAction();
            if (pendingAction != 0) {
                boolean animate = (pendingAction & 4) != 0;
                if ((pendingAction & 2) != 0) {
                    int offset2 = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        animateOffsetTo(parent, abl, offset2, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, offset2);
                    }
                } else if ((pendingAction & 1) != 0) {
                    if (animate) {
                        animateOffsetTo(parent, abl, 0, 0.0f);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, 0);
                    }
                }
            } else if (this.mOffsetToChildIndexOnLayout >= 0) {
                View child = abl.getChildAt(this.mOffsetToChildIndexOnLayout);
                int offset3 = -child.getBottom();
                if (this.mOffsetToChildIndexOnLayoutIsMinHeight) {
                    offset = offset3 + ViewCompat.getMinimumHeight(child);
                } else {
                    offset = offset3 + Math.round(((float) child.getHeight()) * this.mOffsetToChildIndexOnLayoutPerc);
                }
                setTopAndBottomOffset(offset);
            }
            abl.resetPendingAction();
            this.mOffsetToChildIndexOnLayout = -1;
            setTopAndBottomOffset(MathUtils.constrain(getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0));
            abl.dispatchOffsetUpdates(getTopAndBottomOffset());
            return handled;
        }

        /* access modifiers changed from: package-private */
        public boolean canDragView(AppBarLayout view) {
            if (this.mOnDragCallback != null) {
                return this.mOnDragCallback.canDrag(view);
            }
            if (this.mLastNestedScrollingChildRef == null) {
                return true;
            }
            View scrollingView = (View) this.mLastNestedScrollingChildRef.get();
            if (scrollingView == null || !scrollingView.isShown() || ViewCompat.canScrollVertically(scrollingView, -1)) {
                return false;
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public void onFlingFinished(CoordinatorLayout parent, AppBarLayout layout) {
            snapToChildIfNeeded(parent, layout);
        }

        /* access modifiers changed from: package-private */
        public int getMaxDragOffset(AppBarLayout view) {
            return -view.getDownNestedScrollRange();
        }

        /* access modifiers changed from: package-private */
        public int getScrollRangeForDragFling(AppBarLayout view) {
            return view.getTotalScrollRange();
        }

        /* access modifiers changed from: package-private */
        public int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, int newOffset, int minOffset, int maxOffset) {
            int curOffset = getTopBottomOffsetForScrollingSibling();
            int consumed = 0;
            if (minOffset == 0 || curOffset < minOffset || curOffset > maxOffset) {
                this.mOffsetDelta = 0;
            } else {
                int newOffset2 = MathUtils.constrain(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset2) {
                    int interpolatedOffset = appBarLayout.hasChildWithInterpolator() ? interpolateOffset(appBarLayout, newOffset2) : newOffset2;
                    boolean offsetChanged = setTopAndBottomOffset(interpolatedOffset);
                    consumed = curOffset - newOffset2;
                    this.mOffsetDelta = newOffset2 - interpolatedOffset;
                    if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
                    }
                    appBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
                    updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset2, newOffset2 < curOffset ? -1 : 1);
                }
            }
            return consumed;
        }

        /* access modifiers changed from: package-private */
        @VisibleForTesting
        public boolean isOffsetAnimatorRunning() {
            return this.mOffsetAnimator != null && this.mOffsetAnimator.isRunning();
        }

        private int interpolateOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int i = 0;
            int z = layout.getChildCount();
            while (i < z) {
                View child = layout.getChildAt(i);
                LayoutParams childLp = (LayoutParams) child.getLayoutParams();
                Interpolator interpolator = childLp.getScrollInterpolator();
                if (absOffset < child.getTop() || absOffset > child.getBottom()) {
                    i++;
                } else if (interpolator == null) {
                    return offset;
                } else {
                    int childScrollableHeight = 0;
                    int flags = childLp.getScrollFlags();
                    if ((flags & 1) != 0) {
                        childScrollableHeight = 0 + child.getHeight() + childLp.topMargin + childLp.bottomMargin;
                        if ((flags & 2) != 0) {
                            childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                        }
                    }
                    if (ViewCompat.getFitsSystemWindows(child)) {
                        childScrollableHeight -= layout.getTopInset();
                    }
                    if (childScrollableHeight <= 0) {
                        return offset;
                    }
                    return Integer.signum(offset) * (child.getTop() + Math.round(((float) childScrollableHeight) * interpolator.getInterpolation(((float) (absOffset - child.getTop())) / ((float) childScrollableHeight))));
                }
            }
            return offset;
        }

        private void updateAppBarLayoutDrawableState(CoordinatorLayout parent, AppBarLayout layout, int offset, int direction) {
            View child = getAppBarChildOnOffset(layout, offset);
            if (child != null) {
                int flags = ((LayoutParams) child.getLayoutParams()).getScrollFlags();
                boolean collapsed = false;
                if ((flags & 1) != 0) {
                    int minHeight = ViewCompat.getMinimumHeight(child);
                    if (direction > 0 && (flags & 12) != 0) {
                        collapsed = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    } else if ((flags & 2) != 0) {
                        collapsed = (-offset) >= (child.getBottom() - minHeight) - layout.getTopInset();
                    }
                }
                if (layout.setCollapsedState(collapsed) && Build.VERSION.SDK_INT >= 11 && shouldJumpElevationState(parent, layout)) {
                    layout.jumpDrawablesToCurrentState();
                }
            }
        }

        private boolean shouldJumpElevationState(CoordinatorLayout parent, AppBarLayout layout) {
            List<View> dependencies = parent.getDependents(layout);
            int i = 0;
            int size = dependencies.size();
            while (i < size) {
                CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) dependencies.get(i).getLayoutParams()).getBehavior();
                if (!(behavior instanceof ScrollingViewBehavior)) {
                    i++;
                } else if (((ScrollingViewBehavior) behavior).getOverlayTop() != 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        private static View getAppBarChildOnOffset(AppBarLayout layout, int offset) {
            int absOffset = Math.abs(offset);
            int z = layout.getChildCount();
            for (int i = 0; i < z; i++) {
                View child = layout.getChildAt(i);
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    return child;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + this.mOffsetDelta;
        }

        public Parcelable onSaveInstanceState(CoordinatorLayout parent, AppBarLayout abl) {
            Parcelable superState = super.onSaveInstanceState(parent, abl);
            int offset = getTopAndBottomOffset();
            int i = 0;
            int count = abl.getChildCount();
            while (i < count) {
                View child = abl.getChildAt(i);
                int visBottom = child.getBottom() + offset;
                if (child.getTop() + offset > 0 || visBottom < 0) {
                    i++;
                } else {
                    SavedState ss = new SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibleChildAtMinimumHeight = visBottom == ViewCompat.getMinimumHeight(child) + abl.getTopInset();
                    ss.firstVisibleChildPercentageShown = ((float) visBottom) / ((float) child.getHeight());
                    return ss;
                }
            }
            return superState;
        }

        public void onRestoreInstanceState(CoordinatorLayout parent, AppBarLayout appBarLayout, Parcelable state) {
            if (state instanceof SavedState) {
                SavedState ss = (SavedState) state;
                super.onRestoreInstanceState(parent, appBarLayout, ss.getSuperState());
                this.mOffsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                this.mOffsetToChildIndexOnLayoutPerc = ss.firstVisibleChildPercentageShown;
                this.mOffsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibleChildAtMinimumHeight;
                return;
            }
            super.onRestoreInstanceState(parent, appBarLayout, state);
            this.mOffsetToChildIndexOnLayout = -1;
        }

        protected static class SavedState extends AbsSavedState {
            public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
                public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                    return new SavedState(source, loader);
                }

                public SavedState[] newArray(int size) {
                    return new SavedState[size];
                }
            });
            boolean firstVisibleChildAtMinimumHeight;
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                this.firstVisibleChildIndex = source.readInt();
                this.firstVisibleChildPercentageShown = source.readFloat();
                this.firstVisibleChildAtMinimumHeight = source.readByte() != 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(this.firstVisibleChildIndex);
                dest.writeFloat(this.firstVisibleChildPercentageShown);
                dest.writeByte((byte) (this.firstVisibleChildAtMinimumHeight ? 1 : 0));
            }
        }
    }

    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {
        public /* bridge */ /* synthetic */ int getLeftAndRightOffset() {
            return super.getLeftAndRightOffset();
        }

        public /* bridge */ /* synthetic */ int getTopAndBottomOffset() {
            return super.getTopAndBottomOffset();
        }

        public /* bridge */ /* synthetic */ boolean onLayoutChild(CoordinatorLayout coordinatorLayout, View view, int i) {
            return super.onLayoutChild(coordinatorLayout, view, i);
        }

        public /* bridge */ /* synthetic */ boolean onMeasureChild(CoordinatorLayout coordinatorLayout, View view, int i, int i2, int i3, int i4) {
            return super.onMeasureChild(coordinatorLayout, view, i, i2, i3, i4);
        }

        public /* bridge */ /* synthetic */ boolean setLeftAndRightOffset(int i) {
            return super.setLeftAndRightOffset(i);
        }

        public /* bridge */ /* synthetic */ boolean setTopAndBottomOffset(int i) {
            return super.setTopAndBottomOffset(i);
        }

        public ScrollingViewBehavior() {
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, C0004R.styleable.ScrollingViewBehavior_Layout);
            setOverlayTop(a.getDimensionPixelSize(C0004R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return dependency instanceof AppBarLayout;
        }

        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child, Rect rectangle, boolean immediate) {
            boolean z;
            AppBarLayout header = findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                rectangle.offset(child.getLeft(), child.getTop());
                Rect parentRect = this.mTempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());
                if (!parentRect.contains(rectangle)) {
                    if (!immediate) {
                        z = true;
                    } else {
                        z = false;
                    }
                    header.setExpanded(false, z);
                    return true;
                }
            }
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
            if (behavior instanceof Behavior) {
                ViewCompat.offsetTopAndBottom(child, (((dependency.getBottom() - child.getTop()) + ((Behavior) behavior).mOffsetDelta) + getVerticalLayoutGap()) - getOverlapPixelsForOffset(dependency));
            }
        }

        /* access modifiers changed from: package-private */
        public float getOverlapRatioForOffset(View header) {
            int availScrollRange;
            if (!(header instanceof AppBarLayout)) {
                return 0.0f;
            }
            AppBarLayout abl = (AppBarLayout) header;
            int totalScrollRange = abl.getTotalScrollRange();
            int preScrollDown = abl.getDownNestedPreScrollRange();
            int offset = getAppBarLayoutOffset(abl);
            if ((preScrollDown == 0 || totalScrollRange + offset > preScrollDown) && (availScrollRange = totalScrollRange - preScrollDown) != 0) {
                return 1.0f + (((float) offset) / ((float) availScrollRange));
            }
            return 0.0f;
        }

        private static int getAppBarLayoutOffset(AppBarLayout abl) {
            CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) abl.getLayoutParams()).getBehavior();
            if (behavior instanceof Behavior) {
                return ((Behavior) behavior).getTopBottomOffsetForScrollingSibling();
            }
            return 0;
        }

        /* access modifiers changed from: package-private */
        public AppBarLayout findFirstDependency(List<View> views) {
            int z = views.size();
            for (int i = 0; i < z; i++) {
                View view = views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public int getScrollRange(View v) {
            if (v instanceof AppBarLayout) {
                return ((AppBarLayout) v).getTotalScrollRange();
            }
            return super.getScrollRange(v);
        }
    }
}
