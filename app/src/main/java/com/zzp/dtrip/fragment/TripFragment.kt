package com.zzp.dtrip.fragment

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.LocationSource.OnLocationChangedListener
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.SearchActivity
import com.zzp.dtrip.activity.SearchableActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class TripFragment : Fragment(), TencentLocationListener, LocationSource {

    companion object {
        var city = ""
        var position = -1
    }

    private lateinit var mapView: MapView

    private lateinit var searchEdit: EditText

    private lateinit var tencentMap: TencentMap

    private lateinit var locationManager: TencentLocationManager

    private lateinit var locationRequest: TencentLocationRequest

    private lateinit var uiSettings: UiSettings

    private var locationChangedListener: OnLocationChangedListener? = null

    private val TAG = "TripFragment"

    private var flag = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_trip, container, false)

        requirePermission()
        initRequest()
        locationManager = TencentLocationManager.getInstance(requireContext())
        mapView = root.findViewById(R.id.map_view)
        searchEdit = root.findViewById(R.id.search_edit)
        searchEdit.setOnClickListener {
            val intent = Intent(context, SearchableActivity::class.java)
            startActivity(intent)
        }
        //获取地图实例
        tencentMap = mapView.map

        //第一次渲染成功的回调
        tencentMap.addOnMapLoadedCallback {
            //地图正常显示
            initMap()
        }
        return root
    }

    private fun initMap() {
        //地图上设置定位数据源
        tencentMap.setLocationSource(this)
        //设置当前位置可见
        tencentMap.isMyLocationEnabled = true

        uiSettings = tencentMap.uiSettings
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isCompassEnabled = true
        tencentMap.setMyLocationStyle(MyLocationStyle().
            myLocationType(LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER))

//        //添加一个地图中心点标注
//        var marker = tencentMap.addMarker(MarkerOptions(tencentMap.cameraPosition.target))

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        searchEdit.clearFocus()
        if (position != -1) {
            searchEdit.setText(SearchActivity.resultList[position].title)
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLocationChanged(tencentLocation: TencentLocation?, error: Int, reason: String?) {
        //其中 locationChangeListener 为 LocationSource.active 返回给用户的位置监听器
        //用户通过这个监听器就可以设置地图的定位点位置
        if (error == TencentLocation.ERROR_OK && locationChangedListener != null) {
            if (tencentLocation != null) {
                val location = Location(tencentLocation.provider)
                //设置经纬度
                location.latitude = tencentLocation.latitude
                location.longitude = tencentLocation.longitude
                //设置精度，这个值会被设置为定位点上表示精度的圆形半径
                location.accuracy = tencentLocation.accuracy
                //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
                location.bearing = tencentLocation.bearing
                //将位置信息返回给地图
                locationChangedListener!!.onLocationChanged(location)
                if (!flag) {
                    //设置一个新的地图中心点标注
                    //设置一个新的地图中心点标注
                    val newLatLng = tencentMap.cameraPosition.target
                    //把地图变换到指定的状态,生成一个把地图移动到指定的经纬度到屏幕中心的状态变化对象
                    //把地图变换到指定的状态,生成一个把地图移动到指定的经纬度到屏幕中心的状态变化对象
                    newLatLng.latitude = tencentLocation.latitude
                    newLatLng.longitude = tencentLocation.longitude
                    tencentMap.moveCamera(CameraUpdateFactory.newLatLng(newLatLng))
                    tencentMap.addMarker(MarkerOptions(newLatLng))
                    flag = !flag
                }
            }
        }
    }

    override fun onStatusUpdate(name: String?, status: Int, desc: String?) {

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @AfterPermissionGranted(1)
    private fun requirePermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionsForQ = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,  //target为Q时，动态请求后台定位权限
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (if (Build.VERSION.SDK_INT >= 29)
            EasyPermissions.hasPermissions(requireContext(), *permissionsForQ)
            else EasyPermissions.hasPermissions(requireContext(), *permissions)) {
                Toast.makeText(requireContext(), "权限OK", Toast.LENGTH_LONG).show()
            }
        else {
            if (Build.VERSION.SDK_INT >= 29) {
                EasyPermissions.requestPermissions(
                    requireActivity(), "需要权限",
                    1, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,  //target为Q时，动态请求后台定位权限
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            else {
                EasyPermissions.requestPermissions(
                    requireActivity(), "需要权限",
                    1, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, requireContext())
    }

    override fun activate(onLocationChangedListener: OnLocationChangedListener) {
        //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
        locationChangedListener = onLocationChangedListener
        //开启定位
        val err: Int = locationManager.requestLocationUpdates(
            locationRequest, this, Looper.myLooper())
        when (err) {
            1 -> Toast.makeText(
                requireContext(),
                "设备缺少使用腾讯定位服务需要的基本条件",
                Toast.LENGTH_SHORT
            ).show()
            2 -> Toast.makeText(
                requireContext(),
                "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT
            ).show()
            3 -> Toast.makeText(
                requireContext(),
                "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT
            ).show()
            else -> {
            }
        }
    }

    override fun deactivate() {
        //当不需要展示定位点时，需要停止定位并释放相关资源
        locationManager.removeUpdates(this)
        locationChangedListener = null
    }

    private fun initRequest() {
        locationRequest = TencentLocationRequest.create()
        locationRequest.interval = 1000
        locationRequest.isAllowGPS = true
        locationRequest.requestLevel = TencentLocationRequest. REQUEST_LEVEL_ADMIN_AREA
        locationRequest.isAllowDirection = true
        locationRequest.isIndoorLocationMode = true
    }
}



