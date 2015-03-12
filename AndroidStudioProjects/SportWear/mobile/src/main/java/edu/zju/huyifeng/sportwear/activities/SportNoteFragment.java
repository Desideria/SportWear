package edu.zju.huyifeng.sportwear.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.quint.QuintEaseOut;
import com.sefford.circularprogressdrawable.CircularProgressDrawable;

import edu.zju.huyifeng.sportwear.R;

/**
 * Created by huyifeng on 15/3/10.
 */
public class SportNoteFragment extends Fragment {

    private View mRootView;
    private RelativeLayout rlytWalk;
    private RelativeLayout rlytRun;
    private RelativeLayout rlytBike;
    private TextView tvWalkCal;
    private TextView tvRunCal;
    private TextView tvBikeCal;
    private ImageButton ibPreDay;
    private ImageButton ibNextDay;
    private float ratio;
    /**
     * CircleDrawable
     */
    private ImageView ivDrawable;
    private CircularProgressDrawable drawable;
    private Animator currentAnimation;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.iv_circle_drawable:
                    if (currentAnimation != null) {
                        currentAnimation.cancel();
                    }
                    currentAnimation = preparePulseAnimation();
                    currentAnimation.start();
                    break;
                case R.id.btn_next:
                    if (currentAnimation != null) {
                        currentAnimation.cancel();
                    }
                    ratio = 0.5f;
                    currentAnimation = prepareStyle3Animation(ratio);
                    currentAnimation.start();
                    rlytRun.setVisibility(View.GONE);
//                    mBarChart.setLayoutParams(
//                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                    (rlytWalk.getHeight()+mBarChart.getHeight())));
//                    mBarChart.setLayoutParams(
//                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                                    100));
                    break;
                case R.id.btn_pre:
                    break;
                case R.id.rlyt_walk:
                    break;
                case R.id.rlyt_run:
                    break;
                case R.id.rlyt_bike:
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * Alpha
     */
    private static int mCurrAlpha;
    private static int mOldAlpha;

    /**
     * Ease
     */
    private static BaseEasingMethod mCurrEasing;
    private static BaseEasingMethod mOldEasing;

    /**
     * Order
     */
    private final static int[] beginOrder = {0, 1, 2, 3, 4, 5, 6};
    private final static int[] middleOrder = {3, 2, 4, 1, 5, 0, 6};
    private final static int[] endOrder = {6, 5, 4, 3, 2, 1, 0};
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    private static float mOldOverlapFactor;
    private static int[] mOldOverlapOrder;

    /**
     * Enter
     */
    private static float mCurrStartX;
    private static float mCurrStartY;
    private static float mOldStartX;
    private static float mOldStartY;

    /**
     * Bar
     */
    private final static int BAR_MAX = 10;
    private final static int BAR_MIN = 0;
    private final static String[] barLabels = {"YAK", "ANT", "GNU", "OWL", "APE", "JAY", "COD"};
    private final static float[] barValues[] = {{5f, 6f, 2f, 2f, 9f, 3f, 4f},{5f, 6f, 2f, 2f, 9f, 3f, 5f},{5f, 6f, 2f, 2f, 9f, 3f, 6f},{5f, 6f, 2f, 2f, 9f, 3f, 6f}};
    private static BarChartView mBarChart;
    private Paint mBarGridPaint;
    private TextView mBarTooltip;

    private final OnEntryClickListener barEntryListener = new OnEntryClickListener() {
        @Override
        public void onClick(int setIndex, int entryIndex, Rect rect) {
//            if(mBarTooltip == null)
//                showBarTooltip(setIndex, entryIndex, rect);
//            else
//                dismissBarTooltip(setIndex, entryIndex, rect);
        }
    };

