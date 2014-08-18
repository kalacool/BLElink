package com.neat.blelink;

import java.util.LinkedList;
import java.util.UUID;

import com.neat.adapter.PeerAdapter;
import com.neat.bleclass.Peer;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	static PeerAdapter mPeerAdapter;
	BluetoothManager manager;
    BluetoothAdapter adapter;
    Handler mRssiTimerHandler =new Handler();
    public Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }
    Handler mHandler = new Handler(){
        int i = 0;
        @Override
        public void handleMessage(Message msg) {
        	mPeerAdapter.refresh();
            super.handleMessage(msg);
        }  
    };
    
    private BluetoothAdapter.LeScanCallback mDeviceFoundCallback = new BluetoothAdapter.LeScanCallback() {
        @Override // comes from: startLeScan
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        	Peer tmpPeer = new Peer(0,0,device,false,mActivity,mPeerAdapter);
        	tmpPeer.setHandler(mRssiTimerHandler);
        	mPeerAdapter.addItem(tmpPeer);
        	Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_example) {
        	
        	manager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = manager.getAdapter();
            mPeerAdapter.setBTAdapter(adapter);
            UUID[] mServicesToSearchDeviceFor = {
            		UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
            };
            adapter.startLeScan(mServicesToSearchDeviceFor, mDeviceFoundCallback);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView mlistview = (ListView) rootView.findViewById(R.id.listView1);
            LinkedList<Peer> peerList = new LinkedList<Peer>();
            mPeerAdapter = new PeerAdapter(
                    getActivity().getBaseContext(),
                    getActivity().getApplicationContext(),
                    peerList
                    );
            mlistview.setAdapter(mPeerAdapter);
            return rootView;
        }
    }

}
