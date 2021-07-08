package com.beyondinc.commandcenter.adapter

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beyondinc.commandcenter.Interface.MainsFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.text.DecimalFormat


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
    @BindingAdapter("onTouch")
    fun setTouchListener(view: View, callback: () -> Boolean) {
        view.setOnTouchListener { v, event -> callback() }
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
        if (velue == Finals.SELECT_BRIFE)
        {
            view.setBackgroundResource(R.drawable.menu_tab)
            view.setTextColor(Vars.mContext!!.getColor(R.color.black))
        }
        else
        {
            view.background = null
            view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
        }
    }

    @JvmStatic
    @BindingAdapter("text_color_store")
    fun setTextStore(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_STORE)
        {
            view.setBackgroundResource(R.drawable.menu_tab)
            view.setTextColor(Vars.mContext!!.getColor(R.color.black))
        }
        else
        {
            view.background = null
            view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
        }
    }

    @JvmStatic
    @BindingAdapter("text_color_rider")
    fun setTextRider(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_RIDER)
        {
            view.setBackgroundResource(R.drawable.menu_tab)
            view.setTextColor(Vars.mContext!!.getColor(R.color.black))
        }
        else
        {
            view.background = null
            view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
        }
    }

    @JvmStatic
    @BindingAdapter("text_color_map")
    fun setTextMaptoOder(view: TextView, velue: Int) {
        if (velue == Finals.SELECT_MAP)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.text = "오더목록"
        }
        else if(velue == Finals.SELECT_ORDER)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.text = "지도화면"
        }
        else if(velue == Finals.SELECT_MENU)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.text = "메인화면"
        }
        else if(velue == Finals.SELECT_AGENCY)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.text = "가맹점목록"
        }
        else if(velue == Finals.SELECT_RIDERLIST)
        {
            (Vars.mContext as MainsFun).setFragment()
            view.text = "라이더목록"
        }
    }

    @JvmStatic
    @BindingAdapter("menu_title")
    fun setMenutitle(view: TextView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if(height == Finals.SELECT_ORDER || height == Finals.SELECT_MAP)
        {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("menu_title_check_btn")
    fun setMenutitlecheckbtn(view: TextView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        var i = Vars.centerList.keys.count() - Vars.f_center.count()
        if(height == Finals.SELECT_ORDER)
        {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
            view.text = "오더현황 (${i}) ▼"
        }
        else if(height == Finals.SELECT_MAP)
        {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
            view.text = "관제지도 (${i}) ▼"
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
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
    @BindingAdapter("layout_form_list_view")
    fun setLayout_listview(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_RIDERLIST || height == Finals.SELECT_AGENCY) {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        } else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_form_list_view_rider")
    fun setLayout_listview_rider(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_RIDERLIST) {
            layoutParams.weight = 1f
            view.layoutParams = layoutParams
        } else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_form_header")
    fun setLayoutheader(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_RIDERLIST) {
            layoutParams.weight = 4f
            view.layoutParams = layoutParams
        }
        else if(height != Finals.SELECT_MENU) {
            layoutParams.weight = 2f
            view.layoutParams = layoutParams
        }
        else {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_setting_btn")
    fun setSettingBtn(view: TextView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_MENU) {
            layoutParams.width = 80
            layoutParams.height = 80
            layoutParams.rightMargin = 20
            view.layoutParams = layoutParams
        } else {
            layoutParams.width = 0
            layoutParams.height = 0
            layoutParams.rightMargin = 0
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("layout_form_btn")
    fun setLayoutbtn(view: TextView, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == Finals.SELECT_ORDER || height == Finals.SELECT_MAP) {
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
    fun setCustomCheckLogin(view: TextView, height: Boolean) {
        if (height) {
            view.setBackgroundResource(R.drawable.checkbox_sel)
        } else {
            view.setBackgroundResource(R.drawable.checkbox_nor)
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
    @BindingAdapter("Main_title_font", "Main_title_font_sub")
    fun main_title_font(view: TextView, height: Int, sub:Int) {
        var i = Vars.centerList.keys.count() - Vars.f_center.count()

        if(height == Finals.SELECT_EMPTY)
        {
            if(sub == Finals.SELECT_ORDER) view.text = "오더현황 (${i}) ▼"
            else view.text = "지도화면 (${i}) ▼"
        }
        else if(height == Finals.SELECT_CHECK)
        {
            if(sub == Finals.SELECT_ORDER) view.text = "오더현황 (${i}) ▲"
            else view.text = "지도화면 (${i}) ▲"
        }
    }

    @JvmStatic
    @BindingAdapter("Main_title_font2")
    fun main_title_font2(view: TextView, height: Int) {
        if(height == Finals.SELECT_MAP)
        {
            view.text = "관제지도"
            view.gravity = Gravity.CENTER
            view.isClickable = true
        }
        else if(height == Finals.SELECT_ORDER)
        {
            view.text = "오더현황"
            view.gravity = Gravity.CENTER
            view.isClickable = true
        }
        else if(height == Finals.SELECT_AGENCY)
        {
            view.text = "가맹점현황"
            view.gravity = Gravity.CENTER
            view.isClickable = false
        }
        else if(height == Finals.SELECT_MENU)
        {
            view.text = "통합관제 시스템"
            view.gravity = Gravity.CENTER
            view.isClickable = false
        }
        else if(height == Finals.SELECT_RIDERLIST)
        {
            view.text = "라이더현황"
            view.gravity = Gravity.CENTER
            view.isClickable = false
        }
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
        else if(height == Finals.DETAIL_AGENCY) view.text = "가맹점상세"
        else if(height == Finals.DETAIL_RIDER) view.text = "라이더상세"
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
        if(height) view.setBackgroundResource(R.drawable.arrow_down)
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
    }

    @JvmStatic
    @BindingAdapter("Address_top_layer")
    fun toplayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 0 || height == 4 || height == 5)
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
    @BindingAdapter("Address_number_layer")
    fun numberlayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 0 || height == 2 || height == 3 || height == 1)
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
    @BindingAdapter("Address_agency_addr")
    fun Address_agency_addr(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height != 6)
        {
            layoutParams.weight = 10F
            layoutParams.setMargins(30)
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            layoutParams.setMargins(0)
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_agency_assign")
    fun Address_agency_assign(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 6)
        {
            layoutParams.weight = 10F
            layoutParams.setMargins(30)
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            layoutParams.setMargins(0)
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_map_layer")
    fun maplayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 4)
        {
            layoutParams.weight = 8F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 0f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_map_layer_bottom")
    fun maplayer_bottom(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 4)
        {
            layoutParams.weight = 0F
            view.layoutParams = layoutParams
        }
        else
        {
            layoutParams.weight = 8f
            view.layoutParams = layoutParams
        }
    }

    @JvmStatic
    @BindingAdapter("Address_finish_layer")
    fun finishlayer(view: LinearLayout, height: Int) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (height == 5 || height == 4 || height == 6)
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
        if(cnt > 0 && !sub)
        {
            view.setBackgroundResource(R.drawable.arrow_up_red)
        }
        else if(!sub)
        {
            view.setBackgroundResource(R.drawable.arrow_up)
        }
    }

    @JvmStatic
    @BindingAdapter("filter_title_Agency" , "filter_title_Agency_type")
    fun filter_title_Agency(view: TextView, cnt : String , type:Int) {
        if((!cnt.isNullOrEmpty() || cnt != "") && type == Finals.SELECT_STORE)
        {
            view.textSize = 15f
            view.text = "가맹점\n(${cnt})"
            val content: String = view.text.toString()
            val spannableString = SpannableString(content)
            val start: Int = content.indexOf(cnt) - 1
            val end: Int = start + cnt.length + 2
            spannableString.setSpan(RelativeSizeSpan(0.8f),start,end,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = spannableString;
        }
        else
        {
            view.textSize = 25f
            view.text = "가맹점"
        }
    }

    @JvmStatic
    @BindingAdapter("filter_title_Rider","filter_title_Rider_type")
    fun filter_title_Rider(view: TextView, cnt : String , type:Int) {
        if((!cnt.isNullOrEmpty() || cnt != "") && type == Finals.SELECT_RIDER)
        {
            view.textSize = 15f
            view.text = "라이더\n(${cnt})"
            val content: String = view.text.toString()
            val spannableString = SpannableString(content)
            val start: Int = content.indexOf(cnt) - 1
            val end: Int = start + cnt.length + 2
            spannableString.setSpan(RelativeSizeSpan(0.8f),start,end,SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            view.text = spannableString
        }
        else
        {
            view.textSize = 25f
            view.text = "라이더"
        }
    }

    @JvmStatic
    @BindingAdapter("Agency_working")
    fun agency_working(view: TextView, cnt : String) {
        if(cnt == "Y")
        {
            view.text = "영업중"
            view.setTextColor(Vars.mContext!!.getColor(R.color.pickup))
        }
        else
        {
            view.text = "영업종료"
            view.setTextColor(Vars.mContext!!.getColor(R.color.brief))
        }
    }

    @JvmStatic
    @BindingAdapter("Agency_loginTime")
    fun agency_loginTime(view: TextView, cnt : String) {
        var title = "최근 로그인일시:"
        var value : String? = null
        if(cnt.isNullOrEmpty() || cnt == "") value = "없음"
        else value = cnt
        title += value
        val ssb = SpannableStringBuilder(title)
        ssb.setSpan(
            ForegroundColorSpan(Vars.mContext!!.getColor(R.color.complite)),
            title.indexOf(value),
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = ssb
    }

    @JvmStatic
    @BindingAdapter("Agency_manage")
    fun agency_manage(view: TextView, cnt : String) {
        var title = "관리비:"
        var value : String? = null
        if(cnt.isNullOrEmpty() || cnt == "") value = "미설정"
        else value = cnt
        title += value
        val ssb = SpannableStringBuilder(title)
        ssb.setSpan(
            ForegroundColorSpan(Vars.mContext!!.getColor(R.color.complite)),
            title.indexOf(value),
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.text = ssb
    }

    @JvmStatic
    @BindingAdapter("arrive_time_color")
    fun arrive_time_color(view: TextView, cnt : String) {
        var title = cnt
        var value : String? = null
        if(title.isNullOrEmpty() || title == "0") view.text = ""
        else
        {
            value = "분후 도착"
            title += value
            val ssb = SpannableStringBuilder(title)
            ssb.setSpan(
                ForegroundColorSpan(Vars.mContext!!.getColor(R.color.brief)),
                0,
                title.indexOf(value),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            view.text = ssb
        }
    }

    @JvmStatic
    @BindingAdapter("Agency_SumAmt")
    fun agency_sumamt(view: TextView, cnt : String) {
        val df = DecimalFormat("#,###")
        val result = df.format(cnt.toLong())
        view.text = result + "원"
        if(cnt.toLong() < 0) view.setTextColor(Vars.mContext!!.getColor(R.color.brief))
        else view.setTextColor(Vars.mContext!!.getColor(R.color.menu1))
    }

    @JvmStatic
    @BindingAdapter("Riderlist_work")
    fun Riderlist_work(view: TextView, cnt : String) {
        if(cnt == "Y")
        {
            view.setTextColor(Vars.mContext!!.getColor(R.color.complite))
            view.text = "출근"
        }
        else
        {
            view.setTextColor(Vars.mContext!!.getColor(R.color.brief))
            view.text = "퇴근"
        }
    }

    @JvmStatic
    @BindingAdapter("Riderlist_state")
    fun Riderlist_state(view: TextView, cnt : String) {
        if(cnt == "1001")
        {
            view.setTextColor(Vars.mContext!!.getColor(R.color.pickup))
            view.text = "재직"
        }
        else
        {
            view.setTextColor(Vars.mContext!!.getColor(R.color.gray))
            view.text = "퇴사"
        }
    }

    @JvmStatic
    @BindingAdapter("Agency_SetUse")
    fun agency_SetUse(view: LinearLayout, cnt : Boolean) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (!cnt)
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
    @BindingAdapter("Agency_SetUse_color")
    fun agency_SetUse_color(view: LinearLayout, cnt : Boolean) {
        val layoutParams = view.layoutParams as LinearLayout.LayoutParams
        if (!cnt)
        {
            view.setBackgroundColor(Vars.mContext!!.getColor(R.color.lightgray))
        }
        else
        {
            view.setBackgroundResource(R.drawable.balloon)
        }
    }

    @JvmStatic
    @BindingAdapter("focusEditText")
    fun setFocusEditText(view: EditText, cnt : String) {
        view.setSelection(view.text.length)
    }
}