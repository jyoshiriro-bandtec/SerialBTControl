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
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import br.com.bandtec.serialbtcontrol.activity.ClientActivity;
import br.com.bandtec.serialbtcontrol.activity.MainHandler;
import br.com.bandtec.serialbtcontrol.ui.BgButton;
import br.com.bandtec.serialbtcontrol.ui.BgDirControl;
import br.com.bandtec.serialbtcontrol.ui.BgTextView;
import br.com.bandtec.serialbtcontrol.ui.UI;
import br.com.bandtec.serialbtcontrol.util.SerializableMap;

public final class ActivityMain extends ClientActivity implements MainHandler.Callback, View.OnClickListener, BgButton.OnPressingChangeListener, BgDirControl.OnBgDirControlChangeListener, BTConnectable, DialogInterface.OnClickListener {
	private static final int OPT_FORCEDORIENTATION = 0x0001;
	private static final int OPT_LASTADDRESS0 = 0x0020;
	private static final int OPT_LASTADDRESS4 = 0x0024;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int REQUEST_ENABLE_BT = 2000;
	private BTCommunicator btCommunicator;
	private boolean btErrorPending, btOnByUs, btAlreadyShown, pairing, lastBtnScanAgainVisibility;
	private ProgressDialog connectingProgressDialog;
	private CharSequence lastError;
	private int forcedOrientation, lastDir;
	private BgButton btnExit, btnPortrait, btnLandscape, btnAbout, btnScanAgain;
	private BgButton[] btns;
	private BgDirControl dirControl;
	private ArrayList<String> recentlyUsedAddresses; 
	private String lastAddress;
	
	@Override
	public boolean isPairing() {
		return pairing;
	}
	
	private void destroyBTCommunicator() {
		BTCommunicator.destroyBTCommunicatorNow();
		btCommunicator = null;
	}
	
	private void resetUIAfterError() {
		lastError = null;
		final BgTextView txtError = (BgTextView)findViewById(R.id.txtError);
		txtError.setVisibility(View.GONE);
		btnScanAgain.setVisibility(View.GONE);
		dirControl.setVisibility(View.VISIBLE);
		findViewById(R.id.panelMsg).setVisibility(View.VISIBLE);
		findViewById(R.id.panelMsg2).setVisibility(View.VISIBLE);
	}
	
	private void showError(CharSequence error, boolean showScanButton) {
		lastError = error;
		lastBtnScanAgainVisibility = showScanButton;
		final BgTextView txtError = (BgTextView)findViewById(R.id.txtError);
		txtError.setText(error);
		txtError.setTextColor(UI.colorState_text_title_static);
		RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		p.leftMargin = UI._16dp;
		p.topMargin = UI._16dp;
		p.rightMargin = UI._16dp;
		p.bottomMargin = UI._16dp;
		if (showScanButton) {
			btnScanAgain.setVisibility(View.VISIBLE);
			if (UI.isLandscape) {
				p.addRule(RelativeLayout.ABOVE, R.id.btnScanAgain);
				p.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				p.addRule(RelativeLayout.RIGHT_OF, R.id.panelControls);
			} else {
				p.addRule(RelativeLayout.ABOVE, R.id.btnScanAgain);
				p.addRule(RelativeLayout.BELOW, R.id.panelControls);
			}
		} else {
			btnScanAgain.setVisibility(View.GONE);
			if (UI.isLandscape) {
				p.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				p.addRule(RelativeLayout.RIGHT_OF, R.id.panelControls);
			} else {
				p.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
				p.addRule(RelativeLayout.BELOW, R.id.panelControls);
			}
		}
		txtError.setLayoutParams(p);
		txtError.setVisibility(View.VISIBLE);
		dirControl.setVisibility(View.GONE);
		findViewById(R.id.panelMsg).setVisibility(View.GONE);
		findViewById(R.id.panelMsg2).setVisibility(View.GONE);
	}
	
	private void showError(int resId, boolean showScanButton) {
		showError(getText(resId), showScanButton);
	}
	
	@Override
	public int getDesiredWindowColor() {
		return UI.color_list;
	}
	
