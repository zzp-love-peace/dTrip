package com.zzp.dtrip.fragment

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.param.BicyclingParam
import com.tencent.lbssearch.`object`.param.DrivingParam
import com.tencent.lbssearch.`object`.param.TransitParam
import com.tencent.lbssearch.`object`.param.WalkingParam
import com.tencent.lbssearch.`object`.result.BicyclingResultObject
import com.tencent.lbssearch.`object`.result.DrivingResultObject
import com.tencent.lbssearch.`object`.result.TransitResultObject
import com.tencent.lbssearch.`object`.result.WalkingResultObject
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.map.tools.net.http.HttpResponseListener
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.LocationSource.OnLocationChangedListener
import com.tencent.tencentmap.mapsdk.maps.model.LatLng
import com.tencent.tencentmap.mapsdk.maps.model.Marker
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.SearchActivity
import com.zzp.dtrip.activity.SocialActivity
import com.zzp.dtrip.activity.SoundActivity
import com.zzp.dtrip.adapter.RouteAdapter
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class TripFragment : Fragment(), TencentLocationListener, LocationSource {

    companion object {
        var city = ""
        var position = -1
        var lat = 0.0
        var lng = 0.0
    }

    private lateinit var mapView: MapView

    private lateinit var tencentMap: TencentMap

    // 用来展示搜索结果的 Marker
    private var targetMarker: Marker? = null

    private lateinit var locationManager: TencentLocationManager

    private lateinit var locationRequest: TencentLocationRequest

    private lateinit var uiSettings: UiSettings

    private lateinit var searchEdit: EditText

    private lateinit var aroundButton: ImageButton
    private lateinit var sayingButton: ImageButton
    private lateinit var hearingButton: ImageButton

    private lateinit var sheetLayout: CoordinatorLayout
    private lateinit var sheetContent: LinearLayout
    private lateinit var sheetContentBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sheetTitleText: TextView
    private lateinit var sheetAddressText: TextView
    private lateinit var sheetRouteButton: FloatingActionButton
    // 四个交通方式按钮
    private lateinit var sheetRouteDriveButton: ImageButton
    private lateinit var sheetRouteWalkButton: ImageButton
    private lateinit var sheetRouteTransitButton: ImageButton
    private lateinit var sheetRouteBicycleButton: ImageButton
    // 目前位置
    private lateinit var currentLocation: TencentLocation
    // 路线 recycler 及 adapter
    private lateinit var routeDivider: View
    private lateinit var routeRecycler: RecyclerView
    private var routeAdapter: RouteAdapter? = null

    private var locationChangedListener: OnLocationChangedListener? = null

    private val TAG = "TripFragment"

    private var flag = false

    // 避免腾讯地图闪烁
    private var openingSearch = false

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_trip, container, false)

        requirePermission()
        initRequest()
        findViewById(root)

        locationManager = TencentLocationManager.getInstance(requireContext())
        searchEdit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) return@setOnFocusChangeListener
            val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), v, "search_edit")
            // start the new activity
            val intent = Intent(requireActivity(), SearchActivity::class.java)
            startActivity(intent, options.toBundle())
            openingSearch = true
