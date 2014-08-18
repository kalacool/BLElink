package com.neat.bleclass;

import java.util.LinkedList;


import java.util.Random;
import java.util.UUID;

import com.neat.adapter.PeerAdapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class Peer {
	public final Integer ID;
	public Integer RSSI;
	public Integer reciveTimes= 0;
	public final BluetoothDevice DEVICE;
	public final Boolean BUTTONSTATE;
	public final PeerAdapter mPeerAdapter;
	public final Activity mActivity;
	public float result = 0;
	public float value = 0 ;
	Handler mRssiTimerHandler;
	
	public Peer(Integer ID,Integer RSSI,BluetoothDevice DEVICE,Boolean BUTTONSTATE,Activity mActivity,PeerAdapter mPeerAdapter){
		this.ID=ID;
		this.RSSI = RSSI;
		this.BUTTONSTATE=BUTTONSTATE;
		this.DEVICE=DEVICE;
		this.mActivity=mActivity;		
		this.mPeerAdapter=mPeerAdapter;
		
	}
	
	public void setHandler(Handler handler){
		mRssiTimerHandler = handler;
	}
	private void readValue(final BluetoothGatt gatt,final int delay, final BluetoothGattCharacteristic characteristic ){
		
		mRssiTimerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                

                // request RSSI value
            	gatt.readRemoteRssi(); // callback: onReadRemoteRssi
//            	String result = String.valueOf( gatt.readCharacteristic(characteristic) );
//            	Log.d("peer"+RSSI.toString(), result );
                // and call it once more in the future
                readValue(gatt,delay,characteristic);
            }
        }, delay);
		
	}
	public final BluetoothGattCallback mBleCallback = new BluetoothGattCallback(){
		
		@Override // comes from: connectGatt
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
			//Log.d("peer"+RSSI.toString(), "connect state change");
			
			if (newState == BluetoothProfile.STATE_CONNECTED) {
                
                gatt.discoverServices(); // callback: onServicesDiscovered
                reciveTimes = 0;

            }
		}
		@Override // comes from: discoverServices()
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
			
			
			if (status == BluetoothGatt.GATT_SUCCESS){
				
//				UUID serviceUUID = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
//				BluetoothGattService tempService = gatt.getService(serviceUUID);
//				BluetoothGattCharacteristic tempChar = tempService.getCharacteristic(UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb"));
//				if(tempService==null || tempChar==null){
				UUID serviceUUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
				BluetoothGattService	tempService = gatt.getService(serviceUUID);
				BluetoothGattCharacteristic	tempChar = tempService.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
				//}
				boolean result = gatt.setCharacteristicNotification(tempChar, true);
				
				

				BluetoothGattDescriptor descriptor = tempChar.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
				if(UUID.fromString("00001809-0000-1000-8000-00805f9b34fb").equals(serviceUUID)){
					byte[] val =  BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
					descriptor.setValue(val);
				}else if(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb").equals(serviceUUID)){
					byte[] val =  BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
					descriptor.setValue(val);
				}
				
				
				gatt.writeDescriptor(descriptor);
				//gatt.readCharacteristic(tempChar);
				//setCharacteristics(tempService, tempChar);
				
				int delay = new Random().nextInt(500)+1000;
                readValue(gatt,delay,tempChar);
			}
		}
		@Override // comes from: setCharacteristicNotification
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
			
			//Log.d("peer"+ID.toString(), "Characteristic Change");
			//value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
			value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
			reciveTimes++;
			mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	mPeerAdapter.refresh();
                }
            });
        
           
        }
		@Override // comes from: readRemoteRssi()
	    public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
			RSSI = rssi;
			mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	mPeerAdapter.refresh();
                }
            });
		}
		@Override // comes from: writeDescriptor
        public void onDescriptorWrite(BluetoothGatt gatt,
                BluetoothGattDescriptor descriptor,
                int status)
        {
			
        }
		
		@Override // comes from: readCharacteristic
        public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status)
        {
			value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);
			
			mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                	mPeerAdapter.refresh();
                }
            });
			
        }
	};

}
