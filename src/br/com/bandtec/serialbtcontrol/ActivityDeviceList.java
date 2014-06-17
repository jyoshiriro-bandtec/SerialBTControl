//
//    Serial BT Control
//    Copyright (c) 2014 Carlos Rafael Gimenes das Neves
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program. If not, see {http://www.gnu.org/licenses/}.
//
//    https://github.com/BandTec/SerialBTControl
//
package br.com.bandtec.serialbtcontrol;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import br.com.bandtec.serialbtcontrol.activity.ClientActivity;
import br.com.bandtec.serialbtcontrol.list.BaseList;
import br.com.bandtec.serialbtcontrol.list.DeviceItem;
import br.com.bandtec.serialbtcontrol.ui.BgButton;
import br.com.bandtec.serialbtcontrol.ui.BgListView;
import br.com.bandtec.serialbtcontrol.ui.BgTextView;
import br.com.bandtec.serialbtcontrol.ui.UI;
import br.com.bandtec.serialbtcontrol.util.ArraySorter.Comparer;

public class ActivityDeviceList extends ClientActivity implements AdapterView.OnItemClickListener, View.OnClickListener, Comparer<DeviceItem> {
	public static final String PAIRING = "pairing";
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	private BluetoothAdapter btAdapter;
	private BgButton btnGoBack, btnRefresh;
	private BaseList<DeviceItem> deviceList;
	private LinearLayout panelScanning;
	private BroadcastReceiver receiver;
	private ArrayList<String> recentlyUsedAddresses;
	
	public ActivityDeviceList(ArrayList<String> recentlyUsedAddresses) {
		this.recentlyUsedAddresses = ((recentlyUsedAddresses == null) ? new ArrayList<String>() : recentlyUsedAddresses);
	}
	
	@Override
	public int compare(DeviceItem a, DeviceItem b) {
		if (a.highlight != b.highlight)
			return (a.highlight ? -1 : 1);
		return a.description.compareToIgnoreCase(b.description);
	}
	
	@Override
	protected void onCreate() {
		setContentView(R.layout.activity_device_list);
		((BgTextView)findViewById(R.id.txtDevices)).setTextColor(UI.colorState_text_title_static);
		btnGoBack = (BgButton)findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(this);
		btnGoBack.setIcon(UI.ICON_GOBACK);
		final BgListView list = (BgListView)findViewById(R.id.list);
		deviceList = new BaseList<DeviceItem>(DeviceItem.class);
		deviceList.setObserver(list);
		list.setOnItemClickListener(this);
		btnRefresh = (BgButton)findViewById(R.id.btnRefresh);
		btnRefresh.setOnClickListener(this);
		btnRefresh.setIcon(UI.ICON_REFRESH);
		panelScanning = (LinearLayout)findViewById(R.id.panelScanning);
		if (UI.isLowDpiScreen) {
			findViewById(R.id.panelControls).setPadding(0, 0, 0, 0);
			panelScanning.setPadding(0, 0, 0, 0);
		} else if (UI.isLargeScreen) {
			UI.prepareViewPaddingForLargeScreen(list, 0);
			UI.prepareViewPaddingForLargeScreen(panelScanning, 0);
		}
		
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (deviceList == null)
					return;
				final String action = intent.getAction();
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					final String address = device.getAddress();
					boolean paired = false;
					for (int i = deviceList.getCount() - 1; i >= 0; i--) {
						if (deviceList.getItemT(i).address.equals(address)) {
							paired = deviceList.getItemT(i).paired;
							deviceList.setSelection(i, true);
							deviceList.removeSelection();
							break;
						}
					}
					final String name = device.getName();
					deviceList.add(new DeviceItem(((name == null || name.length() == 0) ? getText(R.string.null_device_name).toString() : name) + " - " + address, address, paired, recentlyUsedAddresses.contains(address)), -1);
					deviceList.setSelection(-1, true);
					deviceList.sort(ActivityDeviceList.this);
				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
					if (btnRefresh != null)
						btnRefresh.setVisibility(View.VISIBLE);
					if (panelScanning != null)
						panelScanning.setVisibility(View.GONE);
					if (deviceList.getCount() == 0)
						deviceList.add(new DeviceItem(getText(R.string.none_found).toString(), null, false, false), -1);
				}
			}
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getHostActivity().registerReceiver(receiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		getHostActivity().registerReceiver(receiver, filter);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		final Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices)
				deviceList.add(new DeviceItem(device.getName() + " - " + device.getAddress(), device.getAddress(), true, recentlyUsedAddresses.contains(device.getAddress())), -1);
			deviceList.sort(ActivityDeviceList.this);
		}
        if (btAdapter.isDiscovering())
        	btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
	}
	
	@Override
	protected void onDestroy() {
		btnGoBack = null;
		btnRefresh = null;
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
			btAdapter = null;
		}
		if (receiver != null) {
			getHostActivity().unregisterReceiver(receiver);
			receiver = null;
		}
		if (deviceList != null) {
			deviceList.setObserver(null);
			deviceList = null;
		}
		recentlyUsedAddresses = null;
	}
	
	@Override
	public void onClick(View view) {
		if (view == btnGoBack) {
			finish();
		} else if (view == btnRefresh) {
	        if (btAdapter == null)
	        	return;
			if (btAdapter.isDiscovering())
	        	btAdapter.cancelDiscovery();
	        btAdapter.startDiscovery();
			btnRefresh.setVisibility(View.GONE);
			panelScanning.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id) {
		if (position < 0)
			return;
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
			btAdapter = null;
		}
		final DeviceItem item = deviceList.getItemT(position);
		if (item.address == null || item.address.length() < 17)
			return;
		final Intent intent = new Intent();
		final Bundle data = new Bundle();
		data.putString(EXTRA_DEVICE_ADDRESS, item.address);
		data.putBoolean(PAIRING, !item.paired);
		intent.putExtras(data);
		finish(1, intent);
	}
}
