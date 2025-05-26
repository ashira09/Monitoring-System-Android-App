package com.example.monitoringsystem

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import no.nordicsemi.android.ble.observer.ConnectionObserver
import java.lang.reflect.Method
import java.util.HashMap

class ControlViewModel(private val adapterProvider: BluetoothAdapterProvider): ViewModel() {

    private val controlManager : BleControlManager = BleControlManager(adapterProvider.getContext())

    fun connect(deviceAddress: String) {
        val device = adapterProvider.getAdapter().getRemoteDevice(deviceAddress)
        controlManager.connect(device)
            .retry(2, 100)
            .useAutoConnect(false)
            .done {
                Log.e("ControlViewModel", "connection success!")
            }
            .fail { _, status ->
                Log.e("ControlViewModel", "connection failed, $status")
            }
            .enqueue()
        controlManager.setConnectionObserver(connectionObserver)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {

        controlManager.disconnect().enqueue()
        controlManager.close()

    }

    fun printData(data: String) {
        if (controlManager.isReady) {
            controlManager.printData(data)
        }
    }

    fun getData(): String {
        if (controlManager.isReady) {
            return controlManager.getData()
        }
        return ""
    }

    private val connectionObserver = object : ConnectionObserver {

        override fun onDeviceConnecting(device: BluetoothDevice) {}

        override fun onDeviceConnected(device: BluetoothDevice) {}

        override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {}

        override fun onDeviceReady(device: BluetoothDevice) {
            Log.e("ControlViewModel", "onDeviceReady() device is ready!")
        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {}

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {}

    }

}

@Suppress("UNCHECKED_CAST")
class ControlViewModelFactory (private val adapterProvider: BluetoothAdapterProvider) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControlViewModel::class.java)) {
            return ControlViewModel(adapterProvider) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}