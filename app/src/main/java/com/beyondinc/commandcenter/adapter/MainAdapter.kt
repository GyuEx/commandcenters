package com.beyondinc.commandcenter.adapter

import android.graphics.Color
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import org.w3c.dom.Text

object MainAdapter {

    val True : Int = 1
    val False : Int = 0

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
            view.setTextColor(Vars.mContext!!.getColor(R.color.brief))
        } else {
            view.setTextColor(Vars.mContext!!.getColor(R.color.black))
        }
    }

    @JvmStatic
    @BindingAdapter("text_color_string")
    fun setTextColorString(view: TextView, velue: String) {
        if (velue == "포장완료") {
            view.setTextColor(Vars.mContext!!.getColor(R.color.brief))
        } else {
            view.setTextColor(Vars.mContext!!.getColor(R.color.black))
        }
    }

    @JvmStatic
    @BindingAdapter("back_color")
    fun setBackColor(view: TextView, state: String) {
        if (state == "배정") view.setBackgroundResource(R.color.recive)
        else if (state == "접수") view.setBackgroundResource(R.color.brief)
        else if (state == "취소") view.setBackgroundResource(R.color.cancle)
        else if (state == "완료") view.setBackgroundResource(R.color.complite)
        else if (state == "픽업") view.setBackgroundResource(R.color.pickup)
    }

    @JvmStatic
    @BindingAdapter("text_color_breif")
    fun setTextBrief(view: TextView, velue: Int) {
        if (velue == True) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_store")
    fun setTextStore(view: TextView, velue: Int) {
        if (velue == True) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_rider")
    fun setTextRider(view: TextView, velue: Int) {
        if (velue == True) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_map")
    fun setTextMaptoOder(view: TextView, velue: Int) {
        if (velue == True)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.setBackgroundResource(R.drawable.order_btn)
        }
        else
        {
            (Vars.mContext as MainsFun).setFragment()
            view.setBackgroundResource(R.drawable.map_btn)
        }
    }

    @JvmStatic
    @BindingAdapter("layout")
    fun setLayout(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == True) {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        } else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_check")
    fun setLayoutcheck(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        if (height == True) {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            view.layoutParams = layoutParams
        } else {
            layoutParams.width = 0
            layoutParams.height = 0
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("custom_checkbox")
    fun setCustomCheck(view: View, height: Boolean) {
        Log.e("aa", "" + height)
        if (height == true) {
            view.setBackgroundResource(R.drawable.checkbox_sel)
        } else {
            view.setBackgroundResource(R.drawable.checkbox_nor)
        }
    }

    @JvmStatic
    @BindingAdapter("filter_breifes")
    fun filter_breifes(view: TextView, height: Boolean) {
        Log.e("aa","" + height)
        if(height)view.setBackgroundResource(R.drawable.state1_p)
        else view.setBackgroundResource(R.drawable.state1_n)
    }

    @JvmStatic
    @BindingAdapter("filter_recive")
    fun filter_recive(view: TextView, height: Boolean) {
        if(height)view.setBackgroundResource(R.drawable.state2_p)
        else view.setBackgroundResource(R.drawable.state2_n)
    }

    @JvmStatic
    @BindingAdapter("filter_pikup")
    fun filter_pikup(view: TextView, height: Boolean) {
        if(height)view.setBackgroundResource(R.drawable.state3_p)
        else view.setBackgroundResource(R.drawable.state3_n)
    }

    @JvmStatic
    @BindingAdapter("filter_complete")
    fun filter_complete(view: TextView, height: Boolean) {
        if(height)view.setBackgroundResource(R.drawable.state4_p)
        else view.setBackgroundResource(R.drawable.state4_n)
    }

    @JvmStatic
    @BindingAdapter("filter_cancel")
    fun filter_cancel(view: TextView, height: Boolean) {
        if(height)view.setBackgroundResource(R.drawable.state5_p)
        else view.setBackgroundResource(R.drawable.state5_n)
    }

    @JvmStatic
    @BindingAdapter("filter_font_size")
    fun filter_font_size(view: TextView, height: String) {
        if(height.length == 1) view.textSize = 25F
        else if(height.length == 2) view.textSize = 25F
        else if(height.length == 3) view.textSize = 20F
        else if(height.length == 4) view.textSize = 15F
    }

    @JvmStatic
    @BindingAdapter("Main_title_font")
    fun main_title_font(view: TextView, height: Int) {
        if(height == Finals.SELECT_MAP) view.text = "관제지도"
        else if(height == Finals.SELECT_EMPTY) view.text = "오더현황 ▼"
        else if(height == Finals.SELECT_CHECK) view.text = "오더현황 ▲"
    }

    @JvmStatic
    @BindingAdapter("Main_title_font2")
    fun main_title_font2(view: TextView, height: Int) {
        if(height == Finals.SELECT_MAP) view.text = "관제지도"
        else if(height == Finals.SELECT_ORDER) view.text = "오더현황 ▼"
    }
}