package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.C0004R;
import android.support.design.widget.ValueAnimatorCompat;
import android.support.p000v4.util.Pools;
import android.support.p000v4.view.GravityCompat;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.PointerIconCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.widget.TextViewCompat;
import android.support.p003v7.app.ActionBar;
import android.support.p003v7.appcompat.C0267R;
import android.support.p003v7.content.res.AppCompatResources;
import android.support.p003v7.widget.ActivityChooserView;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@ViewPager.DecorView
public class TabLayout extends HorizontalScrollView {
    private static final int ANIMATION_DURATION = 300;
    static final int DEFAULT_GAP_TEXT_ICON = 8;
    private static final int DEFAULT_HEIGHT = 48;
    private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
    static final int FIXED_WRAP_GUTTER_MIN = 16;
    public static final int GRAVITY_CENTER = 1;
    public static final int GRAVITY_FILL = 0;
    private static final int INVALID_WIDTH = -1;
    public static final int MODE_FIXED = 1;
    public static final int MODE_SCROLLABLE = 0;
    static final int MOTION_NON_ADJACENT_OFFSET = 24;
    private static final int TAB_MIN_WIDTH_MARGIN = 56;
    private static final Pools.Pool<Tab> sTabPool = new Pools.SynchronizedPool(16);
    private AdapterChangeListener mAdapterChangeListener;
    private int mContentInsetStart;
    private OnTabSelectedListener mCurrentVpSelectedListener;
    int mMode;
    private TabLayoutOnPageChangeListener mPageChangeListener;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private final int mRequestedTabMaxWidth;
    private final int mRequestedTabMinWidth;
    private ValueAnimatorCompat mScrollAnimator;
    private final int mScrollableTabMinWidth;
    private OnTabSelectedListener mSelectedListener;
    private final ArrayList<OnTabSelectedListener> mSelectedListeners;
    private Tab mSelectedTab;
    private boolean mSetupViewPagerImplicitly;
    final int mTabBackgroundResId;
    int mTabGravity;
    int mTabMaxWidth;
    int mTabPaddingBottom;
    int mTabPaddingEnd;
    int mTabPaddingStart;
    int mTabPaddingTop;
    private final SlidingTabStrip mTabStrip;
    int mTabTextAppearance;
    ColorStateList mTabTextColors;
    float mTabTextMultiLineSize;
    float mTabTextSize;
    private final Pools.Pool<TabView> mTabViewPool;
    private final ArrayList<Tab> mTabs;
    ViewPager mViewPager;

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public interface OnTabSelectedListener {
        void onTabReselected(Tab tab);

        void onTabSelected(Tab tab);

        void onTabUnselected(Tab tab);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabGravity {
    }

    public TabLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* JADX INFO: finally extract failed */
    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTabs = new ArrayList<>();
        this.mTabMaxWidth = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mSelectedListeners = new ArrayList<>();
        this.mTabViewPool = new Pools.SimplePool(12);
        ThemeUtils.checkAppCompatTheme(context);
        setHorizontalScrollBarEnabled(false);
        this.mTabStrip = new SlidingTabStrip(context);
        super.addView(this.mTabStrip, 0, new FrameLayout.LayoutParams(-2, -1));
        TypedArray a = context.obtainStyledAttributes(attrs, C0004R.styleable.TabLayout, defStyleAttr, C0004R.style.Widget_Design_TabLayout);
        this.mTabStrip.setSelectedIndicatorHeight(a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabIndicatorHeight, 0));
        this.mTabStrip.setSelectedIndicatorColor(a.getColor(C0004R.styleable.TabLayout_tabIndicatorColor, 0));
        int dimensionPixelSize = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabPadding, 0);
        this.mTabPaddingBottom = dimensionPixelSize;
        this.mTabPaddingEnd = dimensionPixelSize;
        this.mTabPaddingTop = dimensionPixelSize;
        this.mTabPaddingStart = dimensionPixelSize;
        this.mTabPaddingStart = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabPaddingStart, this.mTabPaddingStart);
        this.mTabPaddingTop = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabPaddingTop, this.mTabPaddingTop);
        this.mTabPaddingEnd = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabPaddingEnd, this.mTabPaddingEnd);
        this.mTabPaddingBottom = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabPaddingBottom, this.mTabPaddingBottom);
        this.mTabTextAppearance = a.getResourceId(C0004R.styleable.TabLayout_tabTextAppearance, C0004R.style.TextAppearance_Design_Tab);
        TypedArray ta = context.obtainStyledAttributes(this.mTabTextAppearance, C0267R.styleable.TextAppearance);
        try {
            this.mTabTextSize = (float) ta.getDimensionPixelSize(C0267R.styleable.TextAppearance_android_textSize, 0);
            this.mTabTextColors = ta.getColorStateList(C0267R.styleable.TextAppearance_android_textColor);
            ta.recycle();
            if (a.hasValue(C0004R.styleable.TabLayout_tabTextColor)) {
                this.mTabTextColors = a.getColorStateList(C0004R.styleable.TabLayout_tabTextColor);
            }
            if (a.hasValue(C0004R.styleable.TabLayout_tabSelectedTextColor)) {
                this.mTabTextColors = createColorStateList(this.mTabTextColors.getDefaultColor(), a.getColor(C0004R.styleable.TabLayout_tabSelectedTextColor, 0));
            }
            this.mRequestedTabMinWidth = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabMinWidth, -1);
            this.mRequestedTabMaxWidth = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabMaxWidth, -1);
            this.mTabBackgroundResId = a.getResourceId(C0004R.styleable.TabLayout_tabBackground, 0);
            this.mContentInsetStart = a.getDimensionPixelSize(C0004R.styleable.TabLayout_tabContentStart, 0);
            this.mMode = a.getInt(C0004R.styleable.TabLayout_tabMode, 1);
            this.mTabGravity = a.getInt(C0004R.styleable.TabLayout_tabGravity, 0);
            a.recycle();
            Resources res = getResources();
            this.mTabTextMultiLineSize = (float) res.getDimensionPixelSize(C0004R.dimen.design_tab_text_size_2line);
            this.mScrollableTabMinWidth = res.getDimensionPixelSize(C0004R.dimen.design_tab_scrollable_min_width);
            applyModeAndGravity();
        } catch (Throwable th) {
            ta.recycle();
            throw th;
        }
    }

    public void setSelectedTabIndicatorColor(@ColorInt int color) {
        this.mTabStrip.setSelectedIndicatorColor(color);
    }

    public void setSelectedTabIndicatorHeight(int height) {
        this.mTabStrip.setSelectedIndicatorHeight(height);
    }

    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText) {
        setScrollPosition(position, positionOffset, updateSelectedText, true);
    }

    /* access modifiers changed from: package-private */
    public void setScrollPosition(int position, float positionOffset, boolean updateSelectedText, boolean updateIndicatorPosition) {
        int roundedPosition = Math.round(((float) position) + positionOffset);
        if (roundedPosition >= 0 && roundedPosition < this.mTabStrip.getChildCount()) {
            if (updateIndicatorPosition) {
                this.mTabStrip.setIndicatorPositionFromTabPosition(position, positionOffset);
            }
            if (this.mScrollAnimator != null && this.mScrollAnimator.isRunning()) {
                this.mScrollAnimator.cancel();
            }
            scrollTo(calculateScrollXForTab(position, positionOffset), 0);
            if (updateSelectedText) {
                setSelectedTabView(roundedPosition);
            }
        }
    }

    private float getScrollPosition() {
        return this.mTabStrip.getIndicatorPosition();
    }

    public void addTab(@NonNull Tab tab) {
        addTab(tab, this.mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, int position) {
        addTab(tab, position, this.mTabs.isEmpty());
    }

    public void addTab(@NonNull Tab tab, boolean setSelected) {
        addTab(tab, this.mTabs.size(), setSelected);
    }

    public void addTab(@NonNull Tab tab, int position, boolean setSelected) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }
        configureTab(tab, position);
        addTabView(tab);
        if (setSelected) {
            tab.select();
        }
    }

    private void addTabFromItemView(@NonNull TabItem item) {
        Tab tab = newTab();
        if (item.mText != null) {
            tab.setText(item.mText);
        }
        if (item.mIcon != null) {
            tab.setIcon(item.mIcon);
        }
        if (item.mCustomLayout != 0) {
            tab.setCustomView(item.mCustomLayout);
        }
        if (!TextUtils.isEmpty(item.getContentDescription())) {
            tab.setContentDescription(item.getContentDescription());
        }
        addTab(tab);
    }

    @Deprecated
    public void setOnTabSelectedListener(@Nullable OnTabSelectedListener listener) {
        if (this.mSelectedListener != null) {
            removeOnTabSelectedListener(this.mSelectedListener);
        }
        this.mSelectedListener = listener;
        if (listener != null) {
            addOnTabSelectedListener(listener);
        }
    }

    public void addOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        if (!this.mSelectedListeners.contains(listener)) {
            this.mSelectedListeners.add(listener);
        }
    }

    public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener listener) {
        this.mSelectedListeners.remove(listener);
    }

    public void clearOnTabSelectedListeners() {
        this.mSelectedListeners.clear();
    }

    @NonNull
    public Tab newTab() {
        Tab tab = sTabPool.acquire();
        if (tab == null) {
            tab = new Tab();
        }
        tab.mParent = this;
        tab.mView = createTabView(tab);
        return tab;
    }

    public int getTabCount() {
        return this.mTabs.size();
    }

    @Nullable
    public Tab getTabAt(int index) {
        if (index < 0 || index >= getTabCount()) {
            return null;
        }
        return this.mTabs.get(index);
    }

    public int getSelectedTabPosition() {
        if (this.mSelectedTab != null) {
            return this.mSelectedTab.getPosition();
        }
        return -1;
    }

    public void removeTab(Tab tab) {
        if (tab.mParent != this) {
            throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
        }
        removeTabAt(tab.getPosition());
    }

    public void removeTabAt(int position) {
        int selectedTabPosition;
        if (this.mSelectedTab != null) {
            selectedTabPosition = this.mSelectedTab.getPosition();
        } else {
            selectedTabPosition = 0;
        }
        removeTabViewAt(position);
        Tab removedTab = this.mTabs.remove(position);
        if (removedTab != null) {
            removedTab.reset();
            sTabPool.release(removedTab);
        }
        int newTabCount = this.mTabs.size();
        for (int i = position; i < newTabCount; i++) {
            this.mTabs.get(i).setPosition(i);
        }
        if (selectedTabPosition == position) {
            selectTab(this.mTabs.isEmpty() ? null : this.mTabs.get(Math.max(0, position - 1)));
        }
    }

    public void removeAllTabs() {
        for (int i = this.mTabStrip.getChildCount() - 1; i >= 0; i--) {
            removeTabViewAt(i);
        }
        Iterator<Tab> i2 = this.mTabs.iterator();
        while (i2.hasNext()) {
            Tab tab = i2.next();
            i2.remove();
            tab.reset();
            sTabPool.release(tab);
        }
        this.mSelectedTab = null;
    }

    public void setTabMode(int mode) {
        if (mode != this.mMode) {
            this.mMode = mode;
            applyModeAndGravity();
        }
    }

    public int getTabMode() {
        return this.mMode;
    }

    public void setTabGravity(int gravity) {
        if (this.mTabGravity != gravity) {
            this.mTabGravity = gravity;
            applyModeAndGravity();
        }
    }

    public int getTabGravity() {
        return this.mTabGravity;
    }

    public void setTabTextColors(@Nullable ColorStateList textColor) {
        if (this.mTabTextColors != textColor) {
            this.mTabTextColors = textColor;
            updateAllTabs();
        }
    }

    @Nullable
    public ColorStateList getTabTextColors() {
        return this.mTabTextColors;
    }

    public void setTabTextColors(int normalColor, int selectedColor) {
        setTabTextColors(createColorStateList(normalColor, selectedColor));
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        setupWithViewPager(viewPager, true);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh) {
        setupWithViewPager(viewPager, autoRefresh, false);
    }

    private void setupWithViewPager(@Nullable ViewPager viewPager, boolean autoRefresh, boolean implicitSetup) {
        if (this.mViewPager != null) {
            if (this.mPageChangeListener != null) {
                this.mViewPager.removeOnPageChangeListener(this.mPageChangeListener);
            }
            if (this.mAdapterChangeListener != null) {
                this.mViewPager.removeOnAdapterChangeListener(this.mAdapterChangeListener);
            }
        }
        if (this.mCurrentVpSelectedListener != null) {
            removeOnTabSelectedListener(this.mCurrentVpSelectedListener);
            this.mCurrentVpSelectedListener = null;
        }
        if (viewPager != null) {
            this.mViewPager = viewPager;
            if (this.mPageChangeListener == null) {
                this.mPageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            this.mPageChangeListener.reset();
            viewPager.addOnPageChangeListener(this.mPageChangeListener);
            this.mCurrentVpSelectedListener = new ViewPagerOnTabSelectedListener(viewPager);
            addOnTabSelectedListener(this.mCurrentVpSelectedListener);
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                setPagerAdapter(adapter, autoRefresh);
            }
            if (this.mAdapterChangeListener == null) {
                this.mAdapterChangeListener = new AdapterChangeListener();
            }
            this.mAdapterChangeListener.setAutoRefresh(autoRefresh);
            viewPager.addOnAdapterChangeListener(this.mAdapterChangeListener);
            setScrollPosition(viewPager.getCurrentItem(), 0.0f, true);
        } else {
            this.mViewPager = null;
            setPagerAdapter((PagerAdapter) null, false);
        }
        this.mSetupViewPagerImplicitly = implicitSetup;
    }

    @Deprecated
    public void setTabsFromPagerAdapter(@Nullable PagerAdapter adapter) {
        setPagerAdapter(adapter, false);
    }

    public boolean shouldDelayChildPressedState() {
        return getTabScrollRange() > 0;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mViewPager == null) {
            ViewParent vp = getParent();
            if (vp instanceof ViewPager) {
                setupWithViewPager((ViewPager) vp, true, true);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mSetupViewPagerImplicitly) {
            setupWithViewPager((ViewPager) null);
            this.mSetupViewPagerImplicitly = false;
        }
    }

    private int getTabScrollRange() {
        return Math.max(0, ((this.mTabStrip.getWidth() - getWidth()) - getPaddingLeft()) - getPaddingRight());
    }

    /* access modifiers changed from: package-private */
    public void setPagerAdapter(@Nullable PagerAdapter adapter, boolean addObserver) {
        if (!(this.mPagerAdapter == null || this.mPagerAdapterObserver == null)) {
            this.mPagerAdapter.unregisterDataSetObserver(this.mPagerAdapterObserver);
        }
        this.mPagerAdapter = adapter;
        if (addObserver && adapter != null) {
            if (this.mPagerAdapterObserver == null) {
                this.mPagerAdapterObserver = new PagerAdapterObserver();
            }
            adapter.registerDataSetObserver(this.mPagerAdapterObserver);
        }
        populateFromPagerAdapter();
    }

    /* access modifiers changed from: package-private */
    public void populateFromPagerAdapter() {
        int curItem;
        removeAllTabs();
        if (this.mPagerAdapter != null) {
            int adapterCount = this.mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                addTab(newTab().setText(this.mPagerAdapter.getPageTitle(i)), false);
            }
            if (this.mViewPager != null && adapterCount > 0 && (curItem = this.mViewPager.getCurrentItem()) != getSelectedTabPosition() && curItem < getTabCount()) {
                selectTab(getTabAt(curItem));
            }
        }
    }

    private void updateAllTabs() {
        int z = this.mTabs.size();
        for (int i = 0; i < z; i++) {
            this.mTabs.get(i).updateView();
        }
    }

    private TabView createTabView(@NonNull Tab tab) {
        TabView tabView = this.mTabViewPool != null ? this.mTabViewPool.acquire() : null;
        if (tabView == null) {
            tabView = new TabView(getContext());
        }
        tabView.setTab(tab);
        tabView.setFocusable(true);
        tabView.setMinimumWidth(getTabMinWidth());
        return tabView;
    }

    private void configureTab(Tab tab, int position) {
        tab.setPosition(position);
        this.mTabs.add(position, tab);
        int count = this.mTabs.size();
        for (int i = position + 1; i < count; i++) {
            this.mTabs.get(i).setPosition(i);
        }
    }

    private void addTabView(Tab tab) {
        this.mTabStrip.addView(tab.mView, tab.getPosition(), createLayoutParamsForTabs());
    }

    public void addView(View child) {
        addViewInternal(child);
    }

    public void addView(View child, int index) {
        addViewInternal(child);
    }

    public void addView(View child, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addViewInternal(child);
    }

    private void addViewInternal(View child) {
        if (child instanceof TabItem) {
            addTabFromItemView((TabItem) child);
            return;
        }
        throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
    }

    private LinearLayout.LayoutParams createLayoutParamsForTabs() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -1);
        updateTabViewLayoutParams(lp);
        return lp;
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams lp) {
        if (this.mMode == 1 && this.mTabGravity == 0) {
            lp.width = 0;
            lp.weight = 1.0f;
            return;
        }
        lp.width = -2;
        lp.weight = 0.0f;
    }

    /* access modifiers changed from: package-private */
    public int dpToPx(int dps) {
        return Math.round(getResources().getDisplayMetrics().density * ((float) dps));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dpToPx;
        int idealHeight = dpToPx(getDefaultHeight()) + getPaddingTop() + getPaddingBottom();
        switch (View.MeasureSpec.getMode(heightMeasureSpec)) {
            case Integer.MIN_VALUE:
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(Math.min(idealHeight, View.MeasureSpec.getSize(heightMeasureSpec)), 1073741824);
                break;
            case 0:
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(idealHeight, 1073741824);
                break;
        }
        int specWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        if (View.MeasureSpec.getMode(widthMeasureSpec) != 0) {
            if (this.mRequestedTabMaxWidth > 0) {
                dpToPx = this.mRequestedTabMaxWidth;
            } else {
                dpToPx = specWidth - dpToPx(56);
            }
            this.mTabMaxWidth = dpToPx;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() == 1) {
            View child = getChildAt(0);
            boolean remeasure = false;
            switch (this.mMode) {
                case 0:
                    if (child.getMeasuredWidth() >= getMeasuredWidth()) {
                        remeasure = false;
                        break;
                    } else {
                        remeasure = true;
                        break;
                    }
                case 1:
                    if (child.getMeasuredWidth() == getMeasuredWidth()) {
                        remeasure = false;
                        break;
                    } else {
                        remeasure = true;
                        break;
                    }
            }
            if (remeasure) {
                child.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), child.getLayoutParams().height));
            }
        }
    }

    private void removeTabViewAt(int position) {
        TabView view = (TabView) this.mTabStrip.getChildAt(position);
        this.mTabStrip.removeViewAt(position);
        if (view != null) {
            view.reset();
            this.mTabViewPool.release(view);
        }
        requestLayout();
    }

    private void animateToTab(int newPosition) {
        if (newPosition != -1) {
            if (getWindowToken() == null || !ViewCompat.isLaidOut(this) || this.mTabStrip.childrenNeedLayout()) {
                setScrollPosition(newPosition, 0.0f, true);
                return;
            }
            int startScrollX = getScrollX();
            int targetScrollX = calculateScrollXForTab(newPosition, 0.0f);
            if (startScrollX != targetScrollX) {
                if (this.mScrollAnimator == null) {
                    this.mScrollAnimator = ViewUtils.createAnimator();
                    this.mScrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                    this.mScrollAnimator.setDuration(300);
                    this.mScrollAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                        public void onAnimationUpdate(ValueAnimatorCompat animator) {
                            TabLayout.this.scrollTo(animator.getAnimatedIntValue(), 0);
                        }
                    });
                }
                this.mScrollAnimator.setIntValues(startScrollX, targetScrollX);
                this.mScrollAnimator.start();
            }
            this.mTabStrip.animateIndicatorToPosition(newPosition, ANIMATION_DURATION);
        }
    }

    private void setSelectedTabView(int position) {
        int tabCount = this.mTabStrip.getChildCount();
        if (position < tabCount) {
            int i = 0;
            while (i < tabCount) {
                this.mTabStrip.getChildAt(i).setSelected(i == position);
                i++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void selectTab(Tab tab) {
        selectTab(tab, true);
    }

    /* access modifiers changed from: package-private */
    public void selectTab(Tab tab, boolean updateIndicator) {
        int newPosition;
        Tab currentTab = this.mSelectedTab;
        if (currentTab != tab) {
            if (tab != null) {
                newPosition = tab.getPosition();
            } else {
                newPosition = -1;
            }
            if (updateIndicator) {
                if ((currentTab == null || currentTab.getPosition() == -1) && newPosition != -1) {
                    setScrollPosition(newPosition, 0.0f, true);
                } else {
                    animateToTab(newPosition);
                }
                if (newPosition != -1) {
                    setSelectedTabView(newPosition);
                }
            }
            if (currentTab != null) {
                dispatchTabUnselected(currentTab);
            }
            this.mSelectedTab = tab;
            if (tab != null) {
                dispatchTabSelected(tab);
            }
        } else if (currentTab != null) {
            dispatchTabReselected(tab);
            animateToTab(tab.getPosition());
        }
    }

    private void dispatchTabSelected(@NonNull Tab tab) {
        for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--) {
            this.mSelectedListeners.get(i).onTabSelected(tab);
        }
    }

    private void dispatchTabUnselected(@NonNull Tab tab) {
        for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--) {
            this.mSelectedListeners.get(i).onTabUnselected(tab);
        }
    }

    private void dispatchTabReselected(@NonNull Tab tab) {
        for (int i = this.mSelectedListeners.size() - 1; i >= 0; i--) {
            this.mSelectedListeners.get(i).onTabReselected(tab);
        }
    }

    private int calculateScrollXForTab(int position, float positionOffset) {
        int selectedWidth;
        int nextWidth = 0;
        if (this.mMode != 0) {
            return 0;
        }
        View selectedChild = this.mTabStrip.getChildAt(position);
        View nextChild = position + 1 < this.mTabStrip.getChildCount() ? this.mTabStrip.getChildAt(position + 1) : null;
        if (selectedChild != null) {
            selectedWidth = selectedChild.getWidth();
        } else {
            selectedWidth = 0;
        }
        if (nextChild != null) {
            nextWidth = nextChild.getWidth();
        }
        return ((selectedChild.getLeft() + ((int) ((((float) (selectedWidth + nextWidth)) * positionOffset) * 0.5f))) + (selectedChild.getWidth() / 2)) - (getWidth() / 2);
    }

    private void applyModeAndGravity() {
        int paddingStart = 0;
        if (this.mMode == 0) {
            paddingStart = Math.max(0, this.mContentInsetStart - this.mTabPaddingStart);
        }
        ViewCompat.setPaddingRelative(this.mTabStrip, paddingStart, 0, 0, 0);
        switch (this.mMode) {
            case 0:
                this.mTabStrip.setGravity(GravityCompat.START);
                break;
            case 1:
                this.mTabStrip.setGravity(1);
                break;
        }
        updateTabViews(true);
    }

    /* access modifiers changed from: package-private */
    public void updateTabViews(boolean requestLayout) {
        for (int i = 0; i < this.mTabStrip.getChildCount(); i++) {
            View child = this.mTabStrip.getChildAt(i);
            child.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LinearLayout.LayoutParams) child.getLayoutParams());
            if (requestLayout) {
                child.requestLayout();
            }
        }
    }

    public static final class Tab {
        public static final int INVALID_POSITION = -1;
        private CharSequence mContentDesc;
        private View mCustomView;
        private Drawable mIcon;
        TabLayout mParent;
        private int mPosition = -1;
        private Object mTag;
        private CharSequence mText;
        TabView mView;

        Tab() {
        }

        @Nullable
        public Object getTag() {
            return this.mTag;
        }

        @NonNull
        public Tab setTag(@Nullable Object tag) {
            this.mTag = tag;
            return this;
        }

        @Nullable
        public View getCustomView() {
            return this.mCustomView;
        }

        @NonNull
        public Tab setCustomView(@Nullable View view) {
            this.mCustomView = view;
            updateView();
            return this;
        }

        @NonNull
        public Tab setCustomView(@LayoutRes int resId) {
            return setCustomView(LayoutInflater.from(this.mView.getContext()).inflate(resId, this.mView, false));
        }

        @Nullable
        public Drawable getIcon() {
            return this.mIcon;
        }

        public int getPosition() {
            return this.mPosition;
        }

        /* access modifiers changed from: package-private */
        public void setPosition(int position) {
            this.mPosition = position;
        }

        @Nullable
        public CharSequence getText() {
            return this.mText;
        }

        @NonNull
        public Tab setIcon(@Nullable Drawable icon) {
            this.mIcon = icon;
            updateView();
            return this;
        }

        @NonNull
        public Tab setIcon(@DrawableRes int resId) {
            if (this.mParent != null) {
                return setIcon(AppCompatResources.getDrawable(this.mParent.getContext(), resId));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setText(@Nullable CharSequence text) {
            this.mText = text;
            updateView();
            return this;
        }

        @NonNull
        public Tab setText(@StringRes int resId) {
            if (this.mParent != null) {
                return setText(this.mParent.getResources().getText(resId));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        public void select() {
            if (this.mParent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            this.mParent.selectTab(this);
        }

        public boolean isSelected() {
            if (this.mParent != null) {
                return this.mParent.getSelectedTabPosition() == this.mPosition;
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setContentDescription(@StringRes int resId) {
            if (this.mParent != null) {
                return setContentDescription(this.mParent.getResources().getText(resId));
            }
            throw new IllegalArgumentException("Tab not attached to a TabLayout");
        }

        @NonNull
        public Tab setContentDescription(@Nullable CharSequence contentDesc) {
            this.mContentDesc = contentDesc;
            updateView();
            return this;
        }

        @Nullable
        public CharSequence getContentDescription() {
            return this.mContentDesc;
        }

        /* access modifiers changed from: package-private */
        public void updateView() {
            if (this.mView != null) {
                this.mView.update();
            }
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.mParent = null;
            this.mView = null;
            this.mTag = null;
            this.mIcon = null;
            this.mText = null;
            this.mContentDesc = null;
            this.mPosition = -1;
            this.mCustomView = null;
        }
    }

    class TabView extends LinearLayout implements View.OnLongClickListener {
        private ImageView mCustomIconView;
        private TextView mCustomTextView;
        private View mCustomView;
        private int mDefaultMaxLines = 2;
        private ImageView mIconView;
        private Tab mTab;
        private TextView mTextView;

        public TabView(Context context) {
            super(context);
            if (TabLayout.this.mTabBackgroundResId != 0) {
                ViewCompat.setBackground(this, AppCompatResources.getDrawable(context, TabLayout.this.mTabBackgroundResId));
            }
            ViewCompat.setPaddingRelative(this, TabLayout.this.mTabPaddingStart, TabLayout.this.mTabPaddingTop, TabLayout.this.mTabPaddingEnd, TabLayout.this.mTabPaddingBottom);
            setGravity(17);
            setOrientation(1);
            setClickable(true);
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
        }

        public boolean performClick() {
            boolean handled = super.performClick();
            if (this.mTab == null) {
                return handled;
            }
            if (!handled) {
                playSoundEffect(0);
            }
            this.mTab.select();
            return true;
        }

        public void setSelected(boolean selected) {
            boolean changed = isSelected() != selected;
            super.setSelected(selected);
            if (changed && selected && Build.VERSION.SDK_INT < 16) {
                sendAccessibilityEvent(4);
            }
            if (this.mTextView != null) {
                this.mTextView.setSelected(selected);
            }
            if (this.mIconView != null) {
                this.mIconView.setSelected(selected);
            }
            if (this.mCustomView != null) {
                this.mCustomView.setSelected(selected);
            }
        }

        @TargetApi(14)
        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            event.setClassName(ActionBar.Tab.class.getName());
        }

        @TargetApi(14)
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(ActionBar.Tab.class.getName());
        }

        public void onMeasure(int origWidthMeasureSpec, int origHeightMeasureSpec) {
            int widthMeasureSpec;
            Layout layout;
            int specWidthSize = View.MeasureSpec.getSize(origWidthMeasureSpec);
            int specWidthMode = View.MeasureSpec.getMode(origWidthMeasureSpec);
            int maxWidth = TabLayout.this.getTabMaxWidth();
            int heightMeasureSpec = origHeightMeasureSpec;
            if (maxWidth <= 0 || (specWidthMode != 0 && specWidthSize <= maxWidth)) {
                widthMeasureSpec = origWidthMeasureSpec;
            } else {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(TabLayout.this.mTabMaxWidth, Integer.MIN_VALUE);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.mTextView != null) {
                Resources resources = getResources();
                float textSize = TabLayout.this.mTabTextSize;
                int maxLines = this.mDefaultMaxLines;
                if (this.mIconView != null && this.mIconView.getVisibility() == 0) {
                    maxLines = 1;
                } else if (this.mTextView != null && this.mTextView.getLineCount() > 1) {
                    textSize = TabLayout.this.mTabTextMultiLineSize;
                }
                float curTextSize = this.mTextView.getTextSize();
                int curLineCount = this.mTextView.getLineCount();
                int curMaxLines = TextViewCompat.getMaxLines(this.mTextView);
                if (textSize != curTextSize || (curMaxLines >= 0 && maxLines != curMaxLines)) {
                    boolean updateTextView = true;
                    if (TabLayout.this.mMode == 1 && textSize > curTextSize && curLineCount == 1 && ((layout = this.mTextView.getLayout()) == null || approximateLineWidth(layout, 0, textSize) > ((float) ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight())))) {
                        updateTextView = false;
                    }
                    if (updateTextView) {
                        this.mTextView.setTextSize(0, textSize);
                        this.mTextView.setMaxLines(maxLines);
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setTab(@Nullable Tab tab) {
            if (tab != this.mTab) {
                this.mTab = tab;
                update();
            }
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            setTab((Tab) null);
            setSelected(false);
        }

        /* access modifiers changed from: package-private */
        public final void update() {
            View custom;
            boolean z;
            Tab tab = this.mTab;
            if (tab != null) {
                custom = tab.getCustomView();
            } else {
                custom = null;
            }
            if (custom != null) {
                ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                        ((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                this.mCustomView = custom;
                if (this.mTextView != null) {
                    this.mTextView.setVisibility(8);
                }
                if (this.mIconView != null) {
                    this.mIconView.setVisibility(8);
                    this.mIconView.setImageDrawable((Drawable) null);
                }
                this.mCustomTextView = (TextView) custom.findViewById(16908308);
                if (this.mCustomTextView != null) {
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mCustomTextView);
                }
                this.mCustomIconView = (ImageView) custom.findViewById(16908294);
            } else {
                if (this.mCustomView != null) {
                    removeView(this.mCustomView);
                    this.mCustomView = null;
                }
                this.mCustomTextView = null;
                this.mCustomIconView = null;
            }
            if (this.mCustomView == null) {
                if (this.mIconView == null) {
                    ImageView iconView = (ImageView) LayoutInflater.from(getContext()).inflate(C0004R.layout.design_layout_tab_icon, this, false);
                    addView(iconView, 0);
                    this.mIconView = iconView;
                }
                if (this.mTextView == null) {
                    TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(C0004R.layout.design_layout_tab_text, this, false);
                    addView(textView);
                    this.mTextView = textView;
                    this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mTextView);
                }
                TextViewCompat.setTextAppearance(this.mTextView, TabLayout.this.mTabTextAppearance);
                if (TabLayout.this.mTabTextColors != null) {
                    this.mTextView.setTextColor(TabLayout.this.mTabTextColors);
                }
                updateTextAndIcon(this.mTextView, this.mIconView);
            } else if (!(this.mCustomTextView == null && this.mCustomIconView == null)) {
                updateTextAndIcon(this.mCustomTextView, this.mCustomIconView);
            }
            if (tab == null || !tab.isSelected()) {
                z = false;
            } else {
                z = true;
            }
            setSelected(z);
        }

        private void updateTextAndIcon(@Nullable TextView textView, @Nullable ImageView iconView) {
            Drawable icon;
            CharSequence text;
            CharSequence contentDesc;
            boolean hasText;
            if (this.mTab != null) {
                icon = this.mTab.getIcon();
            } else {
                icon = null;
            }
            if (this.mTab != null) {
                text = this.mTab.getText();
            } else {
                text = null;
            }
            if (this.mTab != null) {
                contentDesc = this.mTab.getContentDescription();
            } else {
                contentDesc = null;
            }
            if (iconView != null) {
                if (icon != null) {
                    iconView.setImageDrawable(icon);
                    iconView.setVisibility(0);
                    setVisibility(0);
                } else {
                    iconView.setVisibility(8);
                    iconView.setImageDrawable((Drawable) null);
                }
                iconView.setContentDescription(contentDesc);
            }
            if (!TextUtils.isEmpty(text)) {
                hasText = true;
            } else {
                hasText = false;
            }
            if (textView != null) {
                if (hasText) {
                    textView.setText(text);
                    textView.setVisibility(0);
                    setVisibility(0);
                } else {
                    textView.setVisibility(8);
                    textView.setText((CharSequence) null);
                }
                textView.setContentDescription(contentDesc);
            }
            if (iconView != null) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) iconView.getLayoutParams();
                int bottomMargin = 0;
                if (hasText && iconView.getVisibility() == 0) {
                    bottomMargin = TabLayout.this.dpToPx(8);
                }
                if (bottomMargin != lp.bottomMargin) {
                    lp.bottomMargin = bottomMargin;
                    iconView.requestLayout();
                }
            }
            if (hasText || TextUtils.isEmpty(contentDesc)) {
                setOnLongClickListener((View.OnLongClickListener) null);
                setLongClickable(false);
                return;
            }
            setOnLongClickListener(this);
        }

        public boolean onLongClick(View v) {
            int[] screenPos = new int[2];
            Rect displayFrame = new Rect();
            getLocationOnScreen(screenPos);
            getWindowVisibleDisplayFrame(displayFrame);
            Context context = getContext();
            int width = getWidth();
            int height = getHeight();
            int midy = screenPos[1] + (height / 2);
            int referenceX = screenPos[0] + (width / 2);
            if (ViewCompat.getLayoutDirection(v) == 0) {
                referenceX = context.getResources().getDisplayMetrics().widthPixels - referenceX;
            }
            Toast cheatSheet = Toast.makeText(context, this.mTab.getContentDescription(), 0);
            if (midy < displayFrame.height()) {
                cheatSheet.setGravity(8388661, referenceX, (screenPos[1] + height) - displayFrame.top);
            } else {
                cheatSheet.setGravity(81, 0, height);
            }
            cheatSheet.show();
            return true;
        }

        public Tab getTab() {
            return this.mTab;
        }

        private float approximateLineWidth(Layout layout, int line, float textSize) {
            return layout.getLineWidth(line) * (textSize / layout.getPaint().getTextSize());
        }
    }

    private class SlidingTabStrip extends LinearLayout {
        private ValueAnimatorCompat mIndicatorAnimator;
        private int mIndicatorLeft = -1;
        private int mIndicatorRight = -1;
        private int mSelectedIndicatorHeight;
        private final Paint mSelectedIndicatorPaint;
        int mSelectedPosition = -1;
        float mSelectionOffset;

        SlidingTabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            this.mSelectedIndicatorPaint = new Paint();
        }

        /* access modifiers changed from: package-private */
        public void setSelectedIndicatorColor(int color) {
            if (this.mSelectedIndicatorPaint.getColor() != color) {
                this.mSelectedIndicatorPaint.setColor(color);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void setSelectedIndicatorHeight(int height) {
            if (this.mSelectedIndicatorHeight != height) {
                this.mSelectedIndicatorHeight = height;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean childrenNeedLayout() {
            int z = getChildCount();
            for (int i = 0; i < z; i++) {
                if (getChildAt(i).getWidth() <= 0) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setIndicatorPositionFromTabPosition(int position, float positionOffset) {
            if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            this.mSelectedPosition = position;
            this.mSelectionOffset = positionOffset;
            updateIndicatorPosition();
        }

        /* access modifiers changed from: package-private */
        public float getIndicatorPosition() {
            return ((float) this.mSelectedPosition) + this.mSelectionOffset;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824 && TabLayout.this.mMode == 1 && TabLayout.this.mTabGravity == 1) {
                int count = getChildCount();
                int largestTabWidth = 0;
                int z = count;
                for (int i = 0; i < z; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() == 0) {
                        largestTabWidth = Math.max(largestTabWidth, child.getMeasuredWidth());
                    }
                }
                if (largestTabWidth > 0) {
                    boolean remeasure = false;
                    if (largestTabWidth * count <= getMeasuredWidth() - (TabLayout.this.dpToPx(16) * 2)) {
                        for (int i2 = 0; i2 < count; i2++) {
                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getChildAt(i2).getLayoutParams();
                            if (lp.width != largestTabWidth || lp.weight != 0.0f) {
                                lp.width = largestTabWidth;
                                lp.weight = 0.0f;
                                remeasure = true;
                            }
                        }
                    } else {
                        TabLayout.this.mTabGravity = 0;
                        TabLayout.this.updateTabViews(false);
                        remeasure = true;
                    }
                    if (remeasure) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.mIndicatorAnimator == null || !this.mIndicatorAnimator.isRunning()) {
                updateIndicatorPosition();
                return;
            }
            this.mIndicatorAnimator.cancel();
            animateIndicatorToPosition(this.mSelectedPosition, Math.round((1.0f - this.mIndicatorAnimator.getAnimatedFraction()) * ((float) this.mIndicatorAnimator.getDuration())));
        }

        private void updateIndicatorPosition() {
            int right;
            int left;
            View selectedTitle = getChildAt(this.mSelectedPosition);
            if (selectedTitle == null || selectedTitle.getWidth() <= 0) {
                right = -1;
                left = -1;
            } else {
                left = selectedTitle.getLeft();
                right = selectedTitle.getRight();
                if (this.mSelectionOffset > 0.0f && this.mSelectedPosition < getChildCount() - 1) {
                    View nextTitle = getChildAt(this.mSelectedPosition + 1);
                    left = (int) ((this.mSelectionOffset * ((float) nextTitle.getLeft())) + ((1.0f - this.mSelectionOffset) * ((float) left)));
                    right = (int) ((this.mSelectionOffset * ((float) nextTitle.getRight())) + ((1.0f - this.mSelectionOffset) * ((float) right)));
                }
            }
            setIndicatorPosition(left, right);
        }

        /* access modifiers changed from: package-private */
        public void setIndicatorPosition(int left, int right) {
            if (left != this.mIndicatorLeft || right != this.mIndicatorRight) {
                this.mIndicatorLeft = left;
                this.mIndicatorRight = right;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void animateIndicatorToPosition(final int position, int duration) {
            final int startRight;
            final int startLeft;
            if (this.mIndicatorAnimator != null && this.mIndicatorAnimator.isRunning()) {
                this.mIndicatorAnimator.cancel();
            }
            boolean isRtl = ViewCompat.getLayoutDirection(this) == 1;
            View targetView = getChildAt(position);
            if (targetView == null) {
                updateIndicatorPosition();
                return;
            }
            final int targetLeft = targetView.getLeft();
            final int targetRight = targetView.getRight();
            if (Math.abs(position - this.mSelectedPosition) <= 1) {
                startLeft = this.mIndicatorLeft;
                startRight = this.mIndicatorRight;
            } else {
                int offset = TabLayout.this.dpToPx(24);
                if (position < this.mSelectedPosition) {
                    if (isRtl) {
                        startRight = targetLeft - offset;
                        startLeft = startRight;
                    } else {
                        startRight = targetRight + offset;
                        startLeft = startRight;
                    }
                } else if (isRtl) {
                    startRight = targetRight + offset;
                    startLeft = startRight;
                } else {
                    startRight = targetLeft - offset;
                    startLeft = startRight;
                }
            }
            if (startLeft != targetLeft || startRight != targetRight) {
                ValueAnimatorCompat animator = ViewUtils.createAnimator();
                this.mIndicatorAnimator = animator;
                animator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
                animator.setDuration((long) duration);
                animator.setFloatValues(0.0f, 1.0f);
                animator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimatorCompat animator) {
                        float fraction = animator.getAnimatedFraction();
                        SlidingTabStrip.this.setIndicatorPosition(AnimationUtils.lerp(startLeft, targetLeft, fraction), AnimationUtils.lerp(startRight, targetRight, fraction));
                    }
                });
                animator.addListener(new ValueAnimatorCompat.AnimatorListenerAdapter() {
                    public void onAnimationEnd(ValueAnimatorCompat animator) {
                        SlidingTabStrip.this.mSelectedPosition = position;
                        SlidingTabStrip.this.mSelectionOffset = 0.0f;
                    }
                });
                animator.start();
            }
        }

        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (this.mIndicatorLeft >= 0 && this.mIndicatorRight > this.mIndicatorLeft) {
                canvas.drawRect((float) this.mIndicatorLeft, (float) (getHeight() - this.mSelectedIndicatorHeight), (float) this.mIndicatorRight, (float) getHeight(), this.mSelectedIndicatorPaint);
            }
        }
    }

    private static ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        int[][] states = new int[2][];
        int[] colors = new int[2];
        states[0] = SELECTED_STATE_SET;
        colors[0] = selectedColor;
        int i = 0 + 1;
        states[i] = EMPTY_STATE_SET;
        colors[i] = defaultColor;
        int i2 = i + 1;
        return new ColorStateList(states, colors);
    }

    private int getDefaultHeight() {
        boolean hasIconAndText = false;
        int i = 0;
        int count = this.mTabs.size();
        while (true) {
            if (i < count) {
                Tab tab = this.mTabs.get(i);
                if (tab != null && tab.getIcon() != null && !TextUtils.isEmpty(tab.getText())) {
                    hasIconAndText = true;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (hasIconAndText) {
            return 72;
        }
        return 48;
    }

    private int getTabMinWidth() {
        if (this.mRequestedTabMinWidth != -1) {
            return this.mRequestedTabMinWidth;
        }
        if (this.mMode == 0) {
            return this.mScrollableTabMinWidth;
        }
        return 0;
    }

    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return generateDefaultLayoutParams();
    }

    /* access modifiers changed from: package-private */
    public int getTabMaxWidth() {
        return this.mTabMaxWidth;
    }

    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private int mPreviousScrollState;
        private int mScrollState;
        private final WeakReference<TabLayout> mTabLayoutRef;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            this.mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        public void onPageScrollStateChanged(int state) {
            this.mPreviousScrollState = this.mScrollState;
            this.mScrollState = state;
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            boolean updateIndicator;
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null) {
                boolean updateText = this.mScrollState != 2 || this.mPreviousScrollState == 1;
                if (this.mScrollState == 2 && this.mPreviousScrollState == 0) {
                    updateIndicator = false;
                } else {
                    updateIndicator = true;
                }
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
            }
        }

        public void onPageSelected(int position) {
            TabLayout tabLayout = (TabLayout) this.mTabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                tabLayout.selectTab(tabLayout.getTabAt(position), this.mScrollState == 0 || (this.mScrollState == 2 && this.mPreviousScrollState == 0));
            }
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.mScrollState = 0;
            this.mPreviousScrollState = 0;
        }
    }

    public static class ViewPagerOnTabSelectedListener implements OnTabSelectedListener {
        private final ViewPager mViewPager;

        public ViewPagerOnTabSelectedListener(ViewPager viewPager) {
            this.mViewPager = viewPager;
        }

        public void onTabSelected(Tab tab) {
            this.mViewPager.setCurrentItem(tab.getPosition());
        }

        public void onTabUnselected(Tab tab) {
        }

        public void onTabReselected(Tab tab) {
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        public void onChanged() {
            TabLayout.this.populateFromPagerAdapter();
        }

        public void onInvalidated() {
            TabLayout.this.populateFromPagerAdapter();
        }
    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {
        private boolean mAutoRefresh;

        AdapterChangeListener() {
        }

        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (TabLayout.this.mViewPager == viewPager) {
                TabLayout.this.setPagerAdapter(newAdapter, this.mAutoRefresh);
            }
        }

        /* access modifiers changed from: package-private */
        public void setAutoRefresh(boolean autoRefresh) {
            this.mAutoRefresh = autoRefresh;
        }
    }
}
