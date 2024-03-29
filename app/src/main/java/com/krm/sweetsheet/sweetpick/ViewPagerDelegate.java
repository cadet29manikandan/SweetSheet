package com.krm.sweetsheet.sweetpick;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.viewpager.widget.ViewPager;

import com.krm.sweetsheet.R;
import com.krm.sweetsheet.adapter.ViewpagerAdapter;
import com.krm.sweetsheet.entity.MenuEntity;
import com.krm.sweetsheet.viewhandler.MenuListViewHandler;
import com.krm.sweetsheet.widget.FreeGrowUpParentRelativeLayout;
import com.krm.sweetsheet.widget.IndicatorView;
import com.krm.sweetsheet.widget.SweetView;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerDelegate extends Delegate {

    private ArrayList<MenuListViewHandler> mMenuListViewHandlers;
    private IndicatorView mIndicatorView;
    private ViewPager mViewPager;
    private SweetView mSweetView;
    private MenuListViewHandler mMenuListViewHandler;
    private SweetSheet.OnMenuItemClickListener mOnMenuItemClickListener;
    private List<MenuEntity> mMenuEntities;
    private int mContentViewHeight;

    public ViewPagerDelegate() {

    }

    @Override
    protected View createView() {
        @SuppressLint("InflateParams")
        View rootView = LayoutInflater.from(mParentVG.getContext()).inflate(R.layout.layout_vp_sweet, null, false);
        mSweetView = rootView.findViewById(R.id.sv);
        FreeGrowUpParentRelativeLayout mFreeGrowUpParentRelativeLayout = rootView.findViewById(R.id.freeGrowUpParentF);

        mIndicatorView = rootView.findViewById(R.id.indicatorView);
        mIndicatorView.alphaDismiss(false);
        mSweetView.setAnimationListener(new AnimationImp());
        mViewPager = rootView.findViewById(R.id.vp);

        if (mContentViewHeight > 0) {
            mFreeGrowUpParentRelativeLayout.setContentHeight(mContentViewHeight);
        }
        return rootView;
    }

    @Override
    protected void dismiss() {
        super.dismiss();
    }

    protected void setMenuList(List<MenuEntity> menuEntities) {
        mMenuEntities = menuEntities;
        mMenuListViewHandlers = new ArrayList<>();
        int mNumColumns = 3;
        int fragmentCount = menuEntities.size() / (mNumColumns * 2);
        if (menuEntities.size() % (mNumColumns * 2) != 0) {
            fragmentCount += 1;
        }
        for (int i = 0; i < fragmentCount; i++) {

            int lastIndex = Math.min((i + 1) * (mNumColumns * 2), menuEntities.size());
            MenuListViewHandler menuListViewHandler = MenuListViewHandler.getInstant
                    (i, mNumColumns, menuEntities.subList(i * (mNumColumns * 2), lastIndex));
            menuListViewHandler.setOnMenuItemClickListener(new OnFragmentInteractionListenerImp());
            mMenuListViewHandlers.add(menuListViewHandler);
        }
        mViewPager.setAdapter(new ViewpagerAdapter(mMenuListViewHandlers));
        mIndicatorView.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        selectPosition(0);
    }

    protected void show() {
        super.show();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mRootView.getParent() != null) {
            mParentVG.removeView(mRootView);
        }
        mParentVG.addView(mRootView, lp);
        mSweetView.show();
    }

    @Override
    protected void setOnMenuItemClickListener(SweetSheet.OnMenuItemClickListener onItemClickListener) {
        mOnMenuItemClickListener = onItemClickListener;
    }

    private void selectPosition(int position) {
        mMenuListViewHandler = mMenuListViewHandlers.get(position);
    }

    class OnFragmentInteractionListenerImp implements MenuListViewHandler.OnFragmentInteractionListener {
        @Override
        public void onFragmentInteraction(int index) {
            if (mOnMenuItemClickListener != null) {
                mMenuEntities.get(index);
                if (mOnMenuItemClickListener.onItemClick(index, mMenuEntities.get(index))) {
                    delayedDismiss();
                }
            }
        }
    }

    class AnimationImp implements SweetView.AnimationListener {
        @Override
        public void onStart() {
            mStatus = SweetSheet.Status.SHOWING;
            mIndicatorView.setVisibility(View.INVISIBLE);
            if (mMenuListViewHandler != null) {
                mMenuListViewHandler.animationOnStart();
            }
        }

        @Override
        public void onEnd() {
            if (mStatus == SweetSheet.Status.SHOWING) {
                mIndicatorView.alphaShow(true);
                mIndicatorView.setVisibility(View.VISIBLE);
                mIndicatorView.circularReveal(
                        mIndicatorView.getWidth() / 2,
                        mIndicatorView.getHeight() / 2,
                        0,
                        mIndicatorView.getWidth(), 2000, new DecelerateInterpolator());
                mStatus = SweetSheet.Status.SHOW;
            }
        }

        @Override
        public void onContentShow() {
            if (mMenuListViewHandler != null) {
                mMenuListViewHandler.notifyAnimation();
            }
        }
    }
}
