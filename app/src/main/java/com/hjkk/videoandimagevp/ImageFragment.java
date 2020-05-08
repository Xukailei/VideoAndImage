package com.hjkk.videoandimagevp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ImageFragment extends Fragment {

    private String url;  // 传过来的路径
    private PagerListener finishListener;  //回调监听
    private ImageView imageView;
    private static final int IMAGETIME = 4000; // 默认图片4s切换

    public ImageFragment(String imageRes, PagerListener listener) {
        this.url = imageRes;
        this.finishListener = listener;
    }
    public void imgStart(){
        if (imageView!=null) {
            finishListener.currentItem(IMAGETIME);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_image, null);
        view.setOnClickListener(v->finishListener.onClick());
        imageView = view.findViewById(R.id.image);
        Glide.with(getActivity()).load(url).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        imgStart();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
