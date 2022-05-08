package com.zark.bbandroid.bttest.adapter;

import android.bluetooth.BluetoothDevice;

public class ListItem {
   //   private String btName;
//   private String btMac;
   private BluetoothDevice btDevice;
   private String itemTake = BtAdapter.DEF_ITEM_TYPE;

   public String getItemType() {
      return itemTake;
   }

   public void setItemType(String itemTake) {
      this.itemTake = itemTake;
   }

   public BluetoothDevice getBtDevice() {
      return btDevice;
   }

   public void setBtDevice(BluetoothDevice btDevice) {
      this.btDevice = btDevice;
   }
}
