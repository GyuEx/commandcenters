package com.beyondinc.commandcenter.handler

import android.graphics.Color
import android.util.Log
import com.beyondinc.commandcenter.Interface.ThreadFun
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.util.Codes
import com.beyondinc.commandcenter.util.Finals
import com.beyondinc.commandcenter.util.Vars
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class MarkerThread : Thread() , ThreadFun {

    var isKeep : Boolean = false
    var imgGray = OverlayImage.fromResource(R.drawable.ic_marker_assigned_rider)
    var imgBlue = OverlayImage.fromResource(R.drawable.ic_marker_idle_rider)
    //오버레이 재활용 안하면 메모리릭이 심하다고함, 네이버지도 개발가이드 발췌

    init {
        isKeep = true
    }

    override fun stopThread() {
        isKeep = false
    }

    override fun run()
    {

        var cntj : Int = 0
        var cntu : Int = 0
        var cntd : Int = 0
        var cnts : Int = 0
        var cntt : Int = 0

        if(Vars.riderList.keys.size > 0) // 라이더맵 파악하여 초기/신규라이더가 있는지 여부 확인 마커생성
        {
            var it : Iterator<String> = Vars.riderList.keys.iterator()
            while (it.hasNext())
            {
                var itt = it.next()

                // 배정 카운터 계산
                Vars.riderList[itt]?.assignCount = 0
                Vars.riderList[itt]?.pickupCount = 0
                Vars.riderList[itt]?.completeCount = 0

                var rit : Iterator<String> = Vars.orderList.keys.iterator()
                while (rit.hasNext())
                {
                    var rtemp = rit.next()
                    if ((Vars.orderList[rtemp]!!.DeliveryStateName != "접수" || Vars.orderList[rtemp]!!.DeliveryStateName != "취소") && Vars.orderList[rtemp]!!.RiderId != "0") {
                        if (Vars.riderList.containsKey(Vars.orderList[rtemp]!!.RiderId)) {
                            if (Vars.orderList[rtemp]!!.DeliveryStateName == "배정" && Vars.orderList[rtemp]!!.RiderId == Vars.riderList[itt]!!.id) Vars.riderList[itt]!!.assignCount += 1
                            else if (Vars.orderList[rtemp]!!.DeliveryStateName == "픽업" && Vars.orderList[rtemp]!!.RiderId == Vars.riderList[itt]!!.id) Vars.riderList[itt]!!.pickupCount += 1
                            else if (Vars.orderList[rtemp]!!.DeliveryStateName == "완료" && Vars.orderList[rtemp]!!.RiderId == Vars.riderList[itt]!!.id) Vars.riderList[itt]!!.completeCount += 1
                        }
                    }
                }

                cntj++ // 전체 리스트임
                if (Vars.riderList[itt]!!.workingStateCode!! == Codes.RIDER_OFF_WORK) cntt++
                else if (Vars.riderList[itt]!!.workingStateCode!! == Codes.RIDER_ON_WORK && (Vars.riderList[itt]?.pickupCount!! > 0 || Vars.riderList[itt]?.assignCount!! > 0)) cntu++
                else if (Vars.riderList[itt]!!.workingStateCode!! == Codes.RIDER_ON_WORK && Vars.riderList[itt]?.pickupCount!! == 0 && Vars.riderList[itt]?.assignCount!! == 0) cntd++
                else if (Vars.riderList[itt]!!.workingStateCode!! == Codes.RIDER_ON_EAT) cnts++

                if(Vars.riderList[itt]!!.MakerID == null && Vars.riderList[itt]?.latitude != "0") // 마커가 없을경우 생성해줌
                {
                    var marker = Marker()
                    val latitude = Vars.riderList[itt]?.latitude?.toDouble()
                    val longitude = Vars.riderList[itt]?.longitude?.toDouble()
                    val position = LatLng(latitude!!, longitude!!)
                    marker.position = position
                    marker.setCaptionAligns(Align.Center)
                    marker.captionColor = Color.WHITE
                    marker.captionHaloColor = Color.TRANSPARENT
                    marker.captionText = "${Vars.riderList[itt]?.name.toString()} \n ${Vars.riderList[itt]!!.assignCount!!.toInt()} / ${Vars.riderList[itt]!!.pickupCount!!.toInt()} \n "
                    marker.captionTextSize = 13F
                    if(Vars.riderList[itt]!!.assignCount!!.toInt() == 0 && Vars.riderList[itt]!!.pickupCount!!.toInt() == 0) marker.icon = imgBlue
                    else marker.icon = imgGray
                    marker.setOnClickListener {
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_SUBRIDER,Finals.MAP_FOR_CALL_RIDER, 0,Vars.riderList[itt]!!).sendToTarget()
                        // 메인맵이 아니라 서브에 주는 이유는 어짜피 서브에서 선택하는형태로 주기 위함임, 메인라이더, 서브라이더 별개로 운영하면 중복이 되거나 오더가 잘못 내려갈 소지가 있음
                        // 1. 접수오더는 메인맵뷰모델에서 제어하기 때문 ,  2. 커스텀마커는 마커ID만으로 동기배열(전체삭제,전체생성)을 반복하지 않는이상 찾을 방법이 없음
                        true
                    }
                    Vars.riderList[itt]!!.MakerID = marker // 라이더에게 마커를 지정해줌

                    if(Vars.riderList[itt]!!.workingStateCode == Codes.RIDER_ON_WORK) Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.CREATE_RIDER_MARKER, 0, Vars.riderList[itt]!!).sendToTarget()
                }
                else if(Vars.riderList[itt]!!.MakerID != null)
                {
                    if(Vars.riderList[itt]!!.workingStateCode != Codes.RIDER_ON_WORK || Vars.f_center.contains(Vars.riderList[itt]!!.centerID))
                    {
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.REMOVE_RIDER_MARKER, 0,Vars.riderList[itt]!!).sendToTarget()
                    }
                    else
                    {
                        Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAP,Finals.UPDATE_RIDER_MARKER, 0,Vars.riderList[itt]!!).sendToTarget()
                    }
                }
            }

            var forstr = "전:${cntj} 운:${cntu} 대:${cntd} 식:${cnts} 퇴:${cntt}"
            Vars.DataHandler!!.obtainMessage(Finals.VIEW_MAIN,Finals.INSERT_RIDER_COUNT, 0,forstr).sendToTarget() // 메인뷰에 전체 카운터를 제공한다
        }
    }
}