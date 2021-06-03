package com.beyondinc.commandcenter.adapter

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
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
        if (velue == "Y") {
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
        if (velue == Finals.SELECT_BRIFE) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_store")
    fun setTextStore(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_STORE) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_rider")
    fun setTextRider(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_RIDER) view.setTextColor(Vars.mContext!!.getColor(R.color.white))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
    }

    @JvmStatic
    @BindingAdapter("text_color_map")
    fun setTextMaptoOder(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_MAP)
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
    @BindingAdapter("layout_form")
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
    @BindingAdapter("layout_breif")
    fun setLayout_breif(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_BRIFE) {
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
        if (height == Finals.SELECT_CHECK) {
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
    @BindingAdapter("layout_addr")
    fun setLayoutaddr(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == True) {
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT
            view.layoutParams = layoutParams
        } else {
            layoutParams.width = 0
            layoutParams.height = 0
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_tranform")
    fun setLayouttranform(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == True) {
            layoutParams.weight = 12f
            view.layoutParams = layoutParams
        } else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_txt")
    fun setLayouttxt(view: TextView, height: Int) {
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
    @BindingAdapter("custom_checkbox")
    fun setCustomCheck(view: View, height: Boolean) {
        if (height) {
            view.setBackgroundResource(R.drawable.checkbox_sel)
        } else {
            view.setBackgroundResource(R.drawable.checkbox_nor)
        }
    }

    @JvmStatic
    @BindingAdapter("custom_checkbox_login")
    fun setCustomCheckLogin(view: CheckBox, height: Boolean) {
        if (height) {
            view.setButtonDrawable(R.drawable.k_login_check_p)
        } else {
            view.setButtonDrawable(R.drawable.k_login_check_n)
        }
    }

    @JvmStatic
    @BindingAdapter("drawer_open")
    fun setDrawer_Open(view: DrawerLayout, height: Boolean) {
        if (height) {
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
        //Log.e("aa", "" + height)
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
        if(height.length == 1) view.textSize = 20F
        else if(height.length == 2) view.textSize = 17F
        else if(height.length == 3) view.textSize = 14F
        else if(height.length == 4) view.textSize = 11F
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
        if (height) view.setBackgroundColor(Vars.mContext!!.getColor(R.color.orange))
        else view.setBackgroundColor(Vars.mContext!!.getColor(R.color.lightgray))
    }

    @JvmStatic
    @BindingAdapter("select_dong_backcolor")
    fun setitemdongcolor(view: LinearLayout, height: Boolean) {
        if (height) view.setBackgroundColor(Vars.mContext!!.getColor(R.color.whitegray))
        else view.setBackgroundColor(Vars.mContext!!.getColor(android.R.color.transparent))
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
        //Log.e("item_Size" , " //// $height")
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height > 2)
        {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = 500
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

    @JvmStatic
    @BindingAdapter("detail1")
    fun setDetailBackColor1(view: TextView, height: String) {
        if(height == "접수")
        {
            view.isClickable = true
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.black))
        }
        else
        {
            view.isClickable = false
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.gray))
        }
    }
    @JvmStatic
    @BindingAdapter("detail2")
    fun setDetailBackColor2(view: TextView, height: String) {
        if(height == "배정")
        {
            view.isClickable = true
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.black))
        }
        else
        {
            view.isClickable = false
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.gray))
        }
    }
    @JvmStatic
    @BindingAdapter("detail3")
    fun setDetailBackColor3(view: TextView, height: String) {
        if(height == "완료" || height == "취소" || height == "접수")
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.gray))
            view.isClickable = false
        }
        else
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.black))
            view.isClickable = true
        }
    }

    @JvmStatic
    @BindingAdapter("detail4")
    fun setDetailBackColor4(view: TextView, height: String) {
        if(height == "완료" || height == "취소")
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.gray))
            view.isClickable = false
        }
        else
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.black))
            view.isClickable = true
        }
    }

    @JvmStatic
    @BindingAdapter("detail_title")
    fun setDetailBackTitle(view: TextView, height: Int) {
        if(height == Finals.DETAIL_MAP) view.text = "지도보기"
        else view.text = "오더상세"
    }

    @JvmStatic
    @BindingAdapter("detail_back")
    fun setDetailBack(view: TextView, height: Int) {
        if(height == Finals.DETAIL_MAP) view.setBackgroundResource(R.drawable.abc_vector_test)
        else view.setBackgroundResource(R.color.black)
    }

    @JvmStatic
    @BindingAdapter("map_assign_btn")
    fun setMapassign(view: TextView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == True)
        {
            layoutParams.weight = 3F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("assign_back")
    fun setAssign_Back(view: TextView, height: String) {
        if(height == "배정")
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.trans_recive))
        }
        else
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.trans_pickup))
        }
    }

    @JvmStatic
    @BindingAdapter("item_font_size")
    fun item_font_size(view: TextView, height: Int) {
        view.textSize = height.toFloat()
    }

    @JvmStatic
    @BindingAdapter("item_font_size_title")
    fun item_font_size_title(view: TextView, height: Int) {
        view.textSize = height.toFloat() + 2
    }

    @JvmStatic
    @BindingAdapter("item_font")
    fun item_font(view: Spinner, height: String) {
        if(height == "현금") view.setSelection(0)
        else if(height == "카드") view.setSelection(1)
        else if(height == "선결제") view.setSelection(2)
    }

    @JvmStatic
    @BindingAdapter("scroll_up")
    fun scroll_up(view: RecyclerView, height: Int) {
        if(height > 0) view.smoothScrollToPosition(0)
    }

    @JvmStatic
    @BindingAdapter("sub_scroll_up")
    fun sub_scroll_up(view: TextView, height: Boolean) {
        if(height == true) view.setBackgroundResource(R.drawable.arrow_down)
        else view.setBackgroundResource(R.drawable.arrow_up)
    }

    @JvmStatic
    @BindingAdapter("sel_Addr")
    fun sel_Addr(view: TextView, height: String) {
        if(height =="Jibun") view.text = "지번검색"
        else if(height == "Road") view.text = "도로명검색"
        else if(height == "Build") view.text = "건물명검색"
    }

    @JvmStatic
    @BindingAdapter("focus")
    fun focus(view: EditText, height: Boolean) {
        var focusView: View = view
        var immhide : InputMethodManager = Vars.mContext!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        immhide?.hideSoftInputFromWindow(focusView?.windowToken, 0)
    }

    @JvmStatic
    @BindingAdapter("spin_bun")
    fun spin_bun(view: Spinner, height: Int) {
        view.setSelection(height)
        view.setSelection(height)
    }

    @JvmStatic
    @BindingAdapter("Address_top_layer")
    fun toplayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 0 || height == 4)
        {
            layoutParams.weight = 0F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_map_layer")
    fun maplayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 4)
        {
            layoutParams.weight = 1F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_finish_layer")
    fun finishlayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 5 || height == 4)
        {
            layoutParams.weight = 1F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_rec_layer")
    fun reclayer(view: RecyclerView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 2)
        {
            layoutParams.weight = 1F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_sel_layer")
    fun sellayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 5 || height == 4)
        {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.width = 0
            layoutParams.height = 0
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_sel2_layer")
    fun sel2layer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 5 || height == 4)
        {
            layoutParams.width = 0
            layoutParams.height = 0
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
    @BindingAdapter("Address_selbtn_layer")
    fun selbtnlayer(view: RadioGroup, height: Int) {
        if (height == 5 || height == 4)
        {
            view.clearCheck()
        }
    }

    @JvmStatic
    @BindingAdapter("Address_clear_layer")
    fun clearlayer(view: EditText, height: Int) {
        if (height == 5 || height == 4)
        {
            view.text.clear()
        }
    }

    @JvmStatic
    @BindingAdapter("Address_main_layer")
    fun mainlayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 4 || height == 5)
        {
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.width = 0
            layoutParams.height = 0
            view.layoutParams = layoutParams
        }
    }


    @JvmStatic
    @BindingAdapter("cnt","sub")
    fun balloon(view: TextView, cnt: Int, sub:Boolean) {

        if(sub)
        {
            val str = "${cnt}건"
            val ssb = SpannableStringBuilder(str)
            ssb.setSpan(
                ForegroundColorSpan(Vars.mContext!!.getColor(R.color.brief)),
                0,
                if(cnt > 9) 2
                else if (cnt > 99) 3
                else if (cnt > 999) 4
                else 1, //꼭 1자리라는 법은 없지
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = ssb
        }
        else
        {
            if(cnt == 0) view.text = "접수가 하나도 없어요.."
            else
            {
                val str = "접수가 ${cnt}건 있어요!"
                val ssb = SpannableStringBuilder(str)
                ssb.setSpan(
                    ForegroundColorSpan(Vars.mContext!!.getColor(R.color.brief)),
                    4,
                    if(cnt > 9) 6
                    else if (cnt > 99) 7
                    else if (cnt > 999) 8
                    else 5, //꼭 1자리라는 법은 없지
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                view.text = ssb
            }
        }
    }
}