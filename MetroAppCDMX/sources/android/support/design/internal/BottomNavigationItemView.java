package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.C0004R;
import android.support.p000v4.content.ContextCompat;
import android.support.p000v4.graphics.drawable.DrawableCompat;
import android.support.p000v4.view.PointerIconCompat;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.view.menu.MenuItemImpl;
import android.support.p003v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationItemView extends FrameLayout implements MenuView.ItemView {
    private static final int[] CHECKED_STATE_SET = {16842912};
    public static final int INVALID_ITEM_POSITION = -1;
    private final int mDefaultMargin;
    private ImageView mIcon;
    private ColorStateList mIconTint;
    private MenuItemImpl mItemData;
    private int mItemPosition;
    private final TextView mLargeLabel;
    private final float mScaleDownFactor;
    private final float mScaleUpFactor;
    private final int mShiftAmount;
    private boolean mShiftingMode;
    private final TextView mSmallLabel;

    public BottomNavigationItemView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public BottomNavigationItemView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomNavigationItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemPosition = -1;
        Resources res = getResources();
        int inactiveLabelSize = res.getDimensionPixelSize(C0004R.dimen.design_bottom_navigation_text_size);
        int activeLabelSize = res.getDimensionPixelSize(C0004R.dimen.design_bottom_navigation_active_text_size);
        this.mDefaultMargin = res.getDimensionPixelSize(C0004R.dimen.design_bottom_navigation_margin);
        this.mShiftAmount = inactiveLabelSize - activeLabelSize;
        this.mScaleUpFactor = (((float) activeLabelSize) * 1.0f) / ((float) inactiveLabelSize);
        this.mScaleDownFactor = (((float) inactiveLabelSize) * 1.0f) / ((float) activeLabelSize);
        LayoutInflater.from(context).inflate(C0004R.layout.design_bottom_navigation_item, this, true);
        setBackgroundResource(C0004R.C0005drawable.design_bottom_navigation_item_background);
        this.mIcon = (ImageView) findViewById(C0004R.C0006id.icon);
        this.mSmallLabel = (TextView) findViewById(C0004R.C0006id.smallLabel);
        this.mLargeLabel = (TextView) findViewById(C0004R.C0006id.largeLabel);
    }

    public void initialize(MenuItemImpl itemData, int menuType) {
        this.mItemData = itemData;
        setCheckable(itemData.isCheckable());
        setChecked(itemData.isChecked());
        setEnabled(itemData.isEnabled());
        setIcon(itemData.getIcon());
        setTitle(itemData.getTitle());
        setId(itemData.getItemId());
    }

    public void setItemPosition(int position) {
        this.mItemPosition = position;
    }

    public int getItemPosition() {
        return this.mItemPosition;
    }

    public void setShiftingMode(boolean enabled) {
        this.mShiftingMode = enabled;
    }

    public MenuItemImpl getItemData() {
        return this.mItemData;
    }

    public void setTitle(CharSequence title) {
        this.mSmallLabel.setText(title);
        this.mLargeLabel.setText(title);
    }

    public void setCheckable(boolean checkable) {
        refreshDrawableState();
    }

    public void setChecked(boolean checked) {
        ViewCompat.setPivotX(this.mLargeLabel, (float) (this.mLargeLabel.getWidth() / 2));
        ViewCompat.setPivotY(this.mLargeLabel, (float) this.mLargeLabel.getBaseline());
        ViewCompat.setPivotX(this.mSmallLabel, (float) (this.mSmallLabel.getWidth() / 2));
        ViewCompat.setPivotY(this.mSmallLabel, (float) this.mSmallLabel.getBaseline());
        if (this.mShiftingMode) {
            if (checked) {
                FrameLayout.LayoutParams iconParams = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                iconParams.gravity = 49;
                iconParams.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(iconParams);
                this.mLargeLabel.setVisibility(0);
                ViewCompat.setScaleX(this.mLargeLabel, 1.0f);
                ViewCompat.setScaleY(this.mLargeLabel, 1.0f);
            } else {
                FrameLayout.LayoutParams iconParams2 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
                iconParams2.gravity = 17;
                iconParams2.topMargin = this.mDefaultMargin;
                this.mIcon.setLayoutParams(iconParams2);
                this.mLargeLabel.setVisibility(4);
                ViewCompat.setScaleX(this.mLargeLabel, 0.5f);
                ViewCompat.setScaleY(this.mLargeLabel, 0.5f);
            }
            this.mSmallLabel.setVisibility(4);
        } else if (checked) {
            FrameLayout.LayoutParams iconParams3 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            iconParams3.gravity = 49;
            iconParams3.topMargin = this.mDefaultMargin + this.mShiftAmount;
            this.mIcon.setLayoutParams(iconParams3);
            this.mLargeLabel.setVisibility(0);
            this.mSmallLabel.setVisibility(4);
            ViewCompat.setScaleX(this.mLargeLabel, 1.0f);
            ViewCompat.setScaleY(this.mLargeLabel, 1.0f);
            ViewCompat.setScaleX(this.mSmallLabel, this.mScaleUpFactor);
            ViewCompat.setScaleY(this.mSmallLabel, this.mScaleUpFactor);
        } else {
            FrameLayout.LayoutParams iconParams4 = (FrameLayout.LayoutParams) this.mIcon.getLayoutParams();
            iconParams4.gravity = 49;
            iconParams4.topMargin = this.mDefaultMargin;
            this.mIcon.setLayoutParams(iconParams4);
            this.mLargeLabel.setVisibility(4);
            this.mSmallLabel.setVisibility(0);
            ViewCompat.setScaleX(this.mLargeLabel, this.mScaleDownFactor);
            ViewCompat.setScaleY(this.mLargeLabel, this.mScaleDownFactor);
            ViewCompat.setScaleX(this.mSmallLabel, 1.0f);
            ViewCompat.setScaleY(this.mSmallLabel, 1.0f);
        }
        refreshDrawableState();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.mSmallLabel.setEnabled(enabled);
        this.mLargeLabel.setEnabled(enabled);
        this.mIcon.setEnabled(enabled);
        if (enabled) {
            ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), PointerIconCompat.TYPE_HAND));
        } else {
            ViewCompat.setPointerIcon(this, (PointerIconCompat) null);
        }
    }

    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    public void setShortcut(boolean showShortcut, char shortcutKey) {
    }

    public void setIcon(Drawable icon) {
        if (icon != null) {
            Drawable.ConstantState state = icon.getConstantState();
            if (state != null) {
                icon = state.newDrawable();
            }
            icon = DrawableCompat.wrap(icon).mutate();
            DrawableCompat.setTintList(icon, this.mIconTint);
        }
        this.mIcon.setImageDrawable(icon);
    }

    public boolean prefersCondensedTitle() {
        return false;
    }

    public boolean showsIcon() {
        return true;
    }

    public void setIconTintList(ColorStateList tint) {
        this.mIconTint = tint;
        if (this.mItemData != null) {
            setIcon(this.mItemData.getIcon());
        }
    }

    public void setTextColor(ColorStateList color) {
        this.mSmallLabel.setTextColor(color);
        this.mLargeLabel.setTextColor(color);
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable;
        if (background == 0) {
            backgroundDrawable = null;
        } else {
            backgroundDrawable = ContextCompat.getDrawable(getContext(), background);
        }
        ViewCompat.setBackground(this, backgroundDrawable);
    }
}
