package foodcenter.android.activities.branch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import foodcenter.android.R;

public class BranchSwipeListViewTouchListener implements View.OnTouchListener
{

    private static final String TAG = "Swipe";

    /** tangens(15) is the minimum threshold to handle animation */
    private final static float TANGENS_A = 0.2679f;

    /** minimum move on X required to finish swiping instead of cancel */
    private int xMinMoveToFinish;

    /** minimum X threshold to required start swiping */
    private int xMinMoveToStart;

    /** minimum X position pressed to start swiping */
    private int xMinStartPos;

    /** Cached ViewConfiguration and system-wide constant values */
    private long mAnimationTime;

    // Fixed properties
    private ListView mListView;
    private OnSwipeCallback mCallback;
    private int viewWidth = 1; // 1 and not 0 to prevent dividing by zero

    private final boolean dismissLeft;
    private final boolean dismissRight;

    // Transient properties
    private List<PendingSwipeData> mPendingSwipes = new ArrayList<PendingSwipeData>();
    private int dismissAnimationRefCount = 0;
    private float xPosOnDown;
    private float yPosOnDown;

    private int viewItemPosition;
    private View viewItem;
    private boolean isViewItemSwipable = false;
    private boolean mPaused;

    /**
     * The callback interface used by {@link BranchSwipeListViewTouchListener} to inform its client
     * about a successful swipe of one or more list item positions.
     */
    public interface OnSwipeCallback
    {
        /**
         * Called when the user has swiped the list item to the left.
         * 
         * @param listView The originating {@link ListView}.
         * @param reverseSortedPositions An array of positions to dismiss, sorted in descending
         *            order for convenience.
         */
        void onSwipeLeft(ListView listView, int[] reverseSortedPositions);

        void onSwipeRight(ListView listView, int[] reverseSortedPositions);
    }

    /**
     * Constructs a new swipe-to-action touch listener for the given list view.
     * 
     * @param listView The list view whose items should be dismissable.
     * @param callback The callback to trigger when the user has indicated that she would like to
     *            dismiss one or more list items.
     * @param dismissLeft set if the dismiss animation is up when the user swipe to the left (shrink
     *            and expand)
     * @param dismissRight set if the dismiss animation is up when the user swipe to the right
     *            (shrink and expand)
     * @see #SwipeListViewTouchListener(ListView, OnSwipeCallback, boolean, boolean)
     */
    public BranchSwipeListViewTouchListener(ListView listView,
                                      OnSwipeCallback callback,
                                      boolean dismissLeft,
                                      boolean dismissRight)
    {
        mAnimationTime = listView.getContext()
            .getResources()
            .getInteger(android.R.integer.config_shortAnimTime);

        mListView = listView;
        mCallback = callback;

        this.dismissLeft = dismissLeft;
        this.dismissRight = dismissRight;
    }

    private void setViewWidth(int width)
    {
        // No "if" - small function better performance than branch prediction
        viewWidth = width;

        // (ARM divide operations are expensive use shifting instead)
        xMinMoveToFinish = viewWidth >> 3; // 8th of the view's width
        xMinMoveToStart = viewWidth >> 4; // 16th of the view's width
        xMinStartPos = viewWidth >> 5; // 32th of the view's width
    }

    /**
     * Enables or disables (pauses or resumes) watching for swipe-to-dismiss gestures.
     * 
     * @param enabled Whether or not to watch for gestures.
     */
    public void setEnabled(boolean enabled)
    {
        mPaused = !enabled;
    }

    /**
     * Returns an {@link android.widget.AbsListView.OnScrollListener} to be added to the
     * {@link ListView} using
     * {@link ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener)}.
     * If a scroll listener is already assigned, the caller should still pass scroll changes
     * through to this listener. This will ensure that this {@link BranchSwipeListViewTouchListener} is
     * paused during list view scrolling.</p>
     * 
     * @see {@link BranchSwipeListViewTouchListener}
     */
    public AbsListView.OnScrollListener makeScrollListener()
    {
        return new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {
                setEnabled(scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2)
            {
            }
        };
    }

