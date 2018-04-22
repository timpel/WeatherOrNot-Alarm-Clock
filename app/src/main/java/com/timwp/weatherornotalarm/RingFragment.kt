package com.timwp.weatherornotalarm

import android.support.v4.app.Fragment
import android.view.ViewGroup
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View


class RingFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(
                R.layout.fragment_ring, container, false) as ViewGroup
    }
}
