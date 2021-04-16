package com.beyondinc.commandcenter.adapter

import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object MainAdapter {
    @JvmStatic
    @BindingAdapter("bind:verAdapter")
    fun verAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        val linearLayoutManager = LinearLayoutManager(recyclerView.context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("text_color")
    fun setTextColor(view: TextView, velue: Int) {
        if (velue < 7) {
            view.setTextColor(Color.parseColor("#FFFF0000"))
        } else {
            view.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    @JvmStatic
    @BindingAdapter("text_color_string")
    fun setTextColorString(view: TextView, velue: String) {
        if (velue == "포장완료") {
            view.setTextColor(Color.parseColor("#FFFF0000"))
        } else {
            view.setTextColor(Color.parseColor("#FF000000"))
        }
    }

    @JvmStatic
    @BindingAdapter("back_color")
    fun setBackColor(view: TextView, state: String) {
        if (state == "배정") view.setBackgroundColor(Color.parseColor("#FFFF0000"))
        else if (state == "접수") view.setBackgroundColor(Color.parseColor("#FFFFB600"))
        else if (state == "취소") view.setBackgroundColor(Color.parseColor("#FF707070"))
        else if (state == "완료") view.setBackgroundColor(Color.parseColor("#FF005CFF"))
        else if (state == "픽업") view.setBackgroundColor(Color.parseColor("#FF1C8B00"))
    }

    @JvmStatic
    @BindingAdapter("text_color_mainbtn1")
    fun setTextColor1(view: TextView, velue: Int) {
        if (velue == 1) view.setTextColor(Color.parseColor("#FFFFFFFF"))
        else view.setTextColor(Color.parseColor("#FF808080"))
    }

    @JvmStatic
    @BindingAdapter("text_color_mainbtn2")
    fun setTextColor2(view: TextView, velue: Int) {
        if (velue == 2) view.setTextColor(Color.parseColor("#FFFFFFFF"))
        else view.setTextColor(Color.parseColor("#FF808080"))
    }

    @JvmStatic
    @BindingAdapter("text_color_mainbtn3")
    fun setTextColor3(view: TextView, velue: Int) {
        if (velue == 3) view.setTextColor(Color.parseColor("#FFFFFFFF"))
        else view.setTextColor(Color.parseColor("#FF808080"))
    }

    @JvmStatic
    @BindingAdapter("layout")
    fun setLayout(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 1) {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        } else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }
}