    /**
     * A pressed gesture has started, the motion contains the initial starting location.
     * 
     * @param view
     * @param motionEvent
     * @return
     */
    private boolean onTouchDown(View view, MotionEvent motionEvent)
    {
        Log.d(TAG, "onTouchDown()");
        if (mPaused)
        {
            return false;
        }

        xPosOnDown = motionEvent.getRawX();
        yPosOnDown = motionEvent.getRawY();

        if (xPosOnDown < xMinStartPos)
        {
            return false;
        }

        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
        int childCount = mListView.getChildCount();
        int[] listViewCoords = new int[2];
        mListView.getLocationOnScreen(listViewCoords);
        int x = (int) motionEvent.getRawX() - listViewCoords[0];
        int y = (int) motionEvent.getRawY() - listViewCoords[1];
        View child;
        for (int i = 0; i < childCount; i++)
        {
            child = mListView.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y))
            {
                viewItem = child;
                break;
            }
        }

        if (viewItem != null)
        {
            viewItemPosition = mListView.getPositionForView(viewItem);

            Boolean isSwipable = (Boolean) viewItem.getTag(R.id.swipable);
            isViewItemSwipable = ((null != isSwipable) && (true == isSwipable));
        }

        view.onTouchEvent(motionEvent);
        return true;
    }

    private boolean isSwipe(float rawX, float rawY)
    {
        if (xPosOnDown < xMinStartPos)
        {
            return false;
        }

        float deltaXAbs = Math.abs(rawX - xPosOnDown);
        float deltaYAbs = Math.abs(rawY - yPosOnDown);
        Log.v(TAG, "deltaXAbs=" + deltaXAbs
                   + ", deltaYAbs="
                   + deltaYAbs
                   + ", tg(a)deltaX="
                   + (deltaXAbs * TANGENS_A));

        if (deltaXAbs < xMinMoveToStart)
        {
            return false;
        }

        // Y/X < TNG(A) (ARM divide operations are expensive)
        return deltaYAbs < (deltaXAbs * TANGENS_A);
    }

    /**
     * A change has happened during a press gesture (between ACTION_DOWN <br>
     * and ACTION_UP). The motion contains the most recent point, as well as <br>
     * any intermediate points since the last down or move event.
     * 
     * @param view
     * @param motionEvent
     * @return
     */
    private boolean onTouchMove(View view, MotionEvent motionEvent)
    {
        Log.d(TAG, "onTouchMove()");
        if (mPaused || !isViewItemSwipable)
        {
            return false;
        }

        if (!isSwipe(motionEvent.getRawX(), motionEvent.getRawY()))
        {
            return false;
        }

        float deltaX = motionEvent.getRawX() - xPosOnDown;
        float deltaXAbs = Math.abs(deltaX);

        mListView.requestDisallowInterceptTouchEvent(true);

        // Cancel ListView's touch (un-highlighting the item, because of animation)
        int indexMask = motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT;

        motionEvent.setAction(MotionEvent.ACTION_CANCEL | indexMask);
        mListView.onTouchEvent(motionEvent);

        viewItem.setTranslationX(deltaX);
        viewItem.setAlpha(Math.max(0.6f, Math.min(1f, 1 - 2 * deltaXAbs / viewWidth)));
        return true;
    }

    /**
     * A pressed gesture has finished, the motion contains the final release <br>
     * location as well as any intermediate points since the last down or move event.
     * 
     * @param view
     * @param motionEvent
     * @return
     */
    private boolean onTouchUp(View view, MotionEvent motionEvent)
    {
        Log.d(TAG, "onTouchUp()");
        if (mPaused || !isViewItemSwipable)
        {
            return false;
        }

        float deltaX = motionEvent.getRawX() - xPosOnDown;
        Log.v(TAG, "xPosOnDown=" + xPosOnDown + ", delataX=" + deltaX);
        if (Math.abs(deltaX) < xMinMoveToFinish)
        {
            Log.v(TAG, "abs(" + deltaX + ")<" + xMinMoveToFinish);
            cancelSwipeAnimation();
        }
        else if (isSwipe(motionEvent.getRawX(), motionEvent.getRawY()))
        {
            Log.d(TAG, "swipe...");
            finishSwipeAnimation(deltaX);
        }
        else
        {
            Log.d(TAG, "no swipe...");
            cancelSwipeAnimation();
        }

        resetVarsWhenDone();
        return false;
    }

    private void finishSwipeAnimation(float deltaX)
    {
        // private variables get null'd before performSwipeAction is called
        final View downView = viewItem;
        final int downPosition = viewItemPosition;
        final boolean isSwipeRight = deltaX > 0;
        final boolean isDismiss = isSwipeRight ? dismissRight : dismissLeft;
        ++dismissAnimationRefCount;
        viewItem.animate()
            .translationX(isSwipeRight ? viewWidth : -viewWidth)
            .alpha(0)
            .setDuration(mAnimationTime)
            .setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    performSwipeAction(downView, downPosition, isSwipeRight, isDismiss);
                }
            });
    }

    private void cancelSwipeAnimation()
    {
        // cancel
        viewItem.animate().translationX(0).alpha(1).setDuration(mAnimationTime).setListener(null);
    }

    private void resetVarsWhenDone()
    {
        xPosOnDown = 0;
        viewItem = null;
        viewItemPosition = ListView.INVALID_POSITION;
        isViewItemSwipable = false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        setViewWidth(mListView.getWidth());

        switch (motionEvent.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            {
                return onTouchDown(view, motionEvent);
            }

            case MotionEvent.ACTION_MOVE:
            {
                return onTouchMove(view, motionEvent);
            }

            case MotionEvent.ACTION_UP:
            {
                return onTouchUp(view, motionEvent);
            }
        }
        return false;
    }

    class PendingSwipeData implements Comparable<PendingSwipeData>
    {
        public int position;
        public View view;

        public PendingSwipeData(int position, View view)
        {
            this.position = position;
            this.view = view;
        }

        @Override
        public int compareTo(PendingSwipeData other)
        {
            // Sort by descending position
            return other.position - position;
        }
    }

    private void performSwipeAction(final View swipeView,
                                    final int swipePosition,
                                    boolean toTheRight,
                                    boolean dismiss)
    {
        // Animate the dismissed list item to zero-height and fire the dismiss callback when
        // all dismissed list item animations have completed. This triggers layout on each animation
        // frame; in the future we may want to do something smarter and more performant.

        final ViewGroup.LayoutParams lp = swipeView.getLayoutParams();
        final int originalHeight = swipeView.getHeight();
        final boolean swipeRight = toTheRight;

        ValueAnimator animator;
        if (dismiss)
        {
            animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);
        }
        else
        {
            animator = ValueAnimator.ofInt(originalHeight, originalHeight - 1)
                .setDuration(mAnimationTime);
        }

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                --dismissAnimationRefCount;
                if (dismissAnimationRefCount == 0)
                {
                    // No active animations, process all pending dismisses.
                    // Sort by descending position
                    Collections.sort(mPendingSwipes);

                    int[] swipePositions = new int[mPendingSwipes.size()];
                    for (int i = mPendingSwipes.size() - 1; i >= 0; i--)
                    {
                        swipePositions[i] = mPendingSwipes.get(i).position;
                    }
                    if (swipeRight)
                    {
                        mCallback.onSwipeRight(mListView, swipePositions);
                    }
                    else
                    {
                        mCallback.onSwipeLeft(mListView, swipePositions);
                    }

                    ViewGroup.LayoutParams lp;
                    for (PendingSwipeData pendingDismiss : mPendingSwipes)
                    {
                        // Reset view presentation
                        pendingDismiss.view.setAlpha(1f);
                        pendingDismiss.view.setTranslationX(0);
                        lp = pendingDismiss.view.getLayoutParams();
                        lp.height = originalHeight;
                        pendingDismiss.view.setLayoutParams(lp);
                    }

                    mPendingSwipes.clear();
                }
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                swipeView.setLayoutParams(lp);
            }
        });

        mPendingSwipes.add(new PendingSwipeData(swipePosition, swipeView));
        animator.start();
    }
}
