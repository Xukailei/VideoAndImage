package com.hjkk.videoandimagevp;

/**
 * @author：Xu on 2020/5/8.
 * @describe：
 */

interface PagerListener {

    // 监听 这里可以回调一些数据进行处理
    void onClick();

    // 切换，同样的根据需求回调fragment数据
    void currentItem(int time);
}
