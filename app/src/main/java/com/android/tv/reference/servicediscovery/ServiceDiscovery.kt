package com.android.tv.reference.servicediscovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import timber.log.Timber
import java.net.InetAddress

class ServiceDiscovery (context: Context) {
    private val serviceName = "couchbuddy"
    private val serviceType = "_http._tcp."
    private val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var discoveredService: NsdServiceInfo? = null

    fun startDiscovery () {
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun tearDown() {
        try {
            nsdManager.apply {
                stopServiceDiscovery(discoveryListener)
            }
        } catch (e: Exception) {}
    }

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Timber.e("Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Timber.e("Resolve Succeeded. $serviceInfo")

//            if (serviceInfo.serviceName == serviceName) {
//                Timber.d("Same IP.")
//                return
//            }
            discoveredService = serviceInfo

            Timber.e("Found ${serviceInfo.host.address}, ${serviceInfo.host.hostAddress}, ${serviceInfo.host.hostName}")

            val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
            preferenceManager.edit {
                this.putString("server_url", "http:/${serviceInfo.host}:${serviceInfo.port}")
            }
        }
    }

    // Instantiate a new DiscoveryListener
    private val discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Timber.d("Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Timber.d("Service discovery success -> $service")
            when {
                service.serviceType != serviceType -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Timber.d("Unknown Service Type: ${service.serviceType}")
//                service.serviceName == serviceName -> // The name of the service tells the user what they'd be
//                    // connecting to. It could be "Bob's Chat App".
//                    Timber.d("Same machine: $serviceName")
                service.serviceName.contains(serviceName) -> nsdManager.resolveService(service, resolveListener)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Timber.e("service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Timber.i("Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.e("Discovery failed: Error code:$errorCode")
//            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.e("Discovery failed: Error code:$errorCode")
            nsdManager.stopServiceDiscovery(this)
        }
    }
}

