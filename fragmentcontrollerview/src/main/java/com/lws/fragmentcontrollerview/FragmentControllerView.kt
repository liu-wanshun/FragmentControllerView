package com.lws.fragmentcontrollerview

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentController
import androidx.fragment.app.FragmentHostCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

open class FragmentControllerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), LifecycleOwner, ViewModelStoreOwner, ComponentCallbacks {

    private val mFragments = FragmentController.createController(HostCallbacks())

    private val mViewModelStore = ViewModelStore()

    private val mFragmentLifecycleRegistry = LifecycleRegistry(this)

    private var mCreated = false
    private var mResumed = false
    private var mStopped = true

    init {
        mFragments.attachHost(null /*parent*/)
    }

    override fun getViewModelStore(): ViewModelStore {
        return mViewModelStore
    }

    override fun getLifecycle(): Lifecycle {
        return mFragmentLifecycleRegistry
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mFragments.dispatchConfigurationChanged(newConfig)
    }

    @JvmOverloads
    @CallSuper
    open fun onCreate(savedInstanceState: Bundle? = null) {
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        mFragments.dispatchCreate()
    }

    @CallSuper
    open fun onDestroy() {
        mFragments.dispatchDestroy()
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    @CallSuper
    override fun onLowMemory() {
        mFragments.dispatchLowMemory()
    }

    @CallSuper
    open fun onPause() {
        mResumed = false
        mFragments.dispatchPause()
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }


    @CallSuper
    open fun onResume() {
        mResumed = true
        mFragments.execPendingActions()
    }

    @CallSuper
    open fun onPostResume() {
        onResumeFragments()
    }

    protected fun onResumeFragments() {
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        mFragments.dispatchResume()
    }

    @CallSuper
    open fun onStart() {
        mStopped = false
        if (!mCreated) {
            mCreated = true
            mFragments.dispatchActivityCreated()
        }
        mFragments.execPendingActions()

        // NOTE: HC onStart goes here.
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        mFragments.dispatchStart()
    }

    @CallSuper
    open fun onStop() {
        mStopped = true
        mFragments.dispatchStop()
        mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    open fun onAttachFragment(fragment: Fragment) {

    }

    fun getSupportFragmentManager(): FragmentManager {
        return mFragments.supportFragmentManager
    }


    inner class HostCallbacks :
        FragmentHostCallback<FragmentControllerView>(context, Handler(Looper.getMainLooper()), 0),
        ViewModelStoreOwner,
        FragmentOnAttachListener {
        override fun onGetHost(): FragmentControllerView {
            return this@FragmentControllerView
        }

        override fun onGetLayoutInflater(): LayoutInflater {
            return super.onGetLayoutInflater().cloneInContext(context)
        }

        override fun getViewModelStore(): ViewModelStore {
            return this@FragmentControllerView.viewModelStore
        }

        override fun onAttachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
            onAttachFragment(fragment)
        }

        override fun onFindViewById(id: Int): View? {
            return this@FragmentControllerView.findViewById(id)
        }
    }


}