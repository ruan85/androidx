/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.camera.integration.antelope.cameracontrollers

import android.hardware.camera2.CameraDevice
import androidx.camera.integration.antelope.CameraParams
import androidx.camera.integration.antelope.MainActivity
import androidx.camera.integration.antelope.TestConfig
import androidx.camera.integration.antelope.TestType
import androidx.camera.integration.antelope.testEnded

/** Callbacks that track the state of the camera device using the Camera 2 API. */
class Camera2DeviceStateCallback(
    internal var params: CameraParams,
    internal var activity: MainActivity,
    internal var testConfig: TestConfig
) : CameraDevice.StateCallback() {

    /** Camera device has opened successfully, record timing and initiate the preview stream. */
    override fun onOpened(cameraDevice: CameraDevice) {
        params.timer.openEnd = System.currentTimeMillis()
        MainActivity.logd(
            "In CameraStateCallback onOpened: " +
                cameraDevice.id +
                " current test: " +
                testConfig.currentRunningTest.toString()
        )
        params.isOpen = true
        params.device = cameraDevice

        when (testConfig.currentRunningTest) {
            TestType.INIT -> {
                // Camera opened, we're done
                testConfig.testFinished = true
                closePreviewAndCamera(activity, params, testConfig)
            }
            else -> {
                params.timer.previewStart = System.currentTimeMillis()
                createCameraPreviewSession(activity, params, testConfig)
            }
        }
    }

    /**
     * Camera device has been closed, recording close timing.
     *
     * If this is a switch test, swizzle camera ids and move to the next step of the test.
     */
    override fun onClosed(camera: CameraDevice) {
        MainActivity.logd(
            "In CameraStateCallback onClosed. Camera: " +
                params.id +
                " is closed. testFinished: " +
                testConfig.testFinished
        )
        params.isOpen = false

        if (testConfig.testFinished) {
            params.timer.cameraCloseEnd = System.currentTimeMillis()
            testConfig.testFinished = false
            testEnded(activity, params, testConfig)
            return
        }

        if (
            (testConfig.currentRunningTest == TestType.SWITCH_CAMERA) ||
                (testConfig.currentRunningTest == TestType.MULTI_SWITCH)
        ) {

            // First camera closed, now start the second
            if (testConfig.switchTestCurrentCamera == testConfig.switchTestCameras.get(0)) {
                testConfig.switchTestCurrentCamera = testConfig.switchTestCameras.get(1)
                camera2OpenCamera(activity, params, testConfig)
            }

            // Second camera closed, now start the first
            else if (testConfig.switchTestCurrentCamera == testConfig.switchTestCameras.get(1)) {
                testConfig.switchTestCurrentCamera = testConfig.switchTestCameras.get(0)
                testConfig.testFinished = true
                camera2OpenCamera(activity, params, testConfig)
            }
        }

        super.onClosed(camera)
    }

    /** Camera has been disconnected. Whatever was happening, it won't work now. */
    override fun onDisconnected(cameraDevice: CameraDevice) {
        MainActivity.logd("In CameraStateCallback onDisconnected: " + params.id)
        if (!params.isOpen) {
            return
        }

        testConfig.testFinished = false // Whatever we are doing will fail now, try to exit
        closePreviewAndCamera(activity, params, testConfig)
    }

    /** Camera device has thrown an error. Try to recover or fail gracefully. */
    override fun onError(cameraDevice: CameraDevice, error: Int) {
        MainActivity.logd("In CameraStateCallback onError: " + cameraDevice.id + " error: " + error)
        if (!params.isOpen) {
            return
        }

        when (error) {
            CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE -> {
                // Let's try to close an open camera and re-open this one
                MainActivity.logd("In CameraStateCallback too many cameras open, closing one...")
                closeACamera(activity, testConfig)
                camera2OpenCamera(activity, params, testConfig)
            }
            CameraDevice.StateCallback.ERROR_CAMERA_DEVICE -> {
                MainActivity.logd("Fatal camera error, close and try to re-initialize...")
                closePreviewAndCamera(activity, params, testConfig)
                camera2OpenCamera(activity, params, testConfig)
            }
            CameraDevice.StateCallback.ERROR_CAMERA_IN_USE -> {
                MainActivity.logd("This camera is already open... doing nothing")
            }
            else -> {
                testConfig.testFinished = false // Whatever we are doing will fail, just try to exit
                closePreviewAndCamera(activity, params, testConfig)
            }
        }
    }
}
