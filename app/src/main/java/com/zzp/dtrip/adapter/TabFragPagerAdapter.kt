package com.zzp.dtrip.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zzp.dtrip.R
import com.zzp.dtrip.fragment.NearbyFragment

class TabFragPagerAdapter(private val mFragList: List<Fragment>, private val mTitleList: List<String>, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int = mTitleList.size

    override fun getItem(position: Int): Fragment = mFragList[position]

    override fun getPageTitle(position: Int): CharSequence? = mTitleList[position]

}