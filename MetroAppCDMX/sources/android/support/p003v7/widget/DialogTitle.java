package android.support.p003v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.RestrictTo;
import android.support.p003v7.appcompat.C0267R;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.DialogTitle */
public class DialogTitle extends TextView {
    public DialogTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DialogTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogTitle(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lineCount;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null && (lineCount = layout.getLineCount()) > 0 && layout.getEllipsisCount(lineCount - 1) > 0) {
            setSingleLine(false);
            setMaxLines(2);
            TypedArray a = getContext().obtainStyledAttributes((AttributeSet) null, C0267R.styleable.TextAppearance, 16842817, 16973892);
            int textSize = a.getDimensionPixelSize(C0267R.styleable.TextAppearance_android_textSize, 0);
            if (textSize != 0) {
                setTextSize(0, (float) textSize);
            }
            a.recycle();
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
