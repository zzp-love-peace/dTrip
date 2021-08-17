package com.zzp.dtrip.fragment

import android.Manifest
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tencent.lbssearch.TencentSearch
import com.tencent.lbssearch.`object`.param.BicyclingParam
import com.tencent.lbssearch.`object`.param.DrivingParam
import com.tencent.lbssearch.`object`.param.TransitParam
import com.tencent.lbssearch.`object`.param.WalkingParam
import com.tencent.lbssearch.`object`.result.*
import com.tencent.map.geolocation.*
import com.tencent.map.tools.json.JsonComposer
import com.tencent.map.tools.net.http.HttpResponseListener
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.LocationSource.OnLocationChangedListener
import com.tencent.tencentmap.mapsdk.maps.model.*
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.NearbyActivity
import com.zzp.dtrip.activity.SearchActivity
import com.zzp.dtrip.activity.SocialActivity
import com.zzp.dtrip.activity.SoundActivity
import com.zzp.dtrip.util.MetricUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class TripFragment : Fragment(), TencentLocationListener, LocationSource {

    companion object {
        var city = ""
        var position = -1
        var lat = 0.0
        var lng = 0.0
        var address = ""
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
    private lateinit var sheetCloseButton: ImageButton
    private lateinit var sheetContent: LinearLayout
    private lateinit var sheetContentBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var sheetTitleText: TextView
    private lateinit var sheetAddressText: TextView
    // FAB 及动画
    private lateinit var sheetRouteButton: FloatingActionButton
    private lateinit var sheetRouteButtonShowAnim: Animation
    private lateinit var sheetRouteButtonHideAnim: Animation
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
    // 路线存储
    enum class RouteType {Drive, Walk, Transit, Bicycle}
    private data class RouteResult (val resultObject: RoutePlanningObject, val type: RouteType)
    data class Route (val route: JsonComposer, val type: RouteType)
    private var routeResult: RouteResult? = null
    private var currentRoute: Route? = null
    private var currentRouteSelected = -1
    private var currentPolylinePointSet: List<LatLng>? = null
    private var currentPolyline: Polyline? = null
    // 导航（粗犷版）
    private lateinit var sheetNavigationDirectionText: TextView
    private var currentInNavigation = false
    private var currentTotalDistance = 0
    private var navigationLocation = ""

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
        aroundButton.setOnClickListener {
            val intent = Intent(requireContext(), NearbyActivity::class.java)
            startActivity(intent)
        }

        // 选择交通方式
        sheetCloseButton.setOnClickListener {
            resetPolylineAndRouteSelection()
            sheetContentBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            if(::currentLocation.isInitialized)
                tencentMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(currentLocation.latitude, currentLocation.longitude)))
            currentInNavigation = false
            sheetNavigationDirectionText.visibility = View.GONE
        }
        arrayOf(sheetRouteDriveButton, sheetRouteWalkButton, sheetRouteTransitButton, sheetRouteBicycleButton).let { array ->
            array.forEach { button ->
                button.setOnClickListener { _ ->
                    if (::currentLocation.isInitialized) {
                        // reset selected state for routes
                        resetPolylineAndRouteSelection()
                        // change background for buttons
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
                                            routeResult = RouteResult(p1, RouteType.Drive)
                                            notifyRecyclerUpdate()
                                        }
                                    }
                                    override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                        Toast.makeText(requireContext(), "Error: $p1", Toast.LENGTH_SHORT).show()
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
                                            routeResult = RouteResult(p1, RouteType.Walk)
                                            notifyRecyclerUpdate()
                                        }
                                    }
                                    override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                        Toast.makeText(requireContext(), "Error: $p1", Toast.LENGTH_SHORT).show()
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
                                            routeResult = RouteResult(p1, RouteType.Transit)
                                            notifyRecyclerUpdate()
                                        }
                                    }
                                    override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                        Toast.makeText(requireContext(), "Error: $p1", Toast.LENGTH_SHORT).show()
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
                                            routeResult = RouteResult(p1, RouteType.Bicycle)
                                            notifyRecyclerUpdate()
                                        }
                                    }
                                    override fun onFailure(p0: Int, p1: String?, p2: Throwable?) {
                                        Toast.makeText(requireContext(), "Error: $p1", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), R.string.toast_wait_for_location, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // FAB 事件：出发
        sheetRouteButton.setOnClickListener {
            resetPolylineAndRouteSelection()
            // 绘制线
            currentPolyline = tencentMap.addPolyline(PolylineOptions().apply {
                addAll(currentPolylinePointSet)
                lineCap(true)
                color(ResourcesCompat.getColor(resources, R.color.blue, requireActivity().theme))
                width(20F)
                eraseColor(ResourcesCompat.getColor(resources, R.color.gray, requireActivity().theme))
            })
            currentPolyline!!.setEraseable(true)
            // 初始化数据
            currentInNavigation = true
            navigationLocation = currentLocation.address
            // UI 动画
            routeRecycler.visibility = View.GONE
            sheetNavigationDirectionText.visibility = View.VISIBLE
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
        sheetCloseButton = root.findViewById(R.id.sheet_close_button)
        sheetContent = root.findViewById(R.id.search_result_sheet_layout)
        sheetContentBehavior = BottomSheetBehavior.from(sheetContent)
        sheetTitleText = root.findViewById(R.id.sheet_title_text)
        sheetAddressText = root.findViewById(R.id.sheet_address_text)
        sheetRouteButtonShowAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.sheet_fab_show)
        sheetRouteButtonHideAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.sheet_fab_hide)
        sheetRouteButton = root.findViewById(R.id.sheet_route_button)
        // 自动隐藏、显示
        sheetRouteButtonShowAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                sheetRouteButton.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })
        sheetRouteButtonHideAnim.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                sheetRouteButton.visibility = View.GONE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        sheetRouteDriveButton = root.findViewById(R.id.sheet_route_drive_button)
        sheetRouteWalkButton = root.findViewById(R.id.sheet_route_walk_button)
        sheetRouteTransitButton = root.findViewById(R.id.sheet_route_transit_button)
        sheetRouteBicycleButton = root.findViewById(R.id.sheet_route_bicycle_button)

        routeDivider = root.findViewById(R.id.sheet_route_divider)
        routeRecycler = root.findViewById(R.id.sheet_route_recycler)

        sheetNavigationDirectionText = root.findViewById(R.id.sheet_navi_direction)
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

        // 自动隐藏已有路线
        arrayOf(sheetRouteDriveButton, sheetRouteWalkButton, sheetRouteTransitButton, sheetRouteBicycleButton).forEach {
            it.background = ResourcesCompat.getDrawable(resources, android.R.color.transparent, requireActivity().theme)
        }
        resetPolylineAndRouteSelection()
        routeRecycler.visibility = View.GONE
        routeDivider.visibility = View.GONE
        currentInNavigation = false
        sheetNavigationDirectionText.visibility = View.GONE
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
                address = tencentLocation.address
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
                // 导航
                if (currentInNavigation) {
                    currentTotalDistance += TencentLocationUtils.distanceBetween(currentLocation, tencentLocation).toInt()
                    tencentMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(tencentLocation.latitude, tencentLocation.longitude)))
                    // 擦除线
                    currentPolyline?.eraseTo(1, LatLng(tencentLocation.latitude, tencentLocation.longitude))
                } else {
                    if (currentTotalDistance != 0) {
                        navigationLocation += ",${tencentLocation.name}"
                        // TODO: 上传数据
                        Log.d(TAG, "导航结束，$navigationLocation, $currentTotalDistance")
                        currentTotalDistance = 0
                        resetPolylineAndRouteSelection()
                    }
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

    private fun notifyRecyclerUpdate() {
        if (routeResult != null) {
            if (routeAdapter == null) {
                routeAdapter = RouteAdapter(routeResult!!.resultObject, routeResult!!.type, requireContext())
                routeRecycler.apply {
                    adapter = routeAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                }
            } else {
                routeAdapter!!.setData(routeResult!!.resultObject, routeResult!!.type)
            }
            routeAdapter?.notifyDataSetChanged()
            if (routeRecycler.visibility != View.VISIBLE) {
                routeDivider.visibility = View.VISIBLE
                val slideUpTransition = Slide(Gravity.BOTTOM).apply {
                    duration = 200L
                    addTarget(routeRecycler)
                }
                TransitionManager.beginDelayedTransition(sheetLayout, slideUpTransition)
                routeRecycler.visibility = View.VISIBLE
            }
        } else {
            routeDivider.visibility = View.GONE
            routeRecycler.visibility = View.GONE
        }
    }

    private fun resetPolylineAndRouteSelection(){
        if (sheetRouteButton.visibility == View.VISIBLE)
            sheetRouteButton.startAnimation(sheetRouteButtonHideAnim)
        currentRoute = null
        currentRouteSelected = -1
        currentPolyline?.remove()
        currentPolyline = null
        routeAdapter?.notifyDataSetChanged()
    }

    inner class RouteAdapter(private var result: RoutePlanningObject, private var type: RouteType, private val context: Context): RecyclerView.Adapter<RouteAdapter.RouteHolder>() {

        inner class RouteHolder(item: View): RecyclerView.ViewHolder(item) {
            val layout = item.findViewById<ConstraintLayout>(R.id.route_layout)
            val layoutBackgroundTransition = layout.background as TransitionDrawable
            var transitionFinished = false
            val durationText = item.findViewById<TextView>(R.id.route_duration)
            val distanceText = item.findViewById<TextView>(R.id.route_distance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder = RouteHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false))

        override fun onBindViewHolder(holder: RouteHolder, position: Int) {
            when (type) {
                RouteType.Drive ->
                    (result as DrivingResultObject).result.routes.let {
                        holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                        holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                    }
                RouteType.Walk ->
                    (result as WalkingResultObject).result.routes.let {
                        holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                        holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                    }
                RouteType.Transit ->
                    (result as TransitResultObject).result.routes.let {
                        val partsToBeAppended = mutableListOf<String>()
                        it[position].steps.forEach { step ->
                            when (step.mode) {
                                "WALKING" ->
                                    partsToBeAppended.add(getString(R.string.distance_transit_walk, (step as TransitResultObject.Walking).duration.toInt()))
                                "TRANSIT" ->
                                    (step as TransitResultObject.Transit).lines.forEach { line ->
                                        when (line.vehicle) {
                                            "RAIL" ->
                                                partsToBeAppended.add(getString(R.string.distance_transit_rail, line.title))
                                            "BUS" ->
                                                partsToBeAppended.add(getString(R.string.distance_transit_bus, line.title))
                                            "SUBWAY" ->
                                                partsToBeAppended.add(getString(R.string.distance_transit_subway, line.title))
                                        }
                                    }
                            }
                        }
                        holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration, context)
                        holder.distanceText.text = partsToBeAppended.joinToString(" > ")
                    }
                RouteType.Bicycle ->
                    (result as BicyclingResultObject).result.routes.let {
                        holder.durationText.text = MetricUtil.convertMinutesToTimeString(it[position].duration.toLong(), context)
                        holder.distanceText.text = MetricUtil.convertMetersToDistanceString(it[position].distance, context)
                    }
            }
            holder.layout.setOnClickListener {
                if (currentRouteSelected != holder.adapterPosition) {
                    // 更改选择状态
                    val previousPosition = currentRouteSelected
                    currentRouteSelected = holder.adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(holder.adapterPosition)
                    // 移除上次选择
                    if (currentPolyline != null)
                        currentPolyline!!.remove()
                    // 点击后收起 bottom sheet
                    if (sheetContentBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                        sheetContentBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    // 根据类型不同，应用、合并不同线路
                    currentRoute = Route(when (type) {
                        RouteType.Drive ->
                            (result as DrivingResultObject).result.routes[holder.adapterPosition].also {
                                currentPolylinePointSet = it.polyline
                            }
                        RouteType.Walk ->
                            (result as WalkingResultObject).result.routes[holder.adapterPosition].also {
                                currentPolylinePointSet = it.polyline
                            }
                        RouteType.Transit ->
                            (result as TransitResultObject).result.routes[holder.adapterPosition].also {
                                val resultPointSet = mutableListOf<LatLng>()
                                it.steps.forEach { step ->
                                    when(step.mode) {
                                        "WALKING" -> {
                                            resultPointSet.addAll((step as TransitResultObject.Walking).polyline)
                                        }
                                        "TRANSIT" -> {
                                            (step as TransitResultObject.Transit).lines.forEach { line ->
                                                resultPointSet.addAll(line.polyline)
                                            }
                                        }
                                    }
                                }
                                currentPolylinePointSet = resultPointSet
                            }
                        RouteType.Bicycle ->
                            (result as BicyclingResultObject).result.routes[holder.adapterPosition].also {
                                currentPolylinePointSet = it.polyline
                            }
                    }, type)
                    // 绘制折线并移动相机
                    currentPolyline = tencentMap.addPolyline(PolylineOptions().apply {
                        addAll(currentPolylinePointSet)
                        lineCap(true)
                        color(ResourcesCompat.getColor(resources, R.color.purple_500, requireActivity().theme))
                        width(20F)
                    })
                    tencentMap.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.builder().include(currentPolyline!!.points).build(), 100))
                    // 显示 FAB
                    if (sheetRouteButton.visibility == View.GONE)
                        sheetRouteButton.startAnimation(sheetRouteButtonShowAnim)
                    // 更新导航信息
                    sheetNavigationDirectionText.text = if (currentRoute!!.type == RouteType.Transit) holder.distanceText.text else getString(R.string.hint_navigation_ongoing)
                }
            }
            if (currentRouteSelected == position && !holder.transitionFinished) {
                holder.layoutBackgroundTransition.startTransition(100)
                holder.transitionFinished = true
            } else if (currentRouteSelected != position && holder.transitionFinished) {
                holder.layoutBackgroundTransition.reverseTransition(100)
                holder.transitionFinished = false
            }
        }

        override fun getItemCount(): Int = when (type) {
            RouteType.Drive ->
                (result as DrivingResultObject).result.routes.size
            RouteType.Walk ->
                (result as WalkingResultObject).result.routes.size
            RouteType.Transit ->
                (result as TransitResultObject).result.routes.size
            RouteType.Bicycle ->
                (result as BicyclingResultObject).result.routes.size
        }

        fun setData (result: RoutePlanningObject, type: RouteType) {
            this@RouteAdapter.result = result
            this@RouteAdapter.type = type
        }
    }
}



