package com.timwp.weatherornotalarm

import android.app.ListActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.app.Activity
import android.content.Intent
import android.widget.TextView


class ConditionPicker : ListActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition_picker)
        val conditionOptions = arrayOf("Rain", "Snow", "Sunny", "Cloudy", "Any")
        //val conditionList: ListView = this.findViewById(R.id.conditionList)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, conditionOptions)
        listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, pos: Int, id: Long) {
        super.onListItemClick(l, v, pos, id)
        val returnIntent = Intent()
        returnIntent.putExtra("PICKED_CONDITION", (v as TextView).text)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}