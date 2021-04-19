package com.beyondinc.commandcenter.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.beyondinc.commandcenter.R
import com.beyondinc.commandcenter.databinding.FragmentCheckBinding
import com.beyondinc.commandcenter.viewmodel.CheckViewModel
import com.beyondinc.commandcenter.viewmodel.MainsViewModel

class CheckFragment : Fragment() {

    private var binding: FragmentCheckBinding? = null
    private var viewModel: CheckViewModel? = null

    companion object {
        var fr: Fragment? = null
        var checklistfrag: Fragment? = null
        var fragmentTransaction: FragmentTransaction? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)
        viewModel = CheckViewModel()
        binding!!.viewModel = viewModel
        binding!!.lifecycleOwner = requireActivity()

        fragmentTransaction = childFragmentManager.beginTransaction()
        checklistfrag = CheckListFragment()
        fr = checklistfrag
        fragmentTransaction!!.add(R.id.cfL02, fr!!)
        fragmentTransaction!!.commitAllowingStateLoss()
    }
}