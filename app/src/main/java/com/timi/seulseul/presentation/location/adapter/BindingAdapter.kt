package com.timi.seulseul.presentation.location.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.timi.seulseul.R

@BindingAdapter("endNickName")
fun setEndNickNameImage(view: ImageView, endNickname: String?) {
    when (endNickname) {
        "본가" -> view.setImageResource(R.drawable.ic_location_setting_home_label)
        "자취방" -> view.setImageResource(R.drawable.ic_location_setting_one_room_label)
        "기숙사" -> view.setImageResource(R.drawable.ic_location_setting_dormitory_label)
        "기타" -> view.setImageResource(R.drawable.ic_location_setting_etc_label)
    }
}