package com.zzp.dtrip.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.InformationActivity

class MineFragment : Fragment() {

    private lateinit var informationLayout: LinearLayout
    private lateinit var faceLayout: LinearLayout
    private lateinit var tripLayout: LinearLayout

    private lateinit var switchMaterial: SwitchMaterial

    private lateinit var controlButton: MaterialButton

    private lateinit var prefs: SharedPreferences

    private val TAG = "MineFragment"

    companion object {
        var switchFlag = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_mine, container, false)
        findViewById(root)
        initPrefAndSwitch()

        informationLayout.setOnClickListener {
            val intent = Intent(requireContext(), InformationActivity::class.java)
            startActivity(intent)
        }

        faceLayout.setOnClickListener {  }

        tripLayout.setOnClickListener {  }

        controlButton.setOnClickListener {  }

        switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            switchFlag = isChecked
            saveSwitchFlag()
        }

        return root
    }

    private fun findViewById(root: View) {
        informationLayout = root.findViewById(R.id.information_layout)
        faceLayout = root.findViewById(R.id.face_layout)
        tripLayout= root.findViewById(R.id.trip_layout)
        switchMaterial = root.findViewById(R.id.switch_material)
        controlButton = root.findViewById(R.id.control_button)
    }

    private fun initPrefAndSwitch() {
        prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        switchFlag = prefs.getBoolean("switch", false)
        switchMaterial.isChecked = switchFlag
        Log.d(TAG, "initPrefAndSwitch: ")
    }

    private fun saveSwitchFlag() {
        val edit = prefs.edit()
        edit.putBoolean("switch", switchFlag)
        edit.apply()
    }
}