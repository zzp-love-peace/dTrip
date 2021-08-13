package com.zzp.dtrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.zzp.dtrip.R

class MineFragment : Fragment() {

    private lateinit var informationLayout: LinearLayout
    private lateinit var faceLayout: LinearLayout
    private lateinit var tripLayout: LinearLayout
    private lateinit var settingLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_mine, container, false)
        findViewById(root)
        informationLayout.setOnClickListener {  }

        faceLayout.setOnClickListener {  }

        tripLayout.setOnClickListener {  }

        settingLayout.setOnClickListener {  }
        return root
    }

    private fun findViewById(root: View) {
        informationLayout = root.findViewById(R.id.information_layout)
        faceLayout = root.findViewById(R.id.face_layout)
        tripLayout= root.findViewById(R.id.trip_layout)
        settingLayout = root.findViewById(R.id.setting_layout)
    }
}