package com.whitelife.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import java.util.Arrays;


public class BottomNavigationView extends FrameLayout {

    private BottomMenu bottomMenu;

    private BottomNavigationMenuView bottomNavigationMenuView;

    private BottomNavigationPresent present;

    private BottomMenuInflater inflater;

    private View backgroundView;

    private int backgroundColor;

    private int colors[];

    public BottomNavigationView(@NonNull Context context) {
        this(context,null);
    }

    public BottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BottomNavigationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        backgroundView= LayoutInflater.from(context).inflate(R.layout.layout_background,this,false);
        this.addView(backgroundView);

        bottomMenu=new BottomMenu();
        bottomNavigationMenuView=new BottomNavigationMenuView(context,attrs,defStyleAttr);

        present=new BottomNavigationPresent();
        present.setBottomMenu(bottomMenu);
        present.setBottomNavigationMenuView(bottomNavigationMenuView);

        bottomMenu.setPresent(present);
        bottomNavigationMenuView.setPresent(present);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.bottom_navigation);
        if (a.hasValue(R.styleable.bottom_navigation_menu)) {
            inflateMenu(a.getResourceId(R.styleable.bottom_navigation_menu, 0));
        }

        if (a.hasValue(R.styleable.bottom_navigation_background_color)) {
            backgroundColor=a.getColor(R.styleable.bottom_navigation_background_color, ContextCompat.getColor(getContext(),R.color.default_color));
            setBackgroundColor(backgroundColor);
        }

        if (a.hasValue(R.styleable.bottom_navigation_animation_time)) {
            bottomNavigationMenuView.setAnimationTime(a.getInt(R.styleable.bottom_navigation_animation_time, 0));
        }

        if (a.hasValue(R.styleable.bottom_navigation_shifting_mode)) {
            bottomNavigationMenuView.setFinalShiftingMode(a.getBoolean(R.styleable.bottom_navigation_shifting_mode, false));

        }
        setColor();
        a.recycle();
        //??????view
        addView(bottomNavigationMenuView);

        bottomNavigationMenuView.setClickCallback(new BottomNavigationMenuView.ClickCallback() {
            @Override
            public void click(View view, int selectPosition, int id) {
                if(bottomNavigationMenuView.isShiftingMode()&&colors!=null&&colors.length>selectPosition){
                    updateBackgroundColor(colors[selectPosition],view);
                }
                if(clickCallback!=null){
                    clickCallback.click(view,selectPosition,id);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setElevation(10);
        }

    }


    private BottomMenuInflater getInflater() {
        if(inflater==null){
            inflater=new BottomMenuInflater(getContext());
        }
        return inflater;
    }

    public void inflateMenu(int resId){
        //?????????menu
        getInflater().inflater(bottomMenu,resId);
        //???????????????
        present.updateMenuView();

        //????????????
        setColor();
    }


    private void setColor(){
        if(bottomNavigationMenuView.isShiftingMode()){
            super.setBackgroundColor(backgroundColor);
        }else{
            this.setBackgroundResource(R.color.color_white);
        }
    }

    private void updateBackgroundColor(final int color, View clickView){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            backgroundView.setBackgroundColor(color);
            int centerX = clickView.getLeft()+clickView.getMeasuredWidth()/2;
            int centerY = (clickView.getBottom()-clickView.getTop())/2;
            int startRadius = 0;
            int finalRadius = this.getMeasuredWidth();
            Animator animator = ViewAnimationUtils.createCircularReveal(
                    backgroundView,
                    centerX,
                    centerY,
                    startRadius,
                    finalRadius
            );
            animator.setDuration(bottomNavigationMenuView.getAnimationTime());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onEnd();
                }

                private void onEnd() {
                    present.setAnimalRunning(false);
                    BottomNavigationView.this.setBackgroundColor(color);
                }
            });
            present.setAnimalRunning(true);
            animator.start();
        }else{
            this.setBackgroundColor(color);
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        bottomNavigationMenuView.setBackgroundColor(backgroundColor);
        setColor();
    }

    public int[] getColors() {
        return colors;
    }
    public void setColors(int[] colors) {
        int[]total=new int[bottomNavigationMenuView.getChildCount()];
        if(colors==null){
            return;
        }else if(colors.length<bottomNavigationMenuView.getChildCount()){
            int cCount=bottomNavigationMenuView.getChildCount()-colors.length;
            int[] cs=new int[cCount];
            if(cCount<=colors.length){
                cs= Arrays.copyOf(colors,cCount);
            }else{
                for(int i=0,j=0;i<cCount;i++,j++){
                    if(j==colors.length){
                        j=0;
                    }
                    cs[i]=colors[j];
                }
            }

            System.arraycopy(colors,0,total,0,colors.length);
            System.arraycopy(cs,0,total,colors.length,cs.length);
        }
        if(bottomNavigationMenuView.isShiftingMode()) {
            this.colors = total;
            setBackgroundColor(colors[bottomNavigationMenuView.getSelectPosition()]);
        }
    }

    public void setAnimationTime(int time){
        bottomNavigationMenuView.setAnimationTime(time);
    }


    public void setShiftingMode(boolean shiftingMode){
        bottomNavigationMenuView.setShiftingMode(shiftingMode);
    }

    private BottomNavigationMenuView.ClickCallback clickCallback;

    public void setClickCallback(BottomNavigationMenuView.ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }


    interface ClickCallback{
        void click(View view,int selectPosition,int id);
    }

}
