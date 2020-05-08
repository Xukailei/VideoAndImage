package com.hjkk.videoandimagevp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {
    private  StandardGSYVideoPlayer player;
    private String url;  // 传过来的路径
    private PagerListener finishListener; //回调监听

    public VideoFragment(String url, PagerListener listener) {
        this.url = url;
        this.finishListener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 开始播放
     */
    public void startVideo() {
        if (player != null) {
            player.startPlayLogic();

            // 这块用的是GSYVideoPlayer 里面的方法 ，下面做判断是因为有的视频时间太短，不饿能准确的回调
            player.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {
                if (duration < 30*1000) {//总时长小于40S
                    if (progress == 96){
                        finishListener.currentItem(0);
                    }
                }else if (duration < 50*1000) {//总时长小于40S
                    if (progress == 97){
                        finishListener.currentItem(0);
                    }
                }else if (duration < 90*1000){//总时长小于90S
                    if (progress == 98){
                        finishListener.currentItem(0);
                    }
                }else {
                    if (progress == 99){
                        finishListener.currentItem(0);
                    }
                }
            });

        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_video, null);
        player = view.findViewById(R.id.video_item_player);
        player.setUp(url, true, "");
        player.getTitleTextView().setVisibility(View.GONE);
        player.getBackButton().setOnClickListener(v -> finishListener.onClick());
        player.getFullscreenButton().setVisibility(View.GONE);
        player.getStartButton().setVisibility(View.GONE);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.image8);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        player.setThumbImageView(imageView);

        startVideo();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

}
