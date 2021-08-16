package com.zzp.dtrip.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.NearbyActivity
import com.zzp.dtrip.activity.SearchActivity
import com.zzp.dtrip.adapter.AddressAdapter
import com.zzp.dtrip.data.BigData
import com.zzp.dtrip.data.NearbyResult
import com.zzp.dtrip.util.TencentAppService
import com.zzp.dtrip.util.TencentRetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyFragment(private val keyword: String) : Fragment() {

    val nearbyAddressList = ArrayList<BigData>()

    private lateinit var recyclerView: RecyclerView

    private var adapter: AddressAdapter? = null

    private val KEY = "F5IBZ-US3CW-3JIRY-OKBB5-TUMWV-S7BVZ"

    private val TAG = "NearbyFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_nearby, container, false)
        initRecyclerView(view)
        getNearby(keyword)
        return view
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_nearby)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = AddressAdapter(activity as NearbyActivity, nearbyAddressList)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(activity as NearbyActivity, DividerItemDecoration.VERTICAL))
    }

    private fun getNearby(keyword: String) {
        val appService = TencentRetrofitManager.create<TencentAppService>()
        val boundary = "nearby(${TripFragment.lat},${TripFragment.lng},1000)"
        val task = appService.getNearby(keyword, boundary, KEY)
        task.enqueue(object : Callback<NearbyResult> {
            override fun onResponse(call: Call<NearbyResult>, response: Response<NearbyResult>) {
                response.body()?.apply {
                    if (this.status == 0) {
                        if (nearbyAddressList.size != 0) {
                            nearbyAddressList.clear()
                        }
                        for (obj in this.data) {
                            val objs = BigData(obj)
                            nearbyAddressList.add(objs)
                        }
                        if (keyword == "美食") {
                            if (SearchActivity.resultList.isNotEmpty()) {
                                SearchActivity.resultList.clear()
                            }
                            for (obj in nearbyAddressList) {
                                SearchActivity.resultList.add(obj)
                            }
                        }
                        adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            context, "请求错误",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<NearbyResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
            }

        })
    }

}