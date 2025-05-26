package com.example.monitoringsystem;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;

public class BleControlManager extends BleManager {

    public static final UUID SERVICE_UUID  = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    public static final UUID REQUEST_CHARACTERISTIC_UUID = UUID.fromString("f645582d-9f38-4b7b-a429-8494d5b0fc1d");
    public static final UUID RESPONSE_CHARACTERISTIC_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
    public static final UUID LATITUDE_CHARACTERISTIC_UUID = UUID.fromString("15de1c18-3760-4daf-a0e7-75eb737d8600");
    public static final UUID LONGITUDE_CHARACTERISTIC_UUID = UUID.fromString("b1dfa597-b3a5-4a98-b7ba-941825952aba");
    private BluetoothGattCharacteristic RequestCharacteristic;
    private BluetoothGattCharacteristic ResponseCharacteristic;
    private BluetoothGattCharacteristic LatitudeCharacteristic;
    private BluetoothGattCharacteristic LongitudeCharacteristic;
    public BleControlManager(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new BleControlManagerGattCallback();
    }

    public void printData(String data) {
        writeCharacteristic(ResponseCharacteristic, data.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue();
    }

    public String getData() {
        readCharacteristic(LatitudeCharacteristic).enqueue();
        readCharacteristic(LongitudeCharacteristic).enqueue();
        readCharacteristic(ResponseCharacteristic).enqueue();

        return "Latitude: " + LatitudeCharacteristic.getStringValue(0) + ", Longitude: " + LongitudeCharacteristic.getStringValue(0) + ", Sent data: " + ResponseCharacteristic.getStringValue(0) + ".";
    }
    public void setLongitude(String longitude) {
        writeCharacteristic(LongitudeCharacteristic, longitude.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue();
    }

    public void setLatitude(String latitude) {
        writeCharacteristic(LatitudeCharacteristic, latitude.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).enqueue();
    }

    class BleControlManagerGattCallback extends BleManagerGattCallback {

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {

            BluetoothGattService Service = gatt.getService(SERVICE_UUID);

            if (Service != null) {
                RequestCharacteristic = Service.getCharacteristic(REQUEST_CHARACTERISTIC_UUID);
                ResponseCharacteristic = Service.getCharacteristic(RESPONSE_CHARACTERISTIC_UUID);
                LatitudeCharacteristic = Service.getCharacteristic(LATITUDE_CHARACTERISTIC_UUID);
                LongitudeCharacteristic = Service.getCharacteristic(LONGITUDE_CHARACTERISTIC_UUID);
                readCharacteristic(LatitudeCharacteristic).enqueue();
                readCharacteristic(LongitudeCharacteristic).enqueue();
            }

            return RequestCharacteristic != null && ResponseCharacteristic != null && LatitudeCharacteristic != null && LongitudeCharacteristic != null;
        }

        @Override
        protected void onServicesInvalidated() {
            RequestCharacteristic = null;
            ResponseCharacteristic = null;
            LatitudeCharacteristic = null;
            LongitudeCharacteristic = null;
        }
    }
}
