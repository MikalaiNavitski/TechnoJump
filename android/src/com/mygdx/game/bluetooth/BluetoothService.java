package com.mygdx.game.bluetooth;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.mygdx.game.AndroidLauncher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    private static final String TAG = "BluetoothService";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothService instance;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice connectedDevice;
    private BluetoothSocket socket;
    private List<Thread> threads = new ArrayList<Thread>();
    private Context context;

    private final ArrayList<String> messages = new ArrayList<String>();

    private static final int REQUEST_DISCOVERABLE_BT = 1;

    private BluetoothService(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public static BluetoothService getInstance(Context context) {
        if (instance == null) {
            instance = new BluetoothService(context);
        }
        return instance;
    }

    public static BluetoothService getInstance() {
        return instance;
    }


    public Set<BluetoothDevice> getConnectedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    public void connectToAllBondedDevices() {
        if (!hasBluetoothPermissions()) {
            Log.e(TAG, "Missing Bluetooth permissions");
            return;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                connectedDevice = device;
                this.socket = socket;
                Log.i(TAG, "Connected to device: " + device.getName() + " [" + device.getAddress() + "]");
            } catch (IOException e) {
                Log.e(TAG, "Error while connecting to device: " + device.getName() + " [" + device.getAddress() + "]", e);
                if (socket != null) {
                    closeSocketQuietly(socket);
                }
            }
        }
    }

    public boolean connectToDevice(BluetoothDevice device){
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothAdapter.cancelDiscovery();
            socket.connect();
            connectedDevice = device;
            this.socket = socket;
            Log.i(TAG, "Connected to device: " + device.getName() + " [" + device.getAddress() + "]");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error while connecting to device: " + device.getName() + " [" + device.getAddress() + "]", e);
            if (socket != null) {
                closeSocketQuietly(socket);
            }
            return false;
        }
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void sendMessageToAll(String message) {
        if (socket != null && socket.isConnected()) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                outputStream.flush();
                Log.i(TAG, "Sent message: " + message + " to device: " + socket.getRemoteDevice().getName());
            } catch (IOException e) {
                Log.e(TAG, "Error while sending message to device: " + socket.getRemoteDevice().getName(), e);
            }
        } else {
            Log.w(TAG, "Socket is null or not connected: " + (socket != null ? socket.getRemoteDevice().getName() : "unknown device"));
        }
    }

    public void closeAllConnections() {
        closeSocketQuietly(socket);
        socket = null;
        connectedDevice = null;
    }

    public ArrayList<String> getLastMessages(){
        ArrayList<String> result;
        synchronized (messages) {
            result = new ArrayList<>(messages);
            messages.clear();
        }
        return result;
    }

    private void closeSocketQuietly(BluetoothSocket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while closing socket", e);
        }
    }

    public void listenForMessages(BluetoothSocket socket) {
        Thread messageThread = new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytes;
                while ((bytes = inputStream.read(buffer)) != -1) {
                    String receivedMessage = new String(buffer, 0, bytes);
                    Log.i(TAG, "Received message: " + receivedMessage);
                    synchronized (messages) {
                        messages.add(receivedMessage);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while receiving message", e);
            }
        });
        messageThread.start();
    }

    public void acceptConnections() {
        BluetoothServerSocket serverSocket = null;
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MyService", MY_UUID);
            while (true) {
                BluetoothSocket socket = serverSocket.accept();
                if (socket != null) {
                    listenForMessages(socket);
                    serverSocket.close(); // Close the server socket if you are accepting only one connection
                    break;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while accepting connection", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error while closing server socket", e);
                }
            }
        }
    }

    public boolean checkSocketConnection() {
        if (socket != null && !socket.isConnected()) {
            socket = null;
        }
        return socket != null;
    }

    public UUID getMyUuid(){
        return MY_UUID;
    }
}