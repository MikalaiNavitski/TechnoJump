package com.mygdx.game;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.bluetooth.BluetoothService;

import java.util.Arrays;

public class AndroidLauncher extends AndroidApplication {

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_BLUETOOTH_PERMISSIONS = 2;

	private BluetoothAdapter bluetoothAdapter;
	private static final int REQUEST_DISCOVERABLE_BT = 1;
	private BluetoothService bluetoothService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		bluetoothService = BluetoothService.getInstance(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = true;
		config.useCompass = false;

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (bluetoothAdapter == null) {
			return;
		}

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
		}

		// Start the server thread to listen for incoming connections
		new Thread(() -> {
			bluetoothService.acceptConnections();
		}).start();

		MyMobileGame2 game;

		initialize(game = new MyMobileGame2(), config);
		game.setAndroidLauncher(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode >= REQUEST_ENABLE_BT) {
			System.out.println("!!!!!!!!!!!" + resultCode);
			if (resultCode >= 0) {
				checkPermissionsAndConnect();
			} else {
				Toast.makeText(this, "Bluetooth must be enabled to proceed.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void checkPermissionsAndConnect() {
		if (hasBluetoothPermissions()) {
			return;
		} else {
			requestBluetoothPermissions();
		}
	}

	private boolean hasBluetoothPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
		} else {
			return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
					&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
		}
	}

	private void requestBluetoothPermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
					REQUEST_BLUETOOTH_PERMISSIONS
			);
		} else {
			ActivityCompat.requestPermissions(
					this,
					new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_BLUETOOTH_PERMISSIONS
			);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
			System.out.println(Arrays.toString(permissions));
			if (grantResults.length == 0) {
				return;
			}
			if (allPermissionsGranted(grantResults)) {
			} else {
				Toast.makeText(this, "Bluetooth permissions are required to proceed.", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private boolean allPermissionsGranted(int[] grantResults) {
		for (int result : grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	public void bluetoothConnectionProlong(){
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
		new Thread(() -> {
			bluetoothService.acceptConnections();
		}).start();
	}
}