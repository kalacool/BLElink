package com.neat.adapter;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;













import com.neat.bleclass.Peer;


import com.neat.blelink.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;

public class PeerAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private LinkedList<Peer> peerList = new LinkedList<Peer>();
	private PeerAdapter peerListAdapter;
	private BluetoothAdapter BTAdapter;
	private Context mContext;
	private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override // comes from: startLeScan
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	
        }
    };

	public PeerAdapter(final Context context,final Context appContext, final LinkedList<Peer> peerList) {
		this.peerList = peerList;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		peerListAdapter = this;
		mContext = appContext;
	}
	
	public void addItem(Peer newpeer){
		peerList.add(newpeer);
	}
	public void refresh(){
		super.notifyDataSetChanged();
	}
	
	public void setBTAdapter(BluetoothAdapter adapter){
		BTAdapter = adapter;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return peerList.size();
	}

	@Override
	public Peer getItem(int position) {
		// TODO Auto-generated method stub
		return peerList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null || view.getTag() == null) {
			view = inflater.inflate(R.layout.peer, null);
			final StatusViewHolder holder = new StatusViewHolder();
			holder.name = (TextView) view.findViewById(R.id.textView1);
			holder.rssi = (TextView) view.findViewById(R.id.rssi);
			holder.times = (TextView) view.findViewById(R.id.textView3);
			holder.value = (TextView) view.findViewById(R.id.textView2);
			holder.button = (Button) view.findViewById(R.id.button1);
			holder.progress = (ProgressBar) view.findViewById(R.id.progressBar1);

			view.setTag(holder);
		}
		final StatusViewHolder holder = (StatusViewHolder) view.getTag();
		holder.name.setText(peerList.get(position).DEVICE.getName());
		holder.rssi.setText(peerList.get(position).RSSI.toString());
		holder.value.setText(" "+String.valueOf(peerList.get(position).value));
		holder.times.setText(" "+String.valueOf(peerList.get(position).reciveTimes));
		final int id = peerList.get(position).ID;
		final int pos = position;
		final BluetoothDevice odevice = peerList.get(position).DEVICE;
		final BluetoothGattCallback Callback = peerList.get(position).mBleCallback;

		holder.button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(BTAdapter!=null){
					BTAdapter.stopLeScan(mDeviceFoundCallback);
					BluetoothDevice device= BTAdapter.getRemoteDevice(odevice.getAddress());
					
					if (device != null) {
						BluetoothGatt mBluetoothGatt = device.connectGatt(mContext, false,  Callback);
					}
				}

			}
		});


		
		return view;
	}
	

	protected final class StatusViewHolder {
		int position;
		TextView name;
		TextView rssi;
		TextView value;
		TextView times;
		Button button;
		ProgressBar progress;
	}

}
