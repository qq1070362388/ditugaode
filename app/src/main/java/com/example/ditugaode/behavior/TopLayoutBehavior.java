package com.example.ditugaode.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.example.ditugaode.R;

import java.lang.ref.WeakReference;

public class TopLayoutBehavior extends CoordinatorLayout.Behavior<View>{

    WeakReference<View> mTopLayoutRef;

    public TopLayoutBehavior() {
    }

    public TopLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        View view=child.findViewById(R.id.topLayout);
        mTopLayoutRef=new WeakReference<View>(view);
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if(mTopLayoutRef == null){
            return false;
        }else {
            View view=mTopLayoutRef.get();
            int height=view.getHeight();
            int bottom=view.getBottom();
            int top=dependency.getTop();

            if (top-100<height){
                ViewCompat.offsetTopAndBottom(view,(top-100-bottom));
                return true;
            }

            if (top>height+100 && bottom<height){
                ViewCompat.offsetTopAndBottom(view,(height-bottom));
            }
            return super.onDependentViewChanged(parent,child,dependency);
        }


    }
}