package com.jasoncong.bluetooth;



import com.jasoncong.utils.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * BlueTooth & Sensor
 * 
 * 
 */

public class BlueTooth extends Activity {

	private static final int REQUEST_DISCOVERY = 0x1;
	// ��������ͨ�ŵ�UUID 
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// �Դ�������������
	private BluetoothAdapter bluetoothAdapter = null;
	// ɨ��õ��������豸
	private BluetoothDevice device = null;
	// ����ͨ��socket
	private BluetoothSocket btSocket = null;
	// �ֻ������
	private OutputStream outStream = null;
	private byte[] msgBuffer = null;
	// ����������
	private SensorManager sensorMgr = null;
	// ��������Ӧ
	private Sensor sensor = null;
	// �ֻ�x��y��z�᷽������
	private int x, y, z;
	
	//��ť
//	Button devBtn = null;
	Button leftBtn = null;
	Button rightBtn = null;
	Button quitBtn = null;
	Button playBtn = null;
	Button left_clickBtn = null;
	Button right_clickBtn = null;
	
	private MenuInflater myMenu;
	
	/**
	 * �����activity��һ�α�������ʱ����и÷���
	 **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* ʹ���򴰿�ȫ�� */
		// ����һ��û��title��ȫ������
		this.setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		// ����ȫ��
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ����־
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// ��bluetooth.xml�ļ����ַ��
		setContentView(R.layout.bluetooth);

		// Gravity sensing ��ȡ������
		sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// ��ȡ�ֻ�Ĭ���ϵ�����������
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// �����ֻ������豸
		bluetoothAction();

		
		
//		devBtn = (Button)findViewById(R.id.devBtn);
		leftBtn = (Button)findViewById(R.id.left);
		rightBtn = (Button)findViewById(R.id.right);
		playBtn = (Button)findViewById(R.id.play);
		quitBtn = (Button)findViewById(R.id.quit);		
		left_clickBtn = (Button)findViewById(R.id.left_click);
		right_clickBtn = (Button)findViewById(R.id.right_click);
		
//		devBtn.setOnClickListener(new button_listener());
		leftBtn.setOnClickListener(new button_listener());
		rightBtn.setOnClickListener(new button_listener());
		quitBtn.setOnClickListener(new button_listener());
		playBtn.setOnClickListener(new button_listener());
		right_clickBtn.setOnClickListener(new button_listener());
		left_clickBtn.setOnClickListener(new button_listener());
		
