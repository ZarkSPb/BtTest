package com.zark.bbandroid.bttest.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class ConnectThread extends Thread {
   private Context context;
   private BluetoothAdapter btAdapter;
   private BluetoothDevice device;
   private BluetoothSocket mSocket;
   public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";

   public ConnectThread(Context context, BluetoothAdapter btAdapter, BluetoothDevice device) {
      this.context = context;
      this.btAdapter = btAdapter;
      this.device = device;
   }
}