/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tv.reference.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.android.tv.reference.databinding.FragmentNoFirebaseBinding
import com.android.tv.reference.servicediscovery.ServiceDiscovery

/**
 * Simple Fragment that displays some info about configuring Firebase and has a continue button
 */
class NoFirebaseFragment : Fragment() {
    private lateinit var serviceDiscovery: ServiceDiscovery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceDiscovery = ServiceDiscovery(requireContext())
    }

    private fun isServerUrlSet (): Boolean {
        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        return preferenceManager.getString("server_url", null) != null
    }

    override fun onPause() {
        serviceDiscovery.tearDown()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (!isServerUrlSet()) {
            serviceDiscovery.startDiscovery()
        } else {
            findNavController()
                .navigate(NoFirebaseFragmentDirections.actionNoFirebaseFragmentToBrowseFragment())
        }
    }

    override fun onDestroy() {
        serviceDiscovery.tearDown()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNoFirebaseBinding.inflate(inflater, container, false)
        binding.continueButton.setOnClickListener {
            findNavController()
                .navigate(NoFirebaseFragmentDirections.actionNoFirebaseFragmentToSettingsFragment())
        }
        return binding.root
    }
}
