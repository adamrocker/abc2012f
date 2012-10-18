package com.adamrocker.abc2012f;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

public class SlideFrameLayout extends FrameLayout {

    private Scroller mScroller;
    private int mDuration = 230;
    private float mLastMotionX;
    private ViewGroup mAboveView;
    private ViewGroup mBehindView;
    private int mBehindViewWidth;
    private boolean mDraggable;
    private View mOverlay;

    public SlideFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Interpolator ip = null;
        ip = new AccelerateDecelerateInterpolator();
        //ip = new AccelerateInterpolator();
        //ip = new DecelerateInterpolator();
        mScroller = new Scroller(context, ip);
    }
    
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBehindView = (ViewGroup)getChildAt(0);
        mBehindViewWidth = mBehindView.getMeasuredWidth();
    }
    
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mAboveView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            if (mAboveView.getScrollX() == 0) {
                mOverlay.setVisibility(View.GONE);
            } else {
                mOverlay.setVisibility(View.VISIBLE);
            }
        }
    }
    
    public View getBehindView() {
        return mBehindView;
    }
    
    public void setAboveView(ViewGroup main) {
        final Context context =getContext();
        final int fp = LayoutParams.FILL_PARENT; 
        mAboveView = new FrameLayout(context);
        mAboveView.setLayoutParams(new FrameLayout.LayoutParams(fp, fp));
        addView(mAboveView);
        //mAboveView = (ViewGroup) getChildAt(1);
        mAboveView.addView(main);
        mOverlay = new OverlayView(getContext());
        //int height = child.getHeight() - child.getChildAt(0).getHeight();//remove the actionbar's height
        //int width = child.getWidth();
        //mOverlay.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.BOTTOM));
        mOverlay.setLayoutParams(new FrameLayout.LayoutParams(fp, fp, Gravity.BOTTOM));
        mOverlay.setEnabled(true);
        //mOverlay.setBackgroundColor(Color.parseColor("#33000000"));
        mOverlay.setVisibility(View.GONE);
        mOverlay.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                closeMenu();
            }
        });
        mAboveView.addView(mOverlay);
        requestLayout();
        invalidate();
    }
    
    public View getAboveView() {
        return mAboveView;
    }
    
    public void setAnimationDuration(int msec) {
        mDuration = msec;
    }
    
    public int getAnimationDuration() {
        return mDuration;
    }
    
    public void closeMenu() {
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -curX, 0, mDuration);
        invalidate();
    }
    
    public void openMenu() {
        int curX = mAboveView.getScrollX();
        mScroller.startScroll(curX, 0, -mBehindViewWidth, 0, mDuration);
        invalidate();
    }
    
    public void toggleMenu() {
        int curX = mAboveView.getScrollX();
        if (curX != 0) {
            closeMenu();
        } else {
            openMenu();
        }
    }
    
    public boolean isMenuOpening() {
        return mAboveView != null && mAboveView.getScrollX() == 0;
    }
    
    private boolean mOpening = false;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //if (!mOpening) return super.onInterceptTouchEvent(ev);
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
        {
            float x = ev.getX();
            mLastMotionX = x;
            mDraggable = -(mAboveView.getScrollX()) < x;
            break;
        }
        case MotionEvent.ACTION_UP:
        {
            if (mDraggable) {
                int currentX = mAboveView.getScrollX();
                int diffX = 0;
                if (mOpening) {
                    diffX = -mBehindViewWidth - currentX;
                } else {
                    diffX = -currentX;
                }
                mScroller.startScroll(currentX, 0, diffX, 0, mDuration);
                invalidate();
            }
            break;
        }
        case MotionEvent.ACTION_MOVE:
            if (!mDraggable) return false;
            
            float newX = ev.getX();
            float diffX = -(newX - mLastMotionX);
            int x = mAboveView.getScrollX();
            {
                mOpening = mLastMotionX < newX;
                if (Math.abs(diffX) < 3) {
                    mOpening = mBehindViewWidth / 2 < -x;
                }
            }
            mLastMotionX = newX;
            float nextX = x + diffX;
            if (0 < nextX) {
                mAboveView.scrollTo(0, 0);
            } else {
                if (nextX < -mBehindViewWidth) {
                    mAboveView.scrollTo(-mBehindViewWidth, 0);
                } else {
                    mAboveView.scrollBy((int) diffX, 0);
                }
            }
            break;
        }
        return true;
    }
    
    /**
     * Overlay view only when the behind menu is appeared.
     * This view control scrolling the above view  
     * @author adam
     *
     */
    private class OverlayView extends View {
        private static final float CLICK_RANGE = 3;
        private float mDownX;
        private float mDownY;
        private OnClickListener mClickListener;
        public OverlayView(Context context) {
            super(context);
        }
        
        public void setOnClickListener(OnClickListener listener) {
            mClickListener = listener;
            super.setOnClickListener(listener);
        }
        
        public boolean onTouchEvent(MotionEvent ev) {
            ev.setLocation(ev.getX() - mAboveView.getScrollX(), 0);
            SlideFrameLayout.this.onTouchEvent(ev);
            int action = ev.getAction() & MotionEvent.ACTION_MASK;
                float x = ev.getX();
                float y = ev.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                mDownX = x;
                mDownY = y;
            } else if (action == MotionEvent.ACTION_UP) {
                if (mClickListener != null) {
                    if (Math.abs(mDownX - x) < CLICK_RANGE && Math.abs(mDownY - y) < CLICK_RANGE) {
                        mClickListener.onClick(this);
                    }
                }
            }
            return true;
        }
       
    }
}