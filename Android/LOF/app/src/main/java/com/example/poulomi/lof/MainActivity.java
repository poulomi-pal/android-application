package com.example.poulomi.lof;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import com.example.poulomi.lof.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;



public class  MainActivity extends AppCompatActivity {

    private static final String TAG = "LOF";

    Button btnOn, btnOff;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static SeekBar seek_bar;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//SPP UUID:The standard one
    private static String address = "20:15:05:27:68:06";//address of my HC-05 bluetooth module


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "In onCreate()");

        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        seekbarr();
        seek_bar.setMax(255);
        seek_bar.setKeyProgressIncrement(1);


        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("1");
                Toast msg = Toast.makeText(getBaseContext(), "You have clicked On", Toast.LENGTH_SHORT);
                msg.show();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("0");
                Toast msg = Toast.makeText(getBaseContext(), "You have clicked Off", Toast.LENGTH_SHORT);
                msg.show();

            }
        });

    }
    public void seekbarr(){

        seek_bar = (SeekBar) findViewById(R.id.seekBar);

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            try{
                                btSocket.getOutputStream().write(String.valueOf(progress).getBytes());

                            } catch (IOException e){
                            }
                            }




                }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


        @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...In onResume - Attempting client connect...");

        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }
        btAdapter.cancelDiscovery();

        Log.d(TAG, "...Connecting to Remote...");

        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override

    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    public void checkBTState() {
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is Enabled...");
            } else {
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message) {
        Toast msg = Toast.makeText(getBaseContext(), title + "-" + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Sending data: " + message + "...");

        try {

            outStream.write(msgBuffer);


        } catch (IOException e) {

            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("20:15:05:27:68:06"))
                msg = msg + ".\n\nUpdate your server address from 20:15:05:27:68:06 to the correct address in the java code";
            msg = msg + ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }
}


