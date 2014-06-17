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

import android.content.pm.PackageInfo;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import br.com.bandtec.serialbtcontrol.activity.ClientActivity;
import br.com.bandtec.serialbtcontrol.ui.BgButton;
import br.com.bandtec.serialbtcontrol.ui.UI;
import br.com.bandtec.serialbtcontrol.ui.drawable.BorderDrawable;

public final class ActivityAbout extends ClientActivity implements View.OnClickListener {
	private ScrollView list;
	private BgButton btnGoBack;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreateLayout(boolean firstCreation) {
		setContentView(R.layout.activity_about);
		btnGoBack = (BgButton)findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(this);
		btnGoBack.setIcon(UI.ICON_GOBACK);
		list = (ScrollView)findViewById(R.id.list);
		list.setHorizontalFadingEdgeEnabled(false);
		list.setVerticalFadingEdgeEnabled(false);
		list.setFadingEdgeLength(0);
		list.setBackgroundDrawable(new BorderDrawable(0, UI.thickDividerSize, 0, 0));
		final TextView lblTitle = (TextView)findViewById(R.id.lblTitle);
		lblTitle.setText("Serial BT Control");
		UI.largeTextAndColor(lblTitle);
		final TextView lblVersion = (TextView)findViewById(R.id.lblVersion);
		UI.smallTextAndColor(lblVersion);
		try {
			final PackageInfo inf = getApplication().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);
			lblVersion.setText("v" + inf.versionName);
		} catch (Throwable e) {
		}
		final TextView lblMsg = (TextView)findViewById(R.id.lblMsg);
		lblMsg.setAutoLinkMask(Linkify.ALL);
		lblMsg.setLinksClickable(true);
		lblMsg.setText(getText(R.string.app_more_info));
		lblMsg.setLinkTextColor(UI.color_selected_grad_dk);
		lblMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, UI._14sp);
		if (UI.isLowDpiScreen) {
			findViewById(R.id.panelControls).setPadding(0, 0, 0, 0);
			findViewById(R.id.panelMsg).setPadding(0, 0, 0, 0);
		} else if (UI.isLargeScreen) {
			UI.prepareViewPaddingForLargeScreen(list, 0);
			lblMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, UI._18sp);
		}
	}
	
	@Override
	protected void onOrientationChanged() {
		if (UI.isLargeScreen && list != null)
			UI.prepareViewPaddingForLargeScreen(list, 0);
	}
	
	@Override
	protected void onCleanupLayout() {
		list = null;
		btnGoBack = null;
	}
	
	@Override
	public void onClick(View view) {
		if (view == btnGoBack) {
			finish();
		}
	}
}
