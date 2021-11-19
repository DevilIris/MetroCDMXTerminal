package android.support.design.internal;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.p003v7.view.menu.MenuBuilder;
import android.support.p003v7.view.menu.MenuView;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.util.AttributeSet;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class NavigationMenuView extends RecyclerView implements MenuView {
    public NavigationMenuView(Context context) {
        this(context, (AttributeSet) null);
    }

    public NavigationMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutManager(new LinearLayoutManager(context, 1, false));
    }

    public void initialize(MenuBuilder menu) {
    }

    public int getWindowAnimations() {
        return 0;
    }
}
