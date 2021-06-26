package com.thenativecitizens.onlinewallpapereditor.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.thenativecitizens.onlinewallpapereditor.R
import com.thenativecitizens.onlinewallpapereditor.databinding.FragmentSplashBinding
import com.thenativecitizens.onlinewallpapereditor.util.DelayUtil


class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private lateinit var delayUtil: DelayUtil

    override fun onStart() {
        super.onStart()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)

        //delay
        delayUtil = ViewModelProvider(this).get(DelayUtil::class.java)

        //begin the delay
        delayUtil.beginDelay(5_000L) //delays the splash for 5 seconds


        //Observation to know when the splash delay is done
        delayUtil.isDelayFinished.observe(viewLifecycleOwner){isDone ->
            if (isDone)
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        }

        return binding.root
    }

    //Remove the Fullscreen flags on destroy
    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

}