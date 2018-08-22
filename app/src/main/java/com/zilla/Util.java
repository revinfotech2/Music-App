package com.zilla;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Util {

    @BindingAdapter("url")
    public static void setUrl(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).into(view);
    }
}
