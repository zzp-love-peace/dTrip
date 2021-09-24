package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zzp.dtrip.R
import com.zzp.dtrip.util.TtsUtil

class SynthesisActivity : AppCompatActivity() {

    private lateinit var synthesisEdit: EditText
    private lateinit var synthesisButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synthesis)
        findViewById()

        synthesisButton.setOnClickListener {
            if (synthesisEdit.text.toString().isNotEmpty()) {
                if (synthesisEdit.text.toString().contains(Regex("[\u4E00-\u9FA5a-zA-Z0-9]"))) {
                    TtsUtil.playString(synthesisEdit.text.toString().replace("\\p{P}", " "))
                } else {
                    Toast.makeText(this, "请输入您常用的话语哦!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "您还没输入文字", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findViewById() {
        synthesisEdit = findViewById(R.id.synthesis_edit)
        synthesisButton = findViewById(R.id.synthesis_button)
    }
}