		myMenu = new MenuInflater(this);
	}

	/**button click listener */
	class button_listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			vibrator();
			
			int id = v.getId();
			switch(id){
//			case R.id.devBtn:
//				connectToDevice();
//				break;
			case R.id.left:
				sendData(Key_Events.VK_LEFT);
				break;
			case R.id.right:
				sendData(Key_Events.VK_RIGHT);
				break;
			case R.id.play:
				int[] array = {Key_Events.VK_SHIFT,Key_Events.VK_F5};
				sendData(array,array.length);
				break;
			case R.id.quit:
				sendData(Key_Events.VK_ESCAPE);
				break;
			case R.id.left_click:
				sendData(Key_Events.BUTTON1_MASK);
				break;
			case R.id.right_click:
				sendData(Key_Events.BUTTON3_MASK);
				break;
			default:
				break;
			}	
		}
		
	}
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		//��ʾ���ڶԻ���
		case R.id.select_dev:
			connectToDevice();
			break;
		}
		return true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu.inflate(R.xml.menu, menu);
		return true;
	}
	
	/**
	 * ������ʼ ��ѯ�ֻ��Ƿ�֧�����������֧�ֵĻ���������һ���� �鿴�����豸�Ƿ��Ѵ򿪣��������򿪡�
	 */
	public void bluetoothAction() {
		// �鿴�ֻ��Ƿ��������豸����
		if (hasAdapter(bluetoothAdapter)) {
			if (!bluetoothAdapter.isEnabled()) {
				// ������������
				bluetoothAdapter.enable();
			}
		} else {
			// ������ֹ
			this.finish();
		}
	}

	/**
	 * �鿴�ֻ��Ƿ��������豸����
	 * 
	 * @param ba
	 *            �����豸������
	 * @return boolean
	 */
	public boolean hasAdapter(BluetoothAdapter ba) {
		if (ba != null) {
			return true;
		}
		displayLongToast("���ֻ�û���������ܣ�");
		return false;
	}

	/**
	 * ����һ����ʱ�䵯������ʾ����toast
	 * 
	 * @param str
	 *            ��ʾ�ַ���
	 */
	public void displayLongToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}
	
	/**
	 * ����һ����ʱ�䵯������ʾ����toast
	 * 
	 * @param str
	 *            ��ʾ�ַ���
	 */
	public void displayShortToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	/**
	 * ���������������ѯ���������������豸����ѡ������
	 */
	public void connectToDevice() {
		if (bluetoothAdapter.isEnabled()) {
			// ������һ��activity---DiscoveryActivity���������ڲ�ѯ�������е������豸��
			Intent intent = new Intent(this, DiscoveryActivity.class);
			// ����������ʾ
			displayLongToast("��ѡ��һ�������豸�������ӣ�");

			// �ֻ���ʱ����DiscoveryActivity������档
			// ע�⣺����startActivityForResult�ص����ݷ��ص�ǰ�ĳ���
			// ��ϸ�ο���http://snmoney.blog.163.com/blog/static/440058201073025132670/
			this.startActivityForResult(intent, REQUEST_DISCOVERY);
		} else {
			this.finish();
		}
	}

	/**
	 * startActivityForResult��������DiscoveryActivity����д���
	 * ��ȡ����Ӧ��������ַ���ݺ󣬿�ʼ���Ǻ��ĵ����ݽ���
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);

		// ����ȷ���໥�ص�ʱ���ݵ�׼ȷ����
		if (requestCode != REQUEST_DISCOVERY) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
//		devBtn.setVisibility(View.INVISIBLE);
		
		// ��ȡ��DiscoveryActivity�����󴫹����������豸��ַ
		String addressStr = data.getStringExtra("address");
		// ���������豸��ַ�õ��������豸��������ɨ�赽�������豸Ŷ�������Լ��ģ�
		device = bluetoothAdapter.getRemoteDevice(addressStr);
		try {
			//����UUID����ͨ���׽���
			btSocket = device.createRfcommSocketToServiceRecord(uuid);
		} catch (Exception e) {
			displayLongToast("ͨ��ͨ������ʧ�ܣ�");
		}

		if (btSocket != null) {
			try {
				//��һ��һ��Ҫȷ�������ϣ���Ȼ�Ļ�����Ϳ����������ˡ�
				btSocket.connect();
				displayLongToast("ͨ��ͨ�����ӳɹ���");
			} catch (IOException ioe) {
				displayLongToast("ͨ��ͨ������ʧ�ܣ�");
				try {
					btSocket.close();
					displayLongToast("ͨ��ͨ���ѹرգ�");
				} catch (IOException ioe2) {
					displayLongToast("ͨ��ͨ����δ���ӣ��޷��رգ�");
				}
			} 
			try {
				// ��ȡ�����
				outStream = btSocket.getOutputStream();
				// �ֻ���������
			//	sendSensorData();
			} catch (IOException e) {
				displayLongToast("����������ʧ�ܣ�");
			} 
		}
	}

	/** send the single key data */
	public void sendData(int key){
		if(btSocket == null || outStream == null)
			return ;
		try {
			outStream.write((key+"#").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayShortToast("���ݷ���ʧ�ܣ�");
		}
	}
	
	/******* send the compose keys data ****/
	public void sendData(int key[] , int len){
		String tmp="";
		for(int i=0; i<len; i++)
			tmp+=key[i]+"#";
		
		if(btSocket == null || outStream == null)
			return ;
		try {
			outStream.write(tmp.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayShortToast("���ݷ���ʧ�ܣ�");
		}	
	}

	
	/**
	 * �������� �������ֻ�ͨ��������Ӧ����ȡ��������
	 */
	public void sendSensorData() {
		
		// ������Ӧ����
		SensorEventListener lsn = new SensorEventListener() {
			// ��д�ڲ�����������ȷ�ȷ����仯�Ǵ����÷�����
			@Override
			public void onAccuracyChanged(Sensor s, int accuracy) {
				// TODO Auto-generated method stub
			}

			// ��д�ڲ������������ݷ����仯��ʱ�򴥷��÷�����
			@Override
			public void onSensorChanged(SensorEvent se) {
				// TODO Auto-generated method stub
				/**
				 * ���ֻ�����ͷ��������Ļ�����Լ�ʱ x=10,y=0,z=0; ���ֻ�������Ļ�����Լ�ʱ x=0,y=10,z=0;
				 * ���ֻ�ƽ����Ļ����ʱ x=0,y=0,z=10; �ɴ˿�֪���������ֻ���������Ļ�����Լ�ʱ���У� ˮƽ����X��
				 * ��ֱ����Y�� ��Ļ���Է������Z�� ����ɲο�������---SensorDemo
				 */
				x = (int)se.values[SensorManager.DATA_X];
				y = (int)se.values[SensorManager.DATA_Y];
				z = (int)se.values[SensorManager.DATA_Z];
				if ((y > 5 || y < -5 ) && z > 0) {
//					String str = String.valueOf(x).concat(String.valueOf(y)).concat(String.valueOf(z));
					String str = "x" + String.valueOf(x) + "y" + String.valueOf(y) + "z" + String.valueOf(z) + "/";
					//String str = "hello";
					msgBuffer = str.getBytes();
					try {
						//System.out.println("x=" + x + " y =" + y + " z =" + z);
						outStream.write(msgBuffer);
					} catch (IOException e) {
						displayShortToast("���ݷ���ʧ�ܣ�");
					}
				}
				
//				if (y > 5 || y < -5) {
//					DataModel dataModel=new DataModel(x,y,z);
//					try {
//						System.out.println("x=" + x + " y =" + y + " z =" + z);
//						msgBuffer = dataModel.convertSelfToByteArray();
//						System.out.println("--------"+msgBuffer.length);
//						outStream.write(msgBuffer);
//					} catch (IOException e) {
//						Log.e("BlueTooth",e.getMessage());
//						e.printStackTrace();
//						displayShortToast("���ݷ���ʧ�ܣ�");
//					}
//				}
			}
		};
		

		// ������ע��������Ӧ��������android��һЩ�������ֺܴ�һ���ֶ�Ҫ��ô�ɵġ�
		// ����Ҫע�⡣����������飬�ڶ����򿪵�ʱ����ʵ��ҲҪע��Ȩ�޵ġ���AndroidManifest.xml�ļ���
		sensorMgr
				.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	
    /*
     * �ֻ���
     */
    public void vibrator(){
    	Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    	vibrator.vibrate(100);
    }
	
	
	
	// ////////////////////�������˳������һЩ���������غ��Ĺ��ܵ���/////////////////////////////////
	/**
	 * ��д������������ؼ���ȷ���Ƿ��˳�����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Builder alertDialog = new AlertDialog.Builder(this);
			// ���õ������ͼ��
			alertDialog.setIcon(R.drawable.quit);
			// ���õ������title
			alertDialog.setTitle(R.string.prompt);
			// ���õ��������ʾ��Ϣ
			alertDialog.setMessage(R.string.quit_msg);
			// ���õ�����ȷ�ϼ������¼�
			alertDialog.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// TODO Auto-generated method stub
							try {
								outStream.write("QUIT".getBytes());
								btSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finish(); 
						}
					});
			// ���õ�����ȡ���������¼��������κβ�����
			alertDialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
			// ��ʾ������
			alertDialog.show();
			return true;
		} else {
			// �������Ĳ��Ƿ��ؼ���ť����ô����ʲô��������ʲô������
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * ��д�����������̣߳��˳�ϵͳ��
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}





}