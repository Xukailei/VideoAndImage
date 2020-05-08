package com.hjkk.videoandimagevp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：Xu on 2020/5/7.
 * @describe：
 */
public class MainActivity extends AppCompatActivity implements PagerListener {

    // 指示器相关的
    private List<ImageView> mImgList;
    private int img_select;
    private int img_unSelect;
    private static final int IMAGESIZE = 15;

    // 界面相关的
    private ViewPager viewpager;
    private LinearLayout linearLayout;
    private List<DataBean> dataBeans;
    private List<Fragment> fragments;
    private MyFragmentAdapter adapter;

    private boolean mIsChanged = false;
    private int mCurrentPagePosition = FIRST_ITEM_INDEX;
    private static final int FIRST_ITEM_INDEX = 1;
    // 需要的可以在这块设置成静态内部类，避免内存泄漏
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            viewpager.setCurrentItem(mCurrentPagePosition + 1);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpw);

        viewpager = findViewById(R.id.viewpager);
        linearLayout = findViewById(R.id.dot_horizontal);
        dataBeans = DataBean.MainActivity();
        fragments = new ArrayList<>();

        int size = dataBeans.size();

        mImgList = new ArrayList<>();
        img_select = R.drawable.dot_select;
        img_unSelect = R.drawable.dot_unselect;

        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //为小圆点左右添加间距
            params.leftMargin = 10;
            params.rightMargin = 10;
            //给小圆点一个默认大小
            params.height = IMAGESIZE;
            params.width = IMAGESIZE;
            // 默认第一个开始
            if (i == 0) {
                imageView.setBackgroundResource(img_select);
            } else {
                imageView.setBackgroundResource(img_unSelect);
            }
            //为LinearLayout添加ImageView
            linearLayout.addView(imageView, params);
            mImgList.add(imageView);
        }


        // 为了实现无限轮播  在fragment的第一个加入dataBeans的最后一个
        if (dataBeans.get(size - 1).viewType == 1) {
            fragments.add(new ImageFragment(dataBeans.get(size - 1).imageUrl, MainActivity.this));
        } else {
            fragments.add(new VideoFragment(dataBeans.get(size - 1).imageUrl, MainActivity.this));
        }

        // 这里是正常创建
        for (DataBean dataBean : dataBeans) {
            if (dataBean.viewType == 2) {
                fragments.add(new VideoFragment(dataBean.imageUrl, MainActivity.this));
            } else {
                fragments.add(new ImageFragment(dataBean.imageUrl, MainActivity.this));
            }
        }

        // 为了实现无限轮播  在fragment的末尾一个加入dataBeans的第一个
        if (dataBeans.get(0).viewType == 1) {
            fragments.add(new ImageFragment(dataBeans.get(0).imageUrl, MainActivity.this));
        } else {
            fragments.add(new VideoFragment(dataBeans.get(0).imageUrl, MainActivity.this));
        }

        adapter = new MyFragmentAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(FIRST_ITEM_INDEX, false);
        viewpager.setOffscreenPageLimit(3); //设置预加载的个数
        viewpager.setPageTransformer(false, new ScaleTransformer());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacksAndMessages(null);
                mIsChanged = true;
                if (position >= fragments.size() - 1) {// 末位之后，下次切换跳转到首位（1）
                    mCurrentPagePosition = FIRST_ITEM_INDEX;
                } else if (position <= FIRST_ITEM_INDEX) {// 首位之前，下次切换跳转到末尾（N）
                    mCurrentPagePosition = fragments.size() - 1;
                } else {
                    mCurrentPagePosition = position;
                }

                // 开启自动轮播
                Fragment fragment = fragments.get(position);
                if (fragment instanceof VideoFragment) {
                    //视频自动播放
                    VideoFragment videoFragment = (VideoFragment) fragment;
                    videoFragment.startVideo();
                } else {
                    //图片自动倒计时
                    ImageFragment imageFragment = (ImageFragment) fragment;
                    imageFragment.imgStart();
                }

                for (int i = 0; i < size; i++) {
                    //选中的页面改变小圆点为选中状态，反之为未选中
                    if (i == mCurrentPagePosition-1) {
                        (mImgList.get(i)).setBackgroundResource(img_select);
                    } else {
                        (mImgList.get(i)).setBackgroundResource(img_unSelect);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_IDLE == state) {
                    if (mIsChanged) {
                        mIsChanged = false;
                        viewpager.setCurrentItem(mCurrentPagePosition, false);
                    }
                }
            }
        });

        // 每次开始先切换一次，保障fragment里面的监听回调获取到，这块可以去了解一下viewpage + fragment切换时 fragment的生命周期
        currentItem(0);
    }

    /**
     * 切换viewpage
     */
    Runnable runnable = () -> handler.sendEmptyMessage(0);


    @Override
    public void currentItem(int time) {
        handler.postDelayed(runnable, time);
    }

    @Override
    public void onClick() {
        // 点击处理
        Log.e("TAG", "onClick: 点击了界面" );
    }


    /**
     * Adapter 简单使用
     */
    public static class MyFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> list;

        public MyFragmentAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @NonNull
        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public int getCount() {
            return list.size();
        }


    }

    /**
     * ViewPager 切换效果
     */
    public static class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.3f * position;
                    Log.d("google_lenve_fb", "transformPage: scaleX:" + scaleX);
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }
    }
}