	@Override
	public void activityFinished(ClientActivity activity, int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (resultCode > 0) {
				lastAddress = data.getExtras().getString(ActivityDeviceList.EXTRA_DEVICE_ADDRESS).toUpperCase(Locale.US);
				pairing = data.getExtras().getBoolean(ActivityDeviceList.PAIRING);
				connectingProgressDialog = ProgressDialog.show(getHostActivity(), "", getResources().getString(R.string.connecting_please_wait), true);
				destroyBTCommunicator();
				btCommunicator = BTCommunicator.getBTCommunicator(this, this, BluetoothAdapter.getDefaultAdapter(), getResources());
				btCommunicator.setMACAddress(lastAddress);
				btCommunicator.start();
			} else {
				showError(R.string.none_paired, true);
			}
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			switch (resultCode) {
				case Activity.RESULT_OK:
					btOnByUs = true;
					lastError = null;
					startActivity(new ActivityDeviceList(recentlyUsedAddresses), REQUEST_CONNECT_DEVICE);
					break;
				case Activity.RESULT_CANCELED:
					showError(R.string.bt_needs_to_be_enabled, false);
					break;
				default:
					showError(R.string.problem_at_connecting, false);
					break;
			}
			break;
		}
	}
	
	@Override
	public void onClick(View view) {
		if (view == btnExit) {
			finish();
		} else if (view == btnPortrait) {
			forcedOrientation = 1;
			getHostActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (view == btnLandscape) {
			forcedOrientation = -1;
			getHostActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else if (view == btnAbout) {
			startActivity(new ActivityAbout());
		} else if (view == btnScanAgain) {
			btErrorPending = false;
			resetUIAfterError();
			startActivity(new ActivityDeviceList(recentlyUsedAddresses), REQUEST_CONNECT_DEVICE);
		}
	}
	
	@Override
	public void onPressingChanged(BgButton button, boolean pressed) {
		for (int i = 0; i < 8; i++) {
			if (button == btns[i]) {
				if (btCommunicator != null)
					btCommunicator.sendMessage(BTCommunicator.DO_ACTION, (int)(pressed ? 'A' : 'a') + i);
				return;
			}
		}
	}
	
	@Override
	public void onDirectionChanged(BgDirControl dirControl, char direction) {
		if (btCommunicator != null)
			btCommunicator.sendMessage(BTCommunicator.DO_ACTION, (int)direction);
	}
	
	@Override
	protected boolean onBackPressed() {
		return true; //prevent back key from going back to the home screen
	}
	
	@Override
	public void onClick(DialogInterface dialog, int id) {
		btErrorPending = false;
		dialog.cancel();
		startActivity(new ActivityDeviceList(recentlyUsedAddresses), REQUEST_CONNECT_DEVICE);
	}
	
	@Override
	public boolean handleMessage(Message message) {
		switch (message.getData().getInt("message")) {
		case BTCommunicator.DISPLAY_TOAST:
			UI.toast(getApplication(), message.getData().getString("toastText"));
			break;
		case BTCommunicator.STATE_CONNECTED:
			connectingProgressDialog.dismiss();
			UI.toast(getApplication(), R.string.connected);
			if (recentlyUsedAddresses != null && lastAddress != null) {
				final int i = recentlyUsedAddresses.indexOf(lastAddress);
				if (i >= 0)
					recentlyUsedAddresses.remove(i);
				recentlyUsedAddresses.add(0, lastAddress);
				while (recentlyUsedAddresses.size() > 5)
					recentlyUsedAddresses.remove(5);
			}
			break;
		case BTCommunicator.STATE_CONNECTERROR_PAIRING:
			connectingProgressDialog.dismiss();
			destroyBTCommunicator();
			startActivity(new ActivityDeviceList(recentlyUsedAddresses), REQUEST_CONNECT_DEVICE);
			break;
		case BTCommunicator.STATE_CONNECTERROR:
			connectingProgressDialog.dismiss();
		case BTCommunicator.STATE_RECEIVEERROR:
		case BTCommunicator.STATE_SENDERROR:
			destroyBTCommunicator();
			if (!btErrorPending) {
				btErrorPending = true;
				final AlertDialog.Builder builder = new AlertDialog.Builder(getHostActivity());
				builder.setTitle(getResources().getString(R.string.oops))
						.setMessage(getResources().getString(R.string.bt_error_dialog_message))
						.setCancelable(false)
						.setPositiveButton(R.string.got_it, this);
				builder.create().show();
			}
			break;
		}
		return true;
	}
	
	@Override
	protected void onCreate() {
		final Context context = getApplication();
		SerializableMap opts = SerializableMap.deserialize(context, "_SerialBTControl");
		if (opts == null)
			opts = new SerializableMap();
		forcedOrientation = opts.getInt(OPT_FORCEDORIENTATION, 1);
		recentlyUsedAddresses = new ArrayList<String>(5);
		for (int i = OPT_LASTADDRESS0; i <= OPT_LASTADDRESS4; i++) {
			final String addr = opts.getString(i, null);
			if (addr != null)
				recentlyUsedAddresses.add(addr.toUpperCase(Locale.US));
		}
		addWindowFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (forcedOrientation < 0)
			getHostActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		else
			getHostActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		btns = new BgButton[8];
		lastError = null;
		lastDir = BgDirControl.CENTER_DIRECTION;
		btAlreadyShown = false;
	}
	
	@Override
	protected void onResume() {
		if (BluetoothAdapter.getDefaultAdapter() == null) {
			showError(R.string.bt_initialization_failure, false);
			return;
		}
		if (!btAlreadyShown) {
			btAlreadyShown = true;
			if (!BluetoothAdapter.getDefaultAdapter().isEnabled())
				getHostActivity().startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
			else
				startActivity(new ActivityDeviceList(recentlyUsedAddresses), REQUEST_CONNECT_DEVICE);
		}
	}
	
	@Override
	protected void onCreateLayout(boolean firstCreation) {
		setContentView(UI.isLandscape ? R.layout.activity_main_l : R.layout.activity_main);
		btnExit = (BgButton)findViewById(R.id.btnExit);
		btnExit.setOnClickListener(this);
		btnPortrait = (BgButton)findViewById(R.id.btnPortrait);
		btnPortrait.setOnClickListener(this);
		btnLandscape = (BgButton)findViewById(R.id.btnLandscape);
		btnLandscape.setOnClickListener(this);
		btnAbout = (BgButton)findViewById(R.id.btnAbout);
		btnAbout.setOnClickListener(this);
		btnExit.setIcon(UI.ICON_EXIT);
		btnPortrait.setIcon(UI.ICON_PORTRAIT);
		btnLandscape.setIcon(UI.ICON_LANDSCAPE);
		btnAbout.setIcon(UI.ICON_INFO);
		btnScanAgain = (BgButton)findViewById(R.id.btnScanAgain);
		btnScanAgain.setOnClickListener(this);
		btnScanAgain.setTextColor(UI.colorState_text_listitem_reactive);
		UI.largeText(btnScanAgain);
		dirControl = (BgDirControl)findViewById(R.id.dirControl);
		dirControl.setOnBgDirControlChangeListener(this);
		dirControl.setDirectionValue(lastDir);
		btns[0] = (BgButton)findViewById(R.id.btn1);
		btns[1] = (BgButton)findViewById(R.id.btn2);
		btns[2] = (BgButton)findViewById(R.id.btn3);
		btns[3] = (BgButton)findViewById(R.id.btn4);
		btns[4] = (BgButton)findViewById(R.id.btn5);
		btns[5] = (BgButton)findViewById(R.id.btn6);
		btns[6] = (BgButton)findViewById(R.id.btn7);
		btns[7] = (BgButton)findViewById(R.id.btn8);
		for (int i = 0; i < 8; i++) {
			btns[i].setText(Character.toString((char) ('A' + i)));
			btns[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, UI._22sp);
			btns[i].setOnPressingChangeListener(this);
		}
		if (UI.isLowDpiScreen) {
			findViewById(R.id.panelControls).setPadding(0, 0, 0, 0);
			findViewById(R.id.panelMsg).setPadding(0, 0, 0, 0);
			findViewById(R.id.panelMsg2).setPadding(0, 0, 0, 0);
		} else if (UI.isLargeScreen) {
			final MarginLayoutParams lp = (MarginLayoutParams)dirControl.getLayoutParams();
			lp.leftMargin = UI.dpToPxI(100);
			lp.topMargin = UI.dpToPxI(100);
			lp.rightMargin = UI.dpToPxI(100);
			lp.bottomMargin = UI.dpToPxI(100);
			dirControl.setLayoutParams(lp);
		}
		if (UI.isLandscape) {
			for (int i = 0; i < 8; i++)
				btns[i].setPadding(UI._8dp, 0, UI._8dp, 0);
		}
		if (lastError != null)
			showError(lastError, lastBtnScanAgainVisibility);
	}
	
	@Override
	protected void onOrientationChanged() {
		onCleanupLayout();
		onCreateLayout(false);
	}
	
	@Override
	protected void onCleanupLayout() {
		btnExit = null;
		btnPortrait = null;
		btnLandscape = null;
		btnAbout = null;
		btnScanAgain = null;
		if (dirControl != null) {
			lastDir = dirControl.getDirectionValue();
			dirControl = null;
		}
		for (int i = 0; i < 8; i++)
			btns[i] = null;
	}
	
	@Override
	protected void onDestroy() {
		setExitOnDestroy(true);
		lastError = null;
		btns = null;
		SerializableMap opts = new SerializableMap(32);
		opts.put(OPT_FORCEDORIENTATION, forcedOrientation);
		if (recentlyUsedAddresses != null) {
			for (int i = 0; i < recentlyUsedAddresses.size() && i < 5; i++) {
				final String addr = recentlyUsedAddresses.get(i);
				if (addr != null)
					opts.put(OPT_LASTADDRESS0 + i, addr);
			}
		}
		opts.serialize(getApplication(), "_SerialBTControl");
		destroyBTCommunicator();
		if (btOnByUs) {
			BluetoothAdapter.getDefaultAdapter().disable();
			btOnByUs = false;
		}
		recentlyUsedAddresses = null;
		lastAddress = null;
	}
}
