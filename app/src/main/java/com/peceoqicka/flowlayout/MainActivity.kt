package com.peceoqicka.flowlayout

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.peceoqicka.flowlayout.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var additionLabels: Array<String>
    private val random = Random()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = ViewModel().apply {
            bindActivity = this@MainActivity
        }
        loadData()
    }

    private fun loadData() {
        val dataList = arrayListOf<ItemViewModel>()
        resources.getStringArray(R.array.tag_list)
                .mapTo(dataList, { str ->
                    ItemViewModel().apply { label = str }
                })

        binding.model?.adapter = ItemAdapter(dataList)

        additionLabels = resources.getStringArray(R.array.additional_tag_list)
    }

    fun onAddTag() {
        binding.model?.adapter?.onAddTag(additionLabels[random.nextInt(additionLabels.size)])
    }

    fun onDeleteTag() {
        binding.model?.adapter?.onDeleteTag()
    }
}
