package com.beyondinc.commandcenter.adapter

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.sothree.slidinguppanel.SlidingUpPanelLayout

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
        if (height == true) {
            view.setBackgroundResource(R.drawable.checkbox_sel)
        } else {
            view.setBackgroundResource(R.drawable.checkbox_nor)
        }
    }

    @JvmStatic
    @BindingAdapter("custom_checkbox_login")
    fun setCustomCheckLogin(view: CheckBox, height: Boolean) {
        if (height == true) {
            view.setButtonDrawable(R.drawable.k_login_check_p)
        } else {
            view.setButtonDrawable(R.drawable.k_login_check_n)
        }
    }

    @JvmStatic
    @BindingAdapter("drawer_open")
    fun setDrawer_Open(view: DrawerLayout, height: Boolean) {
        if (height == true) {
            view.openDrawer(Gravity.LEFT)
            view.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
        } else {
            view.closeDrawer(Gravity.LEFT)
            view.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    @JvmStatic
    @BindingAdapter("filter_breifes")
    fun filter_breifes(view: TextView, height: Boolean) {
        Log.e("aa", "" + height)
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
        if(height == Finals.SELECT_EMPTY) view.text = " ▼"
        else if(height == Finals.SELECT_CHECK) view.text = " ▲"
    }

    @JvmStatic
    @BindingAdapter("Main_title_font2")
    fun main_title_font2(view: TextView, height: Int) {
        if(height == Finals.SELECT_MAP) view.text = "관제지도 "
        else if(height == Finals.SELECT_ORDER) view.text = "오더현황 "
    }

    @JvmStatic
    @BindingAdapter("Slide_low_layer")
    fun setSlideLowLayer(view: SlidingUpPanelLayout, height: Boolean) {
        if(height == true)
        {
            view.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            view.isTouchEnabled = false // 이거 풀면 리스트뷰랑 터치공유를 하게되서 풀면안됨
        }
        else
        {
            view.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            view.isTouchEnabled = false // 이거 풀면 리스트뷰랑 터치공유를 하게되서 풀면안됨
        }
    }

    @JvmStatic
    @BindingAdapter("select_item_backcolor")
    fun setitembackcolor(view: LinearLayout, height: Boolean) {
        if (height == false) view.setBackgroundColor(Vars.mContext!!.getColor(R.color.lightgray))
        else view.setBackgroundColor(Vars.mContext!!.getColor(R.color.orange))
    }

    @JvmStatic
    @BindingAdapter("Main_Bright")
    fun setBright(view: LinearLayout, height: Int) {
        if (height == 1) view.setBackgroundColor(-0x38000000) //200
        else if (height == 2) view.setBackgroundColor(-0x4c000000) //180
        else if (height == 3) view.setBackgroundColor(-0x60000000) //160
        else if (height == 4) view.setBackgroundColor(-0x74000000) //140
        else if (height == 5) view.setBackgroundColor(0x78000000) //120
        else if (height == 6) view.setBackgroundColor(0x64000000) //100
        else if (height == 7) view.setBackgroundColor(0x50000000) //80
        else if (height == 8) view.setBackgroundColor(0x3C000000) //60
        else if (height == 9) view.setBackgroundColor(0x28000000) //40
        else view.setBackgroundColor(0x00000000)
    }

    @JvmStatic
    @BindingAdapter("sub_item_size")
    fun setSubItemSize(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height > 2)
        {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = 180
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("maquee")
    fun setMaquee(view: TextView, height: Int) {
        if(height == True) view.isSelected = true
    }
}