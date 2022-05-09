package com.zark.bbandroid.bttest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zark.bbandroid.bttest.adapter.BtAdapter;
import com.zark.bbandroid.bttest.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtListActivity extends AppCompatActivity {
   private final int BT_REQUEST_PERM = 111;

   private ListView listView;
   private BtAdapter adapter;
   private BluetoothAdapter btAdapter;
   private List<ListItem> list;
   private boolean isBtPermissionGranted = false;
   private boolean isDiscovery = false;
   private ActionBar ab;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_bt_list);
      getBtPermission();
      init();
   }

   @Override
   protected void onResume() {
      super.onResume();
      IntentFilter f1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
      IntentFilter f2 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
      IntentFilter f3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
      registerReceiver(bReceiver, f1);
      registerReceiver(bReceiver, f2);
      registerReceiver(bReceiver, f3);
   }

   @Override
   protected void onPause() {
      super.onPause();
      unregisterReceiver(bReceiver);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.bt_list_menu, menu);
      return super.onCreateOptionsMenu(menu);
   }

   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      switch (item.getItemId()) {
         case android.R.id.home:
            if (isDiscovery) {
               btAdapter.cancelDiscovery();
               isDiscovery = false;
               getPairedDevices();
            } else {
               finish();
            }
            break;
         case R.id.id_search:
            if (isDiscovery) return true;
            ab.setTitle(R.string.discovering);
            list.clear();
            ListItem itemTitle = new ListItem();
            itemTitle.setItemType(BtAdapter.TITLE_ITEM_TYPE);
            list.add(itemTitle);
            adapter.notifyDataSetChanged();
            btAdapter.startDiscovery();
            isDiscovery = true;
            break;
      }

      return true;
   }

   private void init() {
      ab = getSupportActionBar();

      btAdapter = BluetoothAdapter.getDefaultAdapter();
      list = new ArrayList<>();

      ActionBar ab = getSupportActionBar();
      if (ab == null) return;
      ab.setDisplayHomeAsUpEnabled(true);

      listView = findViewById(R.id.listView);

      adapter = new BtAdapter(this, R.layout.bt_list_item, list);
      listView.setAdapter(adapter);
      getPairedDevices();

      onItemClickListener();
   }

   private void onItemClickListener() {
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ListItem item = (ListItem) adapterView.getItemAtPosition(i);
            if (item.getItemType().equals(BtAdapter.DISCOVERY_ITEM_TYPE)) {
               item.getBtDevice().createBond();
            }
         }
      });
   }

   private void getPairedDevices() {
      Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
      list.clear();
      for (BluetoothDevice device : pairedDevices) {
         ListItem item = new ListItem();
         item.setBtDevice(device);
         list.add(item);
      }
      adapter.notifyDataSetChanged();
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == BT_REQUEST_PERM) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isBtPermissionGranted = true;
         } else {
            Toast.makeText(this, "No permission for find devices", Toast.LENGTH_SHORT).show();
         }
      } else {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
   }

   private void getBtPermission() {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(this,
               new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, BT_REQUEST_PERM);
      } else {
         isBtPermissionGranted = true;
      }
   }

   private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
         if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            ListItem item = new ListItem();
            item.setBtDevice(device);
            item.setItemType(BtAdapter.DISCOVERY_ITEM_TYPE);
            list.add(item);
            adapter.notifyDataSetChanged();
         } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
            isDiscovery = false;
            getPairedDevices();
            ab.setTitle(R.string.app_name);
         }  else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
               getPairedDevices();
            }
         }
      }
   };
}