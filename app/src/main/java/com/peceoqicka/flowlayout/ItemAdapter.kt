package com.peceoqicka.flowlayout

import android.content.ClipData
import com.peceoqicka.flowlayout.databinding.ItemMainBinding

/**
 * <pre>
 *      author  :   Acer
 *      e-mail  :   xx@xxx
 *      time    :   2018/7/9
 *      desc    :
 *      version :   1.0
 * </pre>
 */
class ItemAdapter(data: ArrayList<ItemViewModel>) : UniversalAdapter<ItemViewModel, ItemMainBinding>(data) {
    override fun getLayoutId() = R.layout.item_main

    override fun onSetData(binding: ItemMainBinding, data: ItemViewModel) {
        binding.model = data
    }

    fun onAddTag(labelText: String) {
        dataList.add(ItemViewModel().apply {
            label = labelText
        })
        notifyDataSetChanged()
    }

    fun onDeleteTag() {
        if (dataList.size >= 0) {
            dataList.removeAt(dataList.size - 1)
            notifyDataSetChanged()
        }
    }
}