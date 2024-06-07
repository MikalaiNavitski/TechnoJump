package com.mygdx.game.bluetooth;

import androidx.core.content.ContextCompat;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static BluetoothService instance;

    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private final Context context;

    private final ArrayList<String> messages = new ArrayList<>();

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
            return;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                this.socket = socket;
            } catch (IOException e) {
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
            this.socket = socket;
            return true;
        } catch (IOException e) {
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

    public void sendMessage(String message) {
        if (socket != null && socket.isConnected()) {
            try {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(message.getBytes());
                outputStream.flush();
            } catch (IOException ignored) {
            }
        }
    }

    public void closeAllConnections() {
        closeSocketQuietly(socket);
        socket = null;
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
        } catch (IOException ignored) {
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
                    synchronized (messages) {
                        messages.add(receivedMessage);
                    }
                }
            } catch (IOException ignored) {
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
                    serverSocket.close();
                    break;
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
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