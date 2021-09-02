package com.zzp.dtrip.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.TabFragPagerAdapter
import com.zzp.dtrip.data.CurrentAddressResult
import com.zzp.dtrip.fragment.NearbyFragment
import com.zzp.dtrip.fragment.TripFragment
import com.zzp.dtrip.util.TencentAppService
import com.zzp.dtrip.util.TencentRetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyActivity : AppCompatActivity() {

    private lateinit var nearbyFragment1: NearbyFragment
    private lateinit var nearbyFragment2: NearbyFragment
    private lateinit var nearbyFragment3: NearbyFragment
    private lateinit var nearbyFragment4: NearbyFragment

    private val fragments = ArrayList<Fragment>()

    private val titles = ArrayList<String>().apply {
        add("美食")
        add("酒店")
        add("超市")
        add("景点")
    }

    private val imageViews = ArrayList<Int>().apply {
        add(R.drawable.ic_vector_food)
        add(R.drawable.ic_vector_hotel)
        add(R.drawable.ic_vector_shopping)
        add(R.drawable.ic_vector_scape)
    }

    // 目前位置
    private var currentLocation: String = ""

    private lateinit var currentAddress: TextView

    private lateinit var tabLayout: TabLayout

    private lateinit var viewPager: ViewPager

    private val TAG = "NearbyActivity"
    private val KEY = "F5IBZ-US3CW-3JIRY-OKBB5-TUMWV-S7BVZ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        findViewById()
        initFragment()
        initTabFragAdapter()

        getCurrentAddress()
    }

    private fun initFragment() {
        nearbyFragment1 = NearbyFragment(titles[0])
        nearbyFragment2 = NearbyFragment(titles[1])
        nearbyFragment3 = NearbyFragment(titles[2])
        nearbyFragment4 = NearbyFragment(titles[3])
        fragments.add(nearbyFragment1)
        fragments.add(nearbyFragment2)
        fragments.add(nearbyFragment3)
        fragments.add(nearbyFragment4)
    }

    private fun findViewById() {
        currentAddress = findViewById(R.id.current_address)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.viewpager)
    }

    @SuppressLint("ResourceAsColor")
    private fun initTabFragAdapter() {

        val adapter = TabFragPagerAdapter(fragments, titles, supportFragmentManager)
        //给ViewPager设置adapter
        viewPager.adapter = adapter
        //预加载管理,除去当前显示页面以外需要被预加载的页面数。
        viewPager.offscreenPageLimit = titles.size
        //设置viewPager与TabLayout联动
        tabLayout.setupWithViewPager(viewPager)

        for (i in 0 until titles.size) {
            val tab = tabLayout.getTabAt(i)
            tab?.customView = getTabView(i)
            if (0 == i) {
                val tabTitle: TextView? = tab?.customView?.findViewById(R.id.nearby_tab_title)
                tabTitle?.setTextColor(ContextCompat.getColor(this@NearbyActivity, R.color.blue))
                val tabImage: ImageView? = tab?.customView?.findViewById(R.id.nearby_tab_image)
                tabImage?.setColorFilter(ContextCompat.getColor(this@NearbyActivity, R.color.blue))
            }
        }

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabLayout.isEnabled = false
                //选择时触发
                tab?.let {
                    val tabTitle: TextView? = it.customView?.findViewById(R.id.nearby_tab_title)
                    tabTitle?.setTextColor(ContextCompat.getColor(this@NearbyActivity, R.color.blue))
                    val tabImage: ImageView? = it.customView?.findViewById(R.id.nearby_tab_image)
                    tabImage?.setColorFilter(ContextCompat.getColor(this@NearbyActivity, R.color.blue))

                    if (SearchActivity.resultList.isNotEmpty()) {
                        SearchActivity.resultList.clear()
                    }
                    val list = (fragments[it.position] as NearbyFragment)
                    for (data in list.nearbyAddressList) {
                        SearchActivity.resultList.add(data)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //未选择时触发
                tab?.let {
                    val tabTitle: TextView? = it.customView?.findViewById(R.id.nearby_tab_title)
                    tabTitle?.setTextColor(ContextCompat.getColor(this@NearbyActivity, R.color.black))
                    val tabImage: ImageView? = it.customView?.findViewById(R.id.nearby_tab_image)
                    tabImage?.setColorFilter(ContextCompat.getColor(this@NearbyActivity, R.color.black))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab.let {
                    //选中之后再次点击即复选时触发
                }
            }
        })
    }

    private fun getTabView(position: Int): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
        val title: TextView = view.findViewById(R.id.nearby_tab_title)
        title.text = titles[position]
        val image: ImageView = view.findViewById(R.id.nearby_tab_image)
        image.setImageResource(imageViews[position])
        return view
    }

    //得到地址精确值,并提供给currentAddress这个TextView控件显示于界面
    private fun getCurrentAddress() {
        val appService = TencentRetrofitManager.create<TencentAppService>()
        val location = "${TripFragment.lat},${TripFragment.lng}"
        val task = appService.getCurrentAddress(location, KEY)
        task.enqueue(object : Callback<CurrentAddressResult> {
            override fun onResponse(
                call: Call<CurrentAddressResult>,
                response: Response<CurrentAddressResult>
            ) {
                response.body()?.apply {
                    if (this.status == 0 && (TripFragment.lat != 0.0 || TripFragment.lng != 0.0)) {
                        currentLocation = this.result.formatted_addresses.recommend
                        currentAddress.text = currentLocation
                    } else {
                        Toast.makeText(
                            this@NearbyActivity, "请求错误",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<CurrentAddressResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(
                    this@NearbyActivity, "$t",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

}