//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013-2014, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package br.com.bandtec.serialbtcontrol.ui;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import br.com.bandtec.serialbtcontrol.R;
import br.com.bandtec.serialbtcontrol.ui.drawable.BorderDrawable;
import br.com.bandtec.serialbtcontrol.util.ColorUtils;

//
//Unit conversions are based on:
//http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.3_r1/android/util/TypedValue.java
//
public final class UI {
	public static final int STATE_PRESSED = 1;
	public static final int STATE_FOCUSED = 2;
	public static final int STATE_CURRENT = 4;
	public static final int STATE_SELECTED = 8;
	public static final int STATE_MULTISELECTED = 16;
	public static final int STATE_CHECKED = 32;
	
	public static final String ICON_EXIT = "X";
	public static final String ICON_MENU = "N";
	public static final String ICON_ORIENTATION = "O";
	public static final String ICON_GOBACK = "_";
	public static final String ICON_INFO = "I";
	public static final String ICON_REFRESH = "R";
	public static final String ICON_SETTINGS = "s";
	public static final String ICON_OPTCHK = "q";
	public static final String ICON_OPTUNCHK = "Q";
	
	public static final int color_window = 0xff303030;
	public static final int color_control_mode = 0xff000000;
	public static final int color_visualizer = 0xff000000;
	public static final int color_list = 0xff080808;
	public static final int color_menu = 0xffffffff;
	public static final int color_menu_icon = 0xff555555;
	public static final int color_divider = 0xff464646;
	public static final int color_highlight = 0xfffad35a;
	public static final int color_text_highlight = 0xff000000;
	public static final int color_text = 0xffffffff;
	public static final int color_text_disabled = 0xff8c8c8c; //959595;
	public static final int color_text_listitem = 0xffffffff;
	public static final int color_text_listitem_secondary = 0xfffad35a;
	public static final int color_text_selected = 0xff000000;
	public static final int color_text_menu = 0xff000000;
	public static final int color_selected_grad_lt = 0xffd1e8ff;
	public static final int color_selected_grad_dk = 0xff5da2e3;
	public static final int color_selected_border = 0xff518ec2;
	public static final int color_selected_pressed = 0xffcfe1ff;
	public static final int color_focused_grad_lt = 0xfff7eb6a;
	public static final int color_focused_grad_dk = 0xfffeb645;
	public static final int color_focused_border = 0xffad9040;
	public static final int color_focused_pressed = 0xffffeed4;
	public static final int color_selected = ColorUtils.blend(color_selected_grad_lt, color_selected_grad_dk, 0.5f);
	public static final int color_focused = ColorUtils.blend(color_focused_grad_lt, color_focused_grad_dk, 0.5f);
	public static final int color_selected_multi = ColorUtils.blend(color_selected, color_list, 0.7f);
	public static final int color_selected_pressed_border = color_selected_border;
	public static final int color_focused_pressed_border = color_focused_border;
	public static final int color_text_title = color_highlight;
	public static final int color_menu_border = color_selected_border;
	public static final int color_glow_dk = 0xff686868;
	public static final int color_glow_lt = 0xffffffff;
	public static final ColorStateList colorState_text_white_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, 0xffffffff });
	public static final ColorStateList colorState_text_menu_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text_menu });
	public static final ColorStateList colorState_text_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text });
	public static final ColorStateList colorState_text_static = ColorStateList.valueOf(color_text);
	public static final ColorStateList colorState_text_listitem_static = ColorStateList.valueOf(color_text_listitem);
	public static final ColorStateList colorState_text_listitem_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text_listitem });
	public static final ColorStateList colorState_text_listitem_secondary_static = ColorStateList.valueOf(color_text_listitem_secondary);
	public static final ColorStateList colorState_text_selected_static = ColorStateList.valueOf(color_text_selected);
	public static final ColorStateList colorState_highlight_static = ColorStateList.valueOf(color_highlight);
	public static final ColorStateList colorState_text_highlight_static = ColorStateList.valueOf(color_text_highlight);
	public static final ColorStateList colorState_text_highlight_reactive = new ColorStateList(new int[][] { new int[] { android.R.attr.state_pressed }, new int[] { android.R.attr.state_focused }, new int[] {} }, new int[] { color_text_selected, color_text_selected, color_text_highlight });
	public static final ColorStateList colorState_text_title_static = colorState_highlight_static;
	
	public static Typeface iconsTypeface, defaultTypeface;
	
	public static final class DisplayInfo {
		public int usableScreenWidth, usableScreenHeight, screenWidth, screenHeight;
		public boolean isLargeScreen, isLandscape, isLowDpiScreen;
		public DisplayMetrics displayMetrics;
		
		private void initializeScreenDimensions(Display display, DisplayMetrics outDisplayMetrics) {
			display.getMetrics(outDisplayMetrics);
			screenWidth = outDisplayMetrics.widthPixels;
			screenHeight = outDisplayMetrics.heightPixels;
			usableScreenWidth = screenWidth;
			usableScreenHeight = screenHeight;
		}
		
		@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		private void initializeScreenDimensions14(Display display, DisplayMetrics outDisplayMetrics) {
			try {
				screenWidth = (Integer)Display.class.getMethod("getRawWidth").invoke(display);
				screenHeight = (Integer)Display.class.getMethod("getRawHeight").invoke(display);
			} catch (Throwable ex) {
				initializeScreenDimensions(display, outDisplayMetrics);
				return;
			}
			display.getMetrics(outDisplayMetrics);
			usableScreenWidth = outDisplayMetrics.widthPixels;
			usableScreenHeight = outDisplayMetrics.heightPixels;
		}
		
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		private void initializeScreenDimensions17(Display display, DisplayMetrics outDisplayMetrics) {
			display.getMetrics(outDisplayMetrics);
			usableScreenWidth = outDisplayMetrics.widthPixels;
			usableScreenHeight = outDisplayMetrics.heightPixels;
			display.getRealMetrics(outDisplayMetrics);
			screenWidth = outDisplayMetrics.widthPixels;
			screenHeight = outDisplayMetrics.heightPixels;
		}
		
		public void getInfo(Context context) {
			final Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			displayMetrics = new DisplayMetrics();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				initializeScreenDimensions17(display, displayMetrics);
			else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				initializeScreenDimensions14(display, displayMetrics);
			else
				initializeScreenDimensions(display, displayMetrics);
			//improved detection for tablets, based on:
			//http://developer.android.com/guide/practices/screens_support.html#DeclaringTabletLayouts
			//(There is also the solution at http://stackoverflow.com/questions/11330363/how-to-detect-device-is-android-phone-or-android-tablet
			//but the former link says it is deprecated...)
			//*** I decided to treat screens >= 500dp as large screens because there are
			//lots of 7" phones/tablets with resolutions starting at around 533dp ***
			final int _500dp = (int)((500.0f * displayMetrics.density) + 0.5f);
			isLargeScreen = ((screenWidth >= _500dp) && (screenHeight >= _500dp));
			isLandscape = (screenWidth >= screenHeight);
			isLowDpiScreen = (displayMetrics.densityDpi < 160);
		}
	}
	
	private static final class Gradient {
		private static final Gradient[] gradients = new Gradient[16];
		private static int pos, count;
		public final boolean focused, vertical;
		public final int size;
		public final LinearGradient gradient;
		
		private Gradient(boolean focused, boolean vertical, int size) {
			this.focused = focused;
			this.vertical = vertical;
			this.size = size;
			this.gradient = (focused ? new LinearGradient(0, 0, (vertical ? size : 0), (vertical ? 0 : size), color_focused_grad_lt, color_focused_grad_dk, Shader.TileMode.CLAMP) :
				new LinearGradient(0, 0, (vertical ? size : 0), (vertical ? 0 : size), color_selected_grad_lt, color_selected_grad_dk, Shader.TileMode.CLAMP));
		}
		
		public static LinearGradient getGradient(boolean focused, boolean vertical, int size) {
			//a LRU algorithm could be implemented here...
			for (int i = count - 1; i >= 0; i--) {
				if (gradients[i].size == size && gradients[i].focused == focused && gradients[i].vertical == vertical)
					return gradients[i].gradient;
			}
			if (count < 16) {
				pos = count;
				count++;
			} else {
				pos = (pos + 1) & 15;
			}
			final Gradient g = new Gradient(focused, vertical, size);
			gradients[pos] = g;
			return g.gradient;
		}
	}
	
	public static final Rect rect = new Rect();
	public static char decimalSeparator;
	public static boolean isLandscape, isLargeScreen, isLowDpiScreen;
	public static int _1dp, _2dp, _4dp, _8dp, _16dp, _2sp, _4sp, _8sp, _16sp, _22sp, _18sp, _14sp, _22spBox, _IconBox, _18spBox, _14spBox, _22spYinBox, _18spYinBox, _14spYinBox,
		strokeSize, thickDividerSize, defaultControlContentsSize, defaultControlSize, usableScreenWidth, usableScreenHeight, screenWidth, screenHeight, densityDpi;
	
	private static String emptyListString;
	private static int emptyListStringHalfWidth;
	private static Toast internalToast;
	
	private static float density, scaledDensity, xdpi_1_72;
	
	public static final Paint fillPaint;
	public static final TextPaint textPaint;
	private static final PorterDuffColorFilter glowFilter;
	private static final PorterDuffColorFilter edgeFilter;
	
	static {
		fillPaint = new Paint();
		fillPaint.setDither(false);
		fillPaint.setAntiAlias(false);
		fillPaint.setStyle(Paint.Style.FILL);
		textPaint = new TextPaint();
		textPaint.setDither(false);
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setTypeface(Typeface.DEFAULT);
		textPaint.setTextAlign(Paint.Align.LEFT);
		textPaint.setColor(color_text);
		textPaint.measureText("SerialBTControl");
		//the color is treated as SRC, and the bitmap is treated as DST
		glowFilter = new PorterDuffColorFilter(color_glow_lt, PorterDuff.Mode.SRC_IN);
		//hide the edge!!! ;)
		edgeFilter = new PorterDuffColorFilter(0, PorterDuff.Mode.CLEAR);
	}
	
	public static String formatIntAsFloat(int number, boolean useTwoDecimalPlaces, boolean removeDecimalPlacesIfExact) {
		int dec;
		if (useTwoDecimalPlaces) {
			dec = number % 100;
			number /= 100;
		} else {
			dec = number % 10;
			number /= 10;
		}
		if (removeDecimalPlacesIfExact && dec == 0)
			return Integer.toString(number);
		if (dec < 0)
			dec = -dec;
		return Integer.toString(number) + decimalSeparator + ((useTwoDecimalPlaces && (dec < 10)) ? ("0" + Integer.toString(dec)) : Integer.toString(dec));
	}
	
	public static void formatIntAsFloat(StringBuilder sb, int number, boolean useTwoDecimalPlaces, boolean removeDecimalPlacesIfExact) {
		int dec;
		if (useTwoDecimalPlaces) {
			dec = number % 100;
			number /= 100;
		} else {
			dec = number % 10;
			number /= 10;
		}
		sb.append(number);
		if (!removeDecimalPlacesIfExact || dec != 0) {
			if (dec < 0)
				dec = -dec;
			sb.append(decimalSeparator);
			if (useTwoDecimalPlaces && (dec < 10))
				sb.append('0');
			sb.append(dec);
		}
	}
	
	public static void initialize(Context context) {
		if (iconsTypeface == null)
			iconsTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/icons.ttf");
		final DisplayInfo info = new DisplayInfo();
		info.getInfo(context);
		density = info.displayMetrics.density;
		densityDpi = info.displayMetrics.densityDpi;
		scaledDensity = info.displayMetrics.scaledDensity;
		xdpi_1_72 = info.displayMetrics.xdpi * (1.0f / 72.0f);
		screenWidth = info.screenWidth;
		screenHeight = info.screenHeight;
		usableScreenWidth = info.usableScreenWidth;
		usableScreenHeight = info.usableScreenHeight;
		isLargeScreen = info.isLargeScreen;
		isLandscape = info.isLandscape;
		isLowDpiScreen = info.isLowDpiScreen;
		//apparently, the display metrics returned by Resources.getDisplayMetrics()
		//is not the same as the one returned by Display.getMetrics()/getRealMetrics()
		final float sd = context.getResources().getDisplayMetrics().scaledDensity;
		if (sd > 0)
			scaledDensity = sd;
		else if (scaledDensity <= 0)
			scaledDensity = 1.0f;
		
		_1dp = dpToPxI(1);
		strokeSize = (_1dp + 1) >> 1;
		thickDividerSize = ((_1dp >= 2) ? _1dp : 2);
		_2dp = dpToPxI(2);
		_4dp = dpToPxI(4);
		_8dp = dpToPxI(8);
		_16dp = dpToPxI(16);
		_2sp = spToPxI(2);
		_4sp = spToPxI(4);
		_8sp = spToPxI(8);
		_16sp = spToPxI(16);
		_22sp = spToPxI(22);
		_18sp = spToPxI(18);
		_14sp = spToPxI(14);
		defaultControlContentsSize = dpToPxI(32);
		defaultControlSize = defaultControlContentsSize + (UI._8sp << 1);
		
		defaultTypeface = Typeface.DEFAULT;
		textPaint.setTypeface(defaultTypeface);
		//Font Metrics in Java OR How, the hell, Should I Position This Font?!
		//http://blog.evendanan.net/2011/12/Font-Metrics-in-Java-OR-How-the-hell-Should-I-Position-This-Font
		textPaint.setTextSize(_22sp);
		final FontMetrics fm = textPaint.getFontMetrics();
		_22spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_22spYinBox = _22spBox - (int)(fm.descent);
		textPaint.setTextSize(_18sp);
		textPaint.getFontMetrics(fm);
		_18spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_18spYinBox = _18spBox - (int)(fm.descent);
		textPaint.setTextSize(_14sp);
		textPaint.getFontMetrics(fm);
		_14spBox = (int)(fm.descent - fm.ascent + 0.5f);
		_14spYinBox = _14spBox - (int)(fm.descent);
		emptyListString = context.getText(R.string.empty_list).toString();
		emptyListStringHalfWidth = measureText(emptyListString, _22sp) >> 1;
		_IconBox = Math.min(spToPxI(24), _22spBox); //both descent and ascent of iconsTypeface are 0!
		
		try {
			final DecimalFormatSymbols d = new DecimalFormatSymbols(Locale.getDefault());
			decimalSeparator = d.getDecimalSeparator();
		} catch (Throwable ex) {
			decimalSeparator = '.';
		}
	}
	
	public static float pxToDp(float px) {
		return px / density;
	}
	
	public static float pxToSp(float px) {
		return px / scaledDensity;
	}
	
	public static float pxToPt(float px) {
		return px / xdpi_1_72;
	}
	
	public static float dpToPx(float dp) {
		return dp * density;
	}
	
	public static float spToPx(float sp) {
		return sp * scaledDensity;
	}
	
	public static float ptToPx(float pt) {
		return pt * xdpi_1_72;
	}
	
	public static int dpToPxI(float dp) {
		return (int)((dp * density) + 0.5f);
	}
	
	public static int spToPxI(float sp) {
		return (int)((sp * scaledDensity) + 0.5f);
	}
	
	public static int ptToPxI(float pt) {
		return (int)((pt * xdpi_1_72) + 0.5f);
	}
	
	public static String ellipsizeText(String text, int size, int width) {
		if (text == null)
			return "";
		if (width <= 1)
			return text;
		textPaint.setTextSize(size);
		return TextUtils.ellipsize(text, textPaint, width, TruncateAt.END).toString();
	}
	
	public static int measureText(String text, int size) {
		if (text == null)
			return 0;
		textPaint.setTextSize(size);
		return (int)(textPaint.measureText(text) + 0.5f);
	}
	
	public static void drawText(Canvas canvas, String text, int color, int size, int x, int y) {
		textPaint.setColor(color);
		textPaint.setTextSize(size);
		canvas.drawText(text, x, y, textPaint);
	}
	
	public static void drawEmptyListString(Canvas canvas) {
		textPaint.setColor(color_text_disabled);
		textPaint.setTextSize(_22sp);
		canvas.drawText(emptyListString, (UI.rect.right >> 1) - emptyListStringHalfWidth, (UI.rect.bottom >> 1) - (_18spBox >> 1) + _18spYinBox, textPaint);
	}
	
	public static void fillRect(Canvas canvas, int fillColor, Rect rect) {
		fillPaint.setColor(fillColor);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, fillPaint);
	}
	
	public static void fillRect(Canvas canvas, int fillColor, Rect rect, int insetX, int insetY) {
		fillPaint.setColor(fillColor);
		canvas.drawRect(rect.left + insetX, rect.top + insetY, rect.right - insetX, rect.bottom - insetY, fillPaint);
	}
	
	public static void fillRect(Canvas canvas, Shader shader, Rect rect) {
		fillPaint.setShader(shader);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, fillPaint);
		fillPaint.setShader(null);
	}
	
	public static void fillRect(Canvas canvas, Shader shader, Rect rect, int insetX, int insetY) {
		fillPaint.setShader(shader);
		canvas.drawRect(rect.left + insetX, rect.top + insetY, rect.right - insetX, rect.bottom - insetY, fillPaint);
		fillPaint.setShader(null);
	}
	
	public static void strokeRect(Canvas canvas, int strokeColor, Rect rect, int thickness) {
		fillPaint.setColor(strokeColor);
		final int l = rect.left, t = rect.top, r = rect.right, b = rect.bottom;
		canvas.drawRect(l, t, r, t + thickness, fillPaint);
		canvas.drawRect(l, b - thickness, r, b, fillPaint);
		canvas.drawRect(l, t + thickness, l + thickness, b - thickness, fillPaint);
		canvas.drawRect(r - thickness, t + thickness, r, b - thickness, fillPaint);
	}
	
	public static int getBorderColor(int state) {
		if ((state & STATE_PRESSED) != 0)
			return (((state & STATE_FOCUSED) != 0) ? color_focused_pressed_border : color_selected_pressed_border);
		if ((state & STATE_FOCUSED) != 0)
			return color_focused_border;
		if ((state & STATE_SELECTED) != 0)
			return color_selected_border;
		return 0;
	}
	
	public static void drawBgBorderless(Canvas canvas, int state, Rect rect) {
		if ((state & ~STATE_CURRENT) != 0) {
			if ((state & STATE_PRESSED) != 0)
				fillRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed : color_selected_pressed, rect);
			else if ((state & (STATE_SELECTED | STATE_FOCUSED)) != 0)
				fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect);
			else if ((state & STATE_MULTISELECTED) != 0)
				fillRect(canvas, color_selected_multi, rect);
		}
	}
	
	private static void drawDivider(Canvas canvas, Rect rect) {
		fillPaint.setColor(color_divider);
		final int top = rect.top;
		rect.top = rect.bottom - strokeSize;
		canvas.drawRect(rect, fillPaint);
		rect.top = top;
	}
	
	public static void drawBg(Canvas canvas, int state, Rect rect, boolean squareItem, boolean dividerAllowed) {
		if ((state & ~STATE_CURRENT) != 0) {
			if ((state & STATE_PRESSED) != 0) {
				strokeRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed_border : color_selected_pressed_border, rect, strokeSize);
				fillRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_pressed : color_selected_pressed, rect, strokeSize, strokeSize);
				return;
			} else if ((state & (STATE_SELECTED | STATE_FOCUSED)) != 0) {
				if (squareItem) {
					strokeRect(canvas, ((state & STATE_FOCUSED) != 0) ? color_focused_border : color_selected_border, rect, strokeSize);
					fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect, strokeSize, strokeSize);
				} else {
					fillRect(canvas, Gradient.getGradient((state & STATE_FOCUSED) != 0, false, rect.bottom), rect);
				}
			} else if ((state & STATE_MULTISELECTED) != 0) {
				fillRect(canvas, color_selected_multi, rect);
			}
		}
		if (!squareItem && dividerAllowed)
			drawDivider(canvas, rect);
	}
	
	public static int handleStateChanges(int state, boolean pressed, boolean focused, View view) {
		boolean r = false;
		final boolean op = ((state & UI.STATE_PRESSED) != 0), of = ((state & UI.STATE_FOCUSED) != 0);
		if (op != pressed) {
			if (pressed)
				state |= UI.STATE_PRESSED;
			else
				state &= ~UI.STATE_PRESSED;
			r = true;
		}
		if (of != focused) {
			if (focused)
				state |= UI.STATE_FOCUSED;
			else
				state &= ~UI.STATE_FOCUSED;
			r = true;
		}
		if (r)
			view.invalidate();
		return state;
	}
	
	public static void smallText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _14sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void smallTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _14sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void mediumText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _18sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void mediumTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _18sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void largeText(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _22sp);
		view.setTypeface(defaultTypeface);
	}
	
	public static void largeTextAndColor(TextView view) {
		view.setTextSize(TypedValue.COMPLEX_UNIT_PX, _22sp);
		view.setTextColor(colorState_text_static);
		view.setTypeface(defaultTypeface);
	}
	
	public static void prepareViewPaddingForLargeScreen(View view, int bottomPadding) {
		final int p = ((usableScreenWidth < usableScreenHeight) ? usableScreenWidth : usableScreenHeight) / (isLandscape ? 5 : 10);
		view.setPadding(p, thickDividerSize, p, bottomPadding);
	}
	
	public static void prepareViewPaddingForLargeScreen(View view, int topPadding, int bottomPadding) {
		final int p = ((usableScreenWidth < usableScreenHeight) ? usableScreenWidth : usableScreenHeight) / (isLandscape ? 5 : 10);
		view.setPadding(p, topPadding, p, bottomPadding);
	}
	
	public static void toast(Context context, Throwable ex) {
		String s = ex.getMessage();
		if (s != null && s.length() > 0)
			s = context.getText(R.string.error).toString() + " " + s;
		else
			s = context.getText(R.string.error).toString() + " " + ex.getClass().getName();
		toast(context, s);
	}
	
	public static void toast(Context context, int resId) {
		toast(context, context.getText(resId).toString());
	}
	
	@SuppressWarnings("deprecation")
	public static void prepareNotificationViewColors(TextView view) {
		view.setTextColor(UI.colorState_text_highlight_static);
		view.setBackgroundDrawable(new BorderDrawable(ColorUtils.blend(color_highlight, 0, 0.5f), color_highlight, true, true, true, true));
	}
	
	public static void toast(Context context, String text) {
		if (internalToast == null) {
			final Toast t = new Toast(context);
			final TextView v = new TextView(context);
			mediumText(v);
			prepareNotificationViewColors(v);
			v.setGravity(Gravity.CENTER);
			v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			v.setPadding(_8dp, _8dp, _8dp, _8dp);
			t.setView(v);
			t.setDuration(Toast.LENGTH_LONG);
			internalToast = t;
		}
		((TextView)internalToast.getView()).setText(text);
		internalToast.show();
	}
	
	public static void prepare(Menu menu) {
		final CustomContextMenu mnu = (CustomContextMenu)menu;
		try {
			mnu.setItemClassConstructor(BgButton.class.getConstructor(Context.class));
		} catch (NoSuchMethodException e) {
		}
		mnu.setBackground(new BorderDrawable(color_menu_border, color_menu, true, true, true, true));
		mnu.setPadding(0);
		mnu.setItemTextSizeInPixels(_22sp);
		mnu.setItemTextColor(colorState_text_menu_reactive);
		mnu.setItemGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
	}
	
	public static void separator(Menu menu, int groupId, int order) {
		((CustomContextMenu)menu).addSeparator(groupId, order, color_menu_border, strokeSize, _8dp, _2dp, _8dp, _2dp);		
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setNextFocusForwardId(View view, int nextFocusForwardId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			view.setNextFocusForwardId(nextFocusForwardId);
	}
	
	public static void prepareEdgeEffectColor(Context context) {
		//
		//:D amazing hack/workaround, as explained here:
		//
		//http://evendanan.net/android/branding/2013/12/09/branding-edge-effect/
		Drawable glow, edge;
		try {
			glow = context.getResources().getDrawable(context.getResources().getIdentifier("overscroll_glow", "drawable", "android"));
			if (glow != null)
				//the color is treated as SRC, and the bitmap is treated as DST
				glow.setColorFilter(glowFilter);
		} catch (Throwable ex) {
		}
		try {
			edge = context.getResources().getDrawable(context.getResources().getIdentifier("overscroll_edge", "drawable", "android"));
			if (edge != null)
				//hide the edge!!! ;)
				edge.setColorFilter(edgeFilter);
		} catch (Throwable ex) {
		}
	}
}
