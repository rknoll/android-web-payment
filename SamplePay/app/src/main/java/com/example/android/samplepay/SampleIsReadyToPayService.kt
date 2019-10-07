/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.samplepay

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.android.samplepay.model.IsReadyToPayParams
import org.chromium.IsReadyToPayService
import org.chromium.IsReadyToPayServiceCallback

private const val TAG = "IsReadyToPayService"

class SampleIsReadyToPayService : Service() {

    private val acceptingBinder = object : IsReadyToPayService.Stub() {
        override fun isReadyToPay(callback: IsReadyToPayServiceCallback?) {
            try {
                Log.d(TAG, "IsReadyToPay: true")
                callback?.handleIsReadyToPay(true)
            } catch (e: RemoteException) {
                // Ignore
            }
        }
    }

    private val rejectingBinder = object : IsReadyToPayService.Stub() {
        override fun isReadyToPay(callback: IsReadyToPayServiceCallback?) {
            try {
                Log.d(TAG, "IsReadyToPay: false")
                callback?.handleIsReadyToPay(false)
            } catch (e: RemoteException) {
                // Ignore
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        val extras = intent?.extras ?: return rejectingBinder
        val params = IsReadyToPayParams.from(extras)
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "$params")
        }
        return if (checkParameters(params)) {
            acceptingBinder
        } else {
            rejectingBinder
        }
    }

    private fun checkParameters(params: IsReadyToPayParams): Boolean {
        return params.methodNames.size == 1 &&
                params.methodNames[0] == "https://sample-pay-e6bb3.firebaseapp.com"
    }
}
