package com.zark.bbandroid.bttest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zark.bbandroid.bttest.adapter.BtConsts;
import com.zark.bbandroid.bttest.bluetooth.BtConnection;

public class MainActivity extends AppCompatActivity {

   private final int ENABLE_REQUEST = 15;
   private String LOG_TAG = "mylog";

   private MenuItem menuItem;
   private BluetoothAdapter btAdapter;
   private SharedPreferences pref;

   private BtConnection btConnection;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      init();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.main_menu, menu);
      menuItem = menu.findItem(R.id.id_bt_button);
      setBtIcon();

      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()) {
         case R.id.id_bt_button:
            if (!btAdapter.isEnabled()) {
               enableBt();
            } else {
               btAdapter.disable();
               menuItem.setIcon(R.drawable.ic_bt_enable);
            }
            break;
         case R.id.id_menu:
            if (btAdapter.isEnabled()) {
               Intent i = new Intent(MainActivity.this, BtListActivity.class);
               startActivity(i);
            } else {
               Toast.makeText(this, "Switch on Bluetooth to go to this screen", Toast.LENGTH_SHORT).show();
            }
            break;
         case R.id.id_connect:
            btConnection.connect();
            break;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == ENABLE_REQUEST) {
         if (resultCode == RESULT_OK) {
            setBtIcon();
         }
      }
   }

   private void setBtIcon() {
      if (btAdapter != null) {
         if (btAdapter.isEnabled()) {
            menuItem.setIcon(R.drawable.ic_bt_disable);
         } else {
            menuItem.setIcon(R.drawable.ic_bt_enable);
         }
      }
   }

   private void init() {
      btAdapter = BluetoothAdapter.getDefaultAdapter();
      pref = getSharedPreferences(BtConsts.MY_PREF, Context.MODE_PRIVATE);
      btConnection = new BtConnection(this);
//      Log.d(LOG_TAG, "Bt name: " + pref.getString(BtConsts.MAC_KEY, "no bt selected"));
   }

   private void enableBt() {
      Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(i, ENABLE_REQUEST);
   }

}