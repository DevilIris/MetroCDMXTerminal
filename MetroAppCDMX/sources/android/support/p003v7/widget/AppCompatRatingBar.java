package android.support.p003v7.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.appcompat.C0267R;
import android.util.AttributeSet;
import android.widget.RatingBar;

/* renamed from: android.support.v7.widget.AppCompatRatingBar */
public class AppCompatRatingBar extends RatingBar {
    private AppCompatProgressBarHelper mAppCompatProgressBarHelper;

    public AppCompatRatingBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, C0267R.attr.ratingBarStyle);
    }

    public AppCompatRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAppCompatProgressBarHelper = new AppCompatProgressBarHelper(this);
        this.mAppCompatProgressBarHelper.loadFromAttributes(attrs, defStyleAttr);
    }

    /* access modifiers changed from: protected */
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Bitmap sampleTile = this.mAppCompatProgressBarHelper.getSampleTime();
        if (sampleTile != null) {
            setMeasuredDimension(ViewCompat.resolveSizeAndState(sampleTile.getWidth() * getNumStars(), widthMeasureSpec, 0), getMeasuredHeight());
        }
    }
}
