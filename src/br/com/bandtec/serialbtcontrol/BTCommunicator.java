/**
 *   Copyright 2010, 2011, 2012 Guenther Hoelzl, Shawn Brown
 *
 *   This file is part of MINDdroid.
 *
 *   MINDdroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MINDdroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
**/

package br.com.bandtec.serialbtcontrol;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import br.com.bandtec.serialbtcontrol.activity.MainHandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public class BTCommunicator extends Thread {
	public static final int DO_ACTION = 52;	
	
	public static final int DISPLAY_TOAST = 1000;
	public static final int STATE_CONNECTED = 1001;
	public static final int STATE_CONNECTERROR = 1002;
	public static final int STATE_CONNECTERROR_PAIRING = 1022;
	public static final int MOTOR_STATE = 1003;
	public static final int STATE_RECEIVEERROR = 1004;
	public static final int STATE_SENDERROR = 1005;
	public static final int FIRMWARE_VERSION = 1006;
	public static final int FIND_FILES = 1007;
	public static final int START_PROGRAM = 1008;
	public static final int STOP_PROGRAM = 1009;
	public static final int GET_PROGRAM_NAME = 1010;
	public static final int PROGRAM_NAME = 1011;
	public static final int SAY_TEXT = 1030;
	public static final int VIBRATE_PHONE = 1031;
	
	public static final int NO_DELAY = 0;
	
	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private Resources mResources;
	private BluetoothAdapter btAdapter;
	private BluetoothSocket nxtBTsocket;
	private OutputStream nxtOutputStream;
	//private InputStream nxtInputStream;
	private boolean connected;
	
	private MainHandler.Callback uiCallback;
	private String mMACaddress;
	private BTConnectable myOwner;
	
	private byte[] returnMessage;
	
	private static BTCommunicator btCommunicator;
	
	public synchronized static BTCommunicator getBTCommunicator(BTConnectable myOwner, MainHandler.Callback uiCallback, BluetoothAdapter btAdapter, Resources resources) {
		if (btCommunicator != null)
			destroyBTCommunicatorNow();
		btCommunicator = new BTCommunicator(myOwner, uiCallback, btAdapter, resources);
		return btCommunicator;
	}
	
	public synchronized static void destroyBTCommunicatorNow() {
		if (btCommunicator != null) {
			if (handler.getLooper().getThread() != Thread.currentThread())
				throw new RuntimeException("destroyBTCommunicatorNow called from an invalid thread!");
			try {
				btCommunicator.destroyNXTconnection();
			} catch (IOException e) {
			}
			btCommunicator.myOwner = null;
			btCommunicator.uiCallback = null;
			btCommunicator.btAdapter = null;
			btCommunicator.mResources = null;
			btCommunicator.nxtBTsocket = null;
			btCommunicator.nxtOutputStream = null;
			//btCommunicator.nxtInputStream = null;
			btCommunicator = null;
		}
	}
	
	private BTCommunicator(BTConnectable myOwner, MainHandler.Callback uiCallback, BluetoothAdapter btAdapter, Resources resources) {
		this.myOwner = myOwner;
		this.uiCallback = uiCallback;
		this.btAdapter = btAdapter;
		this.mResources = resources;
	}
	
	public byte[] getReturnMessage() {
		return returnMessage;
	}
	
	public void setMACAddress(String mMACaddress) {
		this.mMACaddress = mMACaddress;
	}
	
	/**
	 * @return The current status of the connection
	 */			
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {
		try {
			createNXTconnection();
		} catch (IOException e) {
		}
		/*while (connected) {
			try {
				returnMessage = receiveMessage();
				if ((returnMessage.length >= 2) && ((returnMessage[0] == LCPMessage.REPLY_COMMAND) ||
					(returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY)))
					dispatchMessage(returnMessage);
			} catch (IOException e) {
				// Don't inform the user when connection is already closed
				if (connected)
					sendState(STATE_RECEIVEERROR);
				return;
			}
		}*/
	}
	
	/**
	 * Create a bluetooth connection with SerialPortServiceClass_UUID
	 * @see <a href=
	 *	  "http://lejos.sourceforge.net/forum/viewtopic.php?t=1991&highlight=android"
	 *	  />
	 * On error the method either sends a message to it's owner or creates an exception in the
	 * case of no message handler.
	 */
	private void createNXTconnection() throws IOException {
		try {
			BluetoothSocket nxtBTSocketTemporary;
			BluetoothDevice nxtDevice = null;
			nxtDevice = btAdapter.getRemoteDevice(mMACaddress);
			if (nxtDevice == null) {
				if (uiCallback == null)
					throw new IOException();
				else {
					sendToast(mResources.getString(R.string.no_paired_nxt));
					sendState(STATE_CONNECTERROR);
					return;
				}
			}
			nxtBTSocketTemporary = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
			try {
				nxtBTSocketTemporary.connect();
			} catch (IOException e) {  
				if (myOwner.isPairing()) {
					if (uiCallback != null) {
						sendToast(mResources.getString(R.string.pairing_message));
						sendState(STATE_CONNECTERROR_PAIRING);
					}
					else
						throw e;
					return;
				}
				// try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				try {
					Method mMethod = nxtDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					nxtBTSocketTemporary = (BluetoothSocket) mMethod.invoke(nxtDevice, Integer.valueOf(1));			
					nxtBTSocketTemporary.connect();
				} catch (Exception e1){
					if (uiCallback == null)
						throw new IOException();
					else
						sendState(STATE_CONNECTERROR);
					return;
				}
			}
			nxtBTsocket = nxtBTSocketTemporary;
			//nxtInputStream = nxtBTsocket.getInputStream();
			nxtOutputStream = nxtBTsocket.getOutputStream();
			connected = true;
		} catch (IOException e) {
			if (uiCallback == null)
				throw e;
			else {
				if (myOwner.isPairing())
					sendToast(mResources.getString(R.string.pairing_message));
				sendState(STATE_CONNECTERROR);
				return;
			}
		}
		// everything was OK
		if (uiCallback != null)
			sendState(STATE_CONNECTED);
	}
	
	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	private void destroyNXTconnection() throws IOException {
		try {
			if (nxtBTsocket != null) {
				connected = false;
				nxtBTsocket.close();
				nxtBTsocket = null;
			}
			//nxtInputStream = null;
			nxtOutputStream = null;
		} catch (IOException e) {
			if (uiCallback == null)
				throw e;
			else
				sendToast(mResources.getString(R.string.problem_at_closing));
		}
	}
	
	/**
	 * Sends a message on the opened OutputStream
	 * @param message, the message as a byte array
	 */
	private void sendMessage(byte message) throws IOException {
		if (nxtOutputStream == null)
			throw new IOException();
		// send message length
		//int messageLength = message.length;
		//nxtOutputStream.write(messageLength);
		//nxtOutputStream.write(messageLength >> 8);
		//nxtOutputStream.write(message, 0, message.length);
		nxtOutputStream.write(message);
	}  
	
	/**
	 * Receives a message on the opened InputStream
	 * @return the message
	 */				
	/*private byte[] receiveMessage() throws IOException {
		if (nxtInputStream == null)
			throw new IOException();
		int length = nxtInputStream.read();
		length = (nxtInputStream.read() << 8) | length;
		final byte[] returnMessage = new byte[length];
		nxtInputStream.read(returnMessage);
		return returnMessage;
	}*/
	
	/**
	 * Sends a message on the opened OutputStream. In case of 
	 * an error the state is sent to the handler.
	 * @param message, the message as a byte array
	 */
	private void sendMessageAndState(byte message) {
		if (nxtOutputStream == null)
			return;
		try {
			sendMessage(message);
		} catch (IOException e) {
			sendState(STATE_SENDERROR);
		}
	}
	
	/*private void dispatchMessage(byte[] message) {
		switch (message[1]) {
		case LCPMessage.GET_OUTPUT_STATE:
			if (message.length >= 25)
				sendState(MOTOR_STATE);
			break;
		case LCPMessage.GET_FIRMWARE_VERSION:
			if (message.length >= 7)
				sendState(FIRMWARE_VERSION);
			break;
		case LCPMessage.FIND_FIRST:
		case LCPMessage.FIND_NEXT:
			if (message.length >= 28) {
				// Success
				if (message[2] == 0)
					sendState(FIND_FILES);
			}
			break;
		case LCPMessage.GET_CURRENT_PROGRAM_NAME:
			if (message.length >= 23) {
				sendState(PROGRAM_NAME);
			}
			break;
		case LCPMessage.SAY_TEXT:
			if (message.length == 22) {
				sendState(SAY_TEXT);
			}
		case LCPMessage.VIBRATE_PHONE:
			if (message.length == 3) {
				sendState(VIBRATE_PHONE);
			}
			break;
		}
	}*/
	
	private void doAction(int actionNr) {
		sendMessageAndState((byte)(actionNr & 0xff));
	}
	
	private void sendToast(String toastText) {
		final Bundle myBundle = new Bundle();
		myBundle.putInt("message", DISPLAY_TOAST);
		myBundle.putString("toastText", toastText);
		MainHandler.sendMessage(uiCallback, myBundle);
	}
	
	private void sendState(int message) {
		final Bundle myBundle = new Bundle();
		myBundle.putInt("message", message);
		MainHandler.sendMessage(uiCallback, myBundle);
	}
	
	// Receive messages from the UI
	private static final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			final BTCommunicator btc = btCommunicator;
			if (btc == null)
				return;
			switch (myMessage.what) {
			case DO_ACTION:
				btc.doAction(myMessage.arg1);
				break;
			}
		}
	};
	
	public void sendMessage(int message, int value1) {
		handler.sendMessage(handler.obtainMessage(message, value1, 0));
	}
}