//            startActivity(intent)
        }
        //获取地图实例
        tencentMap = mapView.map

        //第一次渲染成功的回调
        tencentMap.addOnMapLoadedCallback {
            //地图正常显示
            initMap()
        }
        sayingButton.setOnClickListener {
            val intent = Intent(requireContext(), SocialActivity::class.java)
            startActivity(intent)
        }
        hearingButton.setOnClickListener {
            val intent = Intent(requireContext(), SoundActivity::class.java)
            startActivity(intent)
        }
        aroundButton.setOnClickListener {  }

        // 选择交通方式
        // TODO: 实现选中路线、绘制道路
        // TODO: 优化 bottom sheet 呈现方式
        // TODO: 页面跳转后重置 button 和 recyclerView
        arrayOf(sheetRouteDriveButton, sheetRouteWalkButton, sheetRouteTransitButton, sheetRouteBicycleButton).let { array ->
            array.forEach { button ->
                button.setOnClickListener { _ ->
                    array.forEach {
                        it.background = ResourcesCompat.getDrawable(resources, if (button.id == it.id) R.drawable.background_transportation else android.R.color.transparent, requireActivity().theme)
                    }
                    // load routes corresponding to selected transportation
                    when (button.id) {
                        R.id.sheet_route_drive_button -> { // Drive
                            val drivingParam = DrivingParam().apply{
                                from(LatLng(currentLocation.latitude, currentLocation.longitude))
                                to(LatLng(SearchActivity.resultList[position].location.lat, SearchActivity.resultList[position].location.lng))
                            }
                            TencentSearch(requireContext()).getRoutePlan(drivingParam, object: HttpResponseListener<DrivingResultObject> {
                                override fun onSuccess(p0: Int, p1: DrivingResultObject?) {
                                    if (p1 != null) {
                                        if (routeAdapter == null) {
                                            routeAdapter = RouteAdapter(p1, RouteAdapter.RouteType.Drive, requireContext())
                                            routeRecycler.adapter = routeAdapter
                                            routeRecycler.layoutManager = LinearLayoutManager(requireContext())
                                            routeRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                                        } else {
                                            routeAdapter!!.setData(p1, RouteAdapter.RouteType.Drive)
                                        }
                                        routeDivider.visibility = View.VISIBLE
                                        routeRecycler.visibility = View.VISIBLE
                                    }
                                }
                                override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                    routeDivider.visibility = View.GONE
                                    routeRecycler.visibility = View.GONE
                                }
                            })
                        }
                        R.id.sheet_route_walk_button -> { // Walk
                            val walkingParam = WalkingParam().apply{
                                from(LatLng(currentLocation.latitude, currentLocation.longitude))
                                to(LatLng(SearchActivity.resultList[position].location.lat, SearchActivity.resultList[position].location.lng))
                            }
                            TencentSearch(requireContext()).getRoutePlan(walkingParam, object: HttpResponseListener<WalkingResultObject> {
                                override fun onSuccess(p0: Int, p1: WalkingResultObject?) {
                                    if (p1 != null) {
                                        if (routeAdapter == null) {
                                            routeAdapter = RouteAdapter(p1, RouteAdapter.RouteType.Walk, requireContext())
                                            routeRecycler.adapter = routeAdapter
                                            routeRecycler.layoutManager = LinearLayoutManager(requireContext())
                                            routeRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                                        } else {
                                            routeAdapter!!.setData(p1, RouteAdapter.RouteType.Walk)
                                        }
                                        routeDivider.visibility = View.VISIBLE
                                        routeRecycler.visibility = View.VISIBLE
                                    }
                                }
                                override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                    routeDivider.visibility = View.GONE
                                    routeRecycler.visibility = View.GONE
                                }
                            })
                        }
                        R.id.sheet_route_transit_button -> { // Transit
                            val transitParam = TransitParam().apply{
                                from(LatLng(currentLocation.latitude, currentLocation.longitude))
                                to(LatLng(SearchActivity.resultList[position].location.lat, SearchActivity.resultList[position].location.lng))
                            }
                            TencentSearch(requireContext()).getRoutePlan(transitParam, object: HttpResponseListener<TransitResultObject> {
                                override fun onSuccess(p0: Int, p1: TransitResultObject?) {
                                    if (p1 != null) {
                                        if (routeAdapter == null) {
                                            routeAdapter = RouteAdapter(p1, RouteAdapter.RouteType.Transit, requireContext())
                                            routeRecycler.adapter = routeAdapter
                                            routeRecycler.layoutManager = LinearLayoutManager(requireContext())
                                            routeRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                                        } else {
                                            routeAdapter!!.setData(p1, RouteAdapter.RouteType.Transit)
                                        }
                                        routeDivider.visibility = View.VISIBLE
                                        routeRecycler.visibility = View.VISIBLE
                                    }
                                }
                                override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                    routeDivider.visibility = View.GONE
                                    routeRecycler.visibility = View.GONE
                                }
                            })
                        }
                        R.id.sheet_route_bicycle_button -> { // Bicycle
                            val bicycleParam = BicyclingParam().apply{
                                from(LatLng(currentLocation.latitude, currentLocation.longitude))
                                to(LatLng(SearchActivity.resultList[position].location.lat, SearchActivity.resultList[position].location.lng))
                            }
                            TencentSearch(requireContext()).getRoutePlan(bicycleParam, object: HttpResponseListener<BicyclingResultObject> {
                                override fun onSuccess(p0: Int, p1: BicyclingResultObject?) {
                                    if (p1 != null) {
                                        if (routeAdapter == null) {
                                            routeAdapter = RouteAdapter(p1, RouteAdapter.RouteType.Transit, requireContext())
                                            routeRecycler.adapter = routeAdapter
                                            routeRecycler.layoutManager = LinearLayoutManager(requireContext())
                                            routeRecycler.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                                        } else {
                                            routeAdapter!!.setData(p1, RouteAdapter.RouteType.Bicycle)
                                        }
                                        routeDivider.visibility = View.VISIBLE
                                        routeRecycler.visibility = View.VISIBLE
                                    }
                                }
                                override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                    routeDivider.visibility = View.GONE
                                    routeRecycler.visibility = View.GONE
                                }
                            })
                        }
                    }
                }
            }
        }

        // FAB 事件：出发
        sheetRouteButton.setOnClickListener {

        }
        return root
    }

    private fun findViewById(root: View) {
        mapView = root.findViewById(R.id.map_view)
        searchEdit = root.findViewById(R.id.search_edit)
        sayingButton = root.findViewById(R.id.saying_button)
        hearingButton = root.findViewById(R.id.hearing_button)
        aroundButton = root.findViewById(R.id.around_button)

        sheetLayout = root.findViewById(R.id.search_result_sheet)
        sheetContent = root.findViewById(R.id.search_result_sheet_layout)
        sheetContentBehavior = BottomSheetBehavior.from(sheetContent)
        sheetTitleText = root.findViewById(R.id.sheet_title_text)
        sheetAddressText = root.findViewById(R.id.sheet_address_text)
        sheetRouteButton = root.findViewById(R.id.sheet_route_button)

        sheetRouteDriveButton = root.findViewById(R.id.sheet_route_drive_button)
        sheetRouteWalkButton = root.findViewById(R.id.sheet_route_walk_button)
        sheetRouteTransitButton = root.findViewById(R.id.sheet_route_transit_button)
        sheetRouteBicycleButton = root.findViewById(R.id.sheet_route_bicycle_button)

        routeDivider = root.findViewById(R.id.sheet_route_divider)
        routeRecycler = root.findViewById(R.id.sheet_route_recycler)
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
        tencentMap.animateCamera(CameraUpdateFactory.zoomIn() )
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        searchEdit.clearFocus()
        sheetContentBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (position != -1) {
            searchEdit.setText(SearchActivity.resultList[position].title)
            sheetTitleText.text = SearchActivity.resultList[position].title
            sheetAddressText.text = SearchActivity.resultList[position].address
            sheetLayout.visibility = View.VISIBLE
            // 标注并将地图定位移动至对应坐标
            val targetPosition = LatLng(SearchActivity.resultList[position].location.lat, SearchActivity.resultList[position].location.lng)
            targetMarker = tencentMap.addMarker(MarkerOptions(targetPosition))
            tencentMap.animateCamera(CameraUpdateFactory.newLatLng(targetPosition))
        }
        openingSearch = false
    }

    override fun onPause() {
        super.onPause()
        if (!openingSearch)
            mapView.onPause()
        targetMarker?.remove()
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
                city = tencentLocation.city
                val location = Location(tencentLocation.provider)
                //设置经纬度
                location.latitude = tencentLocation.latitude
                location.longitude = tencentLocation.longitude
                lat = tencentLocation.latitude
                lng = tencentLocation.longitude
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

                currentLocation = tencentLocation
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



