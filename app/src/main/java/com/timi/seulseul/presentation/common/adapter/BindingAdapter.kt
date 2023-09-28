package com.timi.seulseul.presentation.common.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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

@BindingAdapter("visible")
fun TextView.visibility(isNull : String?) {
    if (isNull == null) {
        this.visibility = View.GONE
    }
}

@BindingAdapter("bindTimeAmPm")
fun TextView.bindTimeAmPm(time: String?) {
    var hour = time?.substring(0, 2)?.toInt()

    when(hour) {
        24 -> hour = 0
        25 -> hour = 1
    }

    if (hour!! < 12) {
        this.text = "오전"
    } else {
        this.text = "오후"
    }
}

@BindingAdapter("bindLineImage")
fun ImageView.bindLineImage(line: String?) {
    when(line) {
        "1호선" -> setImageResource(R.drawable.ic_subway_route_line_1)
        "2호선" -> setImageResource(R.drawable.ic_subway_route_line_2)
        "3호선" -> setImageResource(R.drawable.ic_subway_route_line_3)
        "4호선" -> setImageResource(R.drawable.ic_subway_route_line_4)
        "5호선" -> setImageResource(R.drawable.ic_subway_route_line_5)
        "6호선" -> setImageResource(R.drawable.ic_subway_route_line_6)
        "7호선" -> setImageResource(R.drawable.ic_subway_route_line_7)
        "8호선" -> setImageResource(R.drawable.ic_subway_route_line_8)
        "9호선" -> setImageResource(R.drawable.ic_subway_route_line_9)
        "경의중앙선" -> setImageResource(R.drawable.ic_subway_route_line_gyeongjung)
        "신분당선" -> setImageResource(R.drawable.ic_subway_route_line_sinbundang)
        "경춘선" -> setImageResource(R.drawable.ic_subway_route_line_gyeongchun)
        "인천1호선" -> setImageResource(R.drawable.ic_subway_route_line_incheon_1)
        "수인분당" -> setImageResource(R.drawable.ic_subway_route_line_suin)
        "공항철도" -> setImageResource(R.drawable.ic_subway_route_line_airport)
        "경강선" -> setImageResource(R.drawable.ic_subway_route_line_gyeongang)
        "자기부상" -> setImageResource(R.drawable.ic_subway_route_line_magnetic)
        "서해선" -> setImageResource(R.drawable.ic_subway_route_line_west)
        "김포골드라인" -> setImageResource(R.drawable.ic_subway_route_line_gold)
        "인천2호선" -> setImageResource(R.drawable.ic_subway_route_line_incheon_2)
        "의정부선" -> setImageResource(R.drawable.ic_subway_route_line_uijeongbu)
        "우이신설" -> setImageResource(R.drawable.ic_subway_route_line_ui)
        "에버라인" -> setImageResource(R.drawable.ic_subway_route_line_ever)
        "신림선" -> setImageResource(R.drawable.ic_subway_route_line_sinlim)
    }
}

@BindingAdapter("bindLineColor")
fun View.bindLineColor(line: String?) {
    when(line) {
        "1호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_1))
        "2호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_2))
        "3호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_3))
        "4호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_4))
        "5호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_5))
        "6호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_6))
        "7호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_7))
        "8호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_8))
        "9호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_9))
        "경의중앙선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_gyeongjung))
        "신분당선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_sinbundang))
        "경춘선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_gyeongchun))
        "인천1호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_incheon_1))
        "수인분당" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_suin))
        "공항철도" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_airport))
        "경강선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_gyeonggang))
        "자기부상" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_magnetic))
        "서해선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_west))
        "김포골드라인" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_gold))
        "인천2호선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_incheon_2))
        "의정부선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_uijeongbu))
        "우이신설" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_ui))
        "에버라인" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_ever))
        "신림선" -> setBackgroundColor(ContextCompat.getColor(context, R.color.subway_sinlim))
    }
}