    private final View.OnClickListener barClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if(mBarTooltip != null)
//                dismissBarTooltip(-1, -1, null);
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrOverlapFactor = 1;
        mCurrEasing = new QuintEaseOut();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        mOldOverlapFactor = 1;
        mOldEasing = new QuintEaseOut();
        mOldStartX = -1;
        mOldStartY = 0;
        mOldAlpha = -1;

    }

    @Override
    public void onResume() {
        super.onResume();
        ivDrawable = (ImageView) mRootView.findViewById(R.id.iv_circle_drawable);

        drawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.drawable_ring_size))
                .setOutlineColor(getResources().getColor(android.R.color.darker_gray))
                .setRingColor(getResources().getColor(android.R.color.holo_green_light))
                .setCenterColor(getResources().getColor(android.R.color.holo_blue_dark))
                .create();
        ivDrawable.setImageDrawable(drawable);
        currentAnimation = prepareStyleNewAnimation();
        currentAnimation.start();
        hookUpListeners();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_sport_note, container, false);
        Bundle bundle = this.getArguments();
        initView(bundle);
        return mRootView;
    }

    private void initView(Bundle bundle) {
        rlytWalk = (RelativeLayout) mRootView.findViewById(R.id.rlyt_walk);
        rlytRun = (RelativeLayout) mRootView.findViewById(R.id.rlyt_run);
        rlytBike = (RelativeLayout) mRootView.findViewById(R.id.rlyt_bike);
        tvWalkCal = (TextView) mRootView.findViewById(R.id.tv_walk_cal);
        tvRunCal = (TextView) mRootView.findViewById(R.id.tv_run_cal);
        tvBikeCal = (TextView) mRootView.findViewById(R.id.tv_bike_cal);
        ibPreDay = (ImageButton) mRootView.findViewById(R.id.btn_pre);
        ibNextDay = (ImageButton) mRootView.findViewById(R.id.btn_next);

        rlytWalk.setOnClickListener(listener);
        rlytRun.setOnClickListener(listener);
        rlytBike.setOnClickListener(listener);
        ibPreDay.setOnClickListener(listener);
        ibNextDay.setOnClickListener(listener);

        initBarChart();
        updateBarChart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(0);
    }




    /*------------------------------------*
     *              BARCHART              *
	 *------------------------------------*/

    private void initBarChart() {

        mBarChart = (BarChartView) mRootView.findViewById(R.id.barchart);
        mBarChart.setOnEntryClickListener(barEntryListener);
        mBarChart.setOnClickListener(barClickListener);

        mBarGridPaint = new Paint();
        mBarGridPaint.setColor(this.getResources().getColor(R.color.bar_grid));
        mBarGridPaint.setStyle(Paint.Style.STROKE);
        mBarGridPaint.setAntiAlias(true);
        mBarGridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
    }


    private void updateBarChart() {

        mBarChart.reset();

        BarSet barSet = new BarSet();
        barSet.addBars(barLabels, barValues[0]);
        barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
//        Bar bar;
//        for (int i = 0; i < 7; i++) {
//            bar = new Bar(barLabels[i], barValues[0][i]);
//            if (i == 4)
//                bar.setColor(this.getResources().getColor(R.color.bar_highest));
//            else
//                bar.setColor(this.getResources().getColor(R.color.bar_fill1));
//            barSet.addBar(bar);
//        }
        mBarChart.addData(barSet);

        barSet = new BarSet();
        barSet.addBars(barLabels, barValues[1]);
        barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
        mBarChart.addData(barSet);

        barSet = new BarSet();
        barSet.addBars(barLabels, barValues[2]);
        barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
        mBarChart.addData(barSet);

        barSet = new BarSet();
        barSet.addBars(barLabels, barValues[3]);
        barSet.setColor(this.getResources().getColor(R.color.bar_fill2));
        mBarChart.addData(barSet);

        mBarChart.setSetSpacing(Tools.fromDpToPx(5));
        mBarChart.setBarSpacing(Tools.fromDpToPx(10));

        mBarChart.setBorderSpacing(0)
                .setAxisBorderValues(BAR_MIN, BAR_MAX, 2)
                .setGrid(BarChartView.GridType.FULL, mBarGridPaint)
                .setYAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYLabels(YController.LabelPosition.NONE)
                .show(getAnimation(true).setEndAction(null))
//                .show()
        ;
    }


    /*@SuppressLint("NewApi")
    private void showBarTooltip(int setIndex, int entryIndex, Rect rect){

        mBarTooltip = (TextView) getLayoutInflater().inflate(R.layout.bar_tooltip, null);
        mBarTooltip.setText(Integer.toString((int) barValues[setIndex][entryIndex]));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(rect.width(), rect.height());
        layoutParams.leftMargin = rect.left;
        layoutParams.topMargin = rect.top;
        mBarTooltip.setLayoutParams(layoutParams);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
            mBarTooltip.setAlpha(0);
            mBarTooltip.setScaleY(0);
            mBarTooltip.animate()
                    .setDuration(200)
                    .alpha(1)
                    .scaleY(1)
                    .setInterpolator(enterInterpolator);
        }

        mBarChart.showTooltip(mBarTooltip);
    }*/


    /*@SuppressLint("NewApi")
    private void dismissBarTooltip(final int setIndex, final int entryIndex, final Rect rect){

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            mBarTooltip.animate()
                    .setDuration(100)
                    .scaleY(0)
                    .alpha(0)
                    .setInterpolator(exitInterpolator).withEndAction(new Runnable(){
                @Override
                public void run() {
                    mBarChart.removeView(mBarTooltip);
                    mBarTooltip = null;
                    if(entryIndex != -1)
                        showBarTooltip(setIndex, entryIndex, rect);
                }
            });
        }else{
            mBarChart.dismissTooltip(mBarTooltip);
            mBarTooltip = null;
            if(entryIndex != -1)
                showBarTooltip(setIndex, entryIndex, rect);
        }
    }*/


