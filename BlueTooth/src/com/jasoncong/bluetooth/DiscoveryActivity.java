package com.jasoncong.bluetooth;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * ���༯��ListActivity,��Ҫ��ɨ�貢��ʾ���������е������豸 ������ظ�BlueTooth
 * 
 */
public class DiscoveryActivity extends ListActivity {

	// ��ȡ�ֻ�Ĭ���ϵ�����������
	private BluetoothAdapter blueToothAdapter = BluetoothAdapter
			.getDefaultAdapter();

	// ��ÿһ��HashMap��ֵ�Ե������豸��Ϣ��ŵ�list�����в����ļ����ַ��ķ�ʽ���ֳ���
	private ArrayList<HashMap<String, String>> list = null;
	// ���������������ɨ�赽�������豸��list
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/* ʹ���򴰿�ȫ�� */
		// ����һ��û��title��ȫ������
		this.setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		// ����ȫ��
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ����־
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// ��discovery.xml�ļ����ַ��
		setContentView(R.layout.discovery);

		list = new ArrayList<HashMap<String, String>>();

		// ��ɨ�趼��ÿһ�������豸�ŵ�list�У������ָ��ͻ���
		showDevices();
	}

	/**
	 * ��ɨ�趼��ÿһ�������豸�ŵ�list�У������ָ��ͻ��ˡ�
	 */
	public void showDevices() {
		// ��ȡ��������Ե������豸
		Set<BluetoothDevice> devices = blueToothAdapter.getBondedDevices();

		if (devices.size() > 0) {
			Iterator<BluetoothDevice> it = devices.iterator();
			BluetoothDevice bluetoothDevice = null;
			HashMap<String, String> map = new HashMap<String, String>();
			while (it.hasNext()) {
				bluetoothDevice = it.next();
				// ��ÿһ����ȡ���������豸�����ƺ͵�ַ��ŵ�HashMap�����У����磺xx:xx:xx:xx:xx: royal
				map.put("address", bluetoothDevice.getAddress());
				map.put("name", bluetoothDevice.getName());
				// ��list���ڴ�ų��ֵ������豸����ŵ���ÿ���豸��map
				list.add(map);
				// ��list���ڴ�ŵ���������ÿһ�������豸����
				_devices.add(bluetoothDevice);
			}

			// ����һ���򵥵��Զ��岼�ַ�񣬸�������������ȷ�����Ӧ�������googleһ��SimpleAdapter�Ͳο�һЩ����
			SimpleAdapter listAdapter = new SimpleAdapter(this, list,
					R.layout.device, new String[] { "address", "name" },
					new int[] { R.id.address, R.id.name });
			this.setListAdapter(listAdapter);
		}
	}

	/**
	 * list�������¼� ���豸ɨ����ʾ��ɺ󣬿�ѡ������Ӧ���豸�������ӡ�
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent result = new Intent();
		String addressStr = _devices.get(position).getAddress();
		//��ַֻȡ��17λ����ȻaddressStr��address��һ�� xx:xx:xx:xx:xx:xx
		String address = addressStr.substring(addressStr.length() - 17);
		
		result.putExtra("address", address);
		// ������ǻش������ˣ�����ַ���ظ�BlueTooth---activity
		// �����resultCode��RESULT_OK��BlueTooth---activity����onActivityResult���Ӧ��resultCodeҲӦ����RESULT_OK
		//ֻ��resultCodeֵ��ƥ�䣬����ȷ��result���ݻص�������
		setResult(RESULT_OK, result);
		// һ��Ҫfinish��ֻ��finish����ܽ����ݴ���BlueTooth---activity
		// ����onActivityResult������
		finish();
	}

}