//    private void updateValues(BarChartView chartView) {
//
//        chartView.updateValues(0, barValues[1]);
//        chartView.updateValues(1, barValues[0]);
//        chartView.notifyDataUpdate();
//    }

    private Animation getAnimation(boolean newAnim) {
        if (newAnim)
            return new Animation()
                    .setAlpha(mCurrAlpha)
                    .setEasing(mCurrEasing)
                    .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                    .setStartPoint(mCurrStartX, mCurrStartY);
        else
            return new Animation()
                    .setAlpha(mOldAlpha)
                    .setEasing(mOldEasing)
                    .setOverlap(mOldOverlapFactor, mOldOverlapOrder)
                    .setStartPoint(mOldStartX, mOldStartY);
    }

    private void hookUpListeners() {
        ivDrawable.setOnClickListener(listener);
    }

    /**
     * This animation will make a pulse effect to the inner circle
     *
     * @return Animation
     */
    private Animator preparePulseAnimation() {
        AnimatorSet animation = new AnimatorSet();

//        Animator firstBounce = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY,
//                drawable.getCircleScale(), 0.85f);
//        firstBounce.setDuration(300);
//        firstBounce.setInterpolator(new CycleInterpolator(1));
        Animator secondBounce = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY,
                0.75f, 0.83f);
        secondBounce.setDuration(300);
        secondBounce.setInterpolator(new CycleInterpolator(1));
        Animator thirdBounce = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY,
                0.75f, 0.80f);
        thirdBounce.setDuration(300);
        thirdBounce.setInterpolator(new CycleInterpolator(1));

        animation.playSequentially(secondBounce, thirdBounce);
        return animation;
    }
    /**
     * Style 3 animation will turn a 3/4 animation with Anticipate/Overshoot interpolation to a
     * blank waiting - like state, wait for 2 seconds then return to the original state
     *
     * @return Animation
     */
    private Animator prepareStyleNewAnimation() {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator invertedProgress = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, 0f, 0.75f);
        invertedProgress.setDuration(1200);
        invertedProgress.setStartDelay(1200);
        invertedProgress.setInterpolator(new OvershootInterpolator());

        animation.playTogether(invertedProgress);
        return animation;
    }
    /**
     * Style 3 animation will turn a 3/4 animation with Anticipate/Overshoot interpolation to a
     * blank waiting - like state, wait for 2 seconds then return to the original state
     *
     * @return Animation
     */
    private Animator prepareStyle3Animation(float ratio) {
        AnimatorSet animation = new AnimatorSet();

        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, ratio, 0f);
        progressAnimation.setDuration(600);
        progressAnimation.setInterpolator(new AnticipateInterpolator());

        Animator innerCircleAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0.75f, 0f);
        innerCircleAnimation.setDuration(600);
        innerCircleAnimation.setInterpolator(new AnticipateInterpolator());

        ObjectAnimator invertedProgress = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY, 0f, ratio);
        invertedProgress.setDuration(1200);
        invertedProgress.setStartDelay(1200);
        invertedProgress.setInterpolator(new OvershootInterpolator());

        Animator invertedCircle = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.CIRCLE_SCALE_PROPERTY, 0f, 0.75f);
        invertedCircle.setDuration(1200);
        invertedCircle.setStartDelay(1200);
        invertedCircle.setInterpolator(new OvershootInterpolator());

        animation.playTogether(progressAnimation, innerCircleAnimation, invertedProgress, invertedCircle);
        return animation;
    }
}


