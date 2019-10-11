package com.jriz.drawit;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.jriz.drawit.Structure.Object;
import com.jriz.drawit.Structure.Point;
import com.jriz.drawit.Structure.Polygon;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.UUID;

public class ConnectedToDevice extends AppCompatActivity {

    //TO MAKE TESTS
    Button btnPrint, btnDisconnect;

    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private StringBuilder DataStringIN;
    private ConnectedThread myConnectionBT;
    private UUID BTMODULEUUID;
    private int handlerState;
    private Object object;
    private boolean imprimiendo;
    private LinkedList<String> tramaList;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_to_device);
        handlerState = 0;
        DataStringIN = new StringBuilder();
        BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        btnPrint = findViewById(R.id.print);
        btnDisconnect = findViewById(R.id.disconnect);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPrint.setEnabled(false);
                setNewObject(Objects.requireNonNull(getIntent().getExtras().getString("objetojsonb")));
                startPrint();
                btnPrint.setEnabled(true);
            }
        });
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();
                        ;
                    }
                }
                finish();
            }
        });
    }
    void startPrint() {
        tramaList = new LinkedList<String>();
        String trama = "";
        int pointCount;
        for (Polygon polygon : object.polygonList) {
            pointCount = 0;
            for (int j = 0; j <= polygon.pointList.size() - 1; j++) {
                Point point = polygon.getPoint(j);
                //First trama is to go at the start of the polygon
                if (j == 0) {
                    trama = "l*" + point.toString();
                    tramaList.add(trama);
                    //Second trama is to start to draw the polygon
                    trama = "b";
                }
                //Each of this kind of tramas are to draw the polygon
                else if (pointCount == 5) {
                    tramaList.add(trama);
                    trama = "*" + point.toString();
                    pointCount = 1;
                }
                //Adding points to a trama
                else {
                    trama += "*" + point.toString();
                    pointCount++;
                }
            }
            tramaList.add(trama);
        }
        String cadena="";
        for(String t:tramaList){
            cadena+=t+"|";
        }
        Log.d("TAG","INICIO"+cadena+"FIN");
    }

    private Point convert(Point newActPoint) {
//        float newX = (((newActPoint.x * 100) / W) - 50) * 2;
//        float newY = (((newActPoint.x * 100) / H) - 50) * (-2);
//        act = new Point(newX, newY);
//        Point p = new Point(act.x - ant.x, act.y - ant.y);
//        ant = act;
//        //return p;
        return newActPoint;
    }

    void setNewObject(String objectJson) {
        if (objectJson.equals(Constants.NULL)) {
            object = new Object();
        } else {
            Gson gson = new Gson();
            this.object = gson.fromJson(objectJson, Object.class);
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String address = intent.getStringExtra(BluetoothDevices.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(this, Constants.SHOW_UPS, Toast.LENGTH_SHORT).show();
            }
        }
        myConnectionBT = new ConnectedThread(
                btSocket,
                new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        if (msg.what == handlerState) {
                            String readMessage = (String) msg.obj;
                            DataStringIN.append(readMessage);
                            int endOfLineIndex = DataStringIN.indexOf("#");
                            if (endOfLineIndex > 0) {
                                imprimiendo = false;
                                String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                                DataStringIN.delete(0, DataStringIN.length());
                                Toast.makeText(ConnectedToDevice.this, Boolean.toString(imprimiendo), Toast.LENGTH_SHORT).show();
                                if (dataInPrint.contains(",")) {
                                    //poner ancho y alto
                                }
                            }
                        }
                    }
                }
                , handlerState);
        myConnectionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            btSocket.close();
        } catch (IOException e2) {
            Toast.makeText(this, Constants.SHOW_UPS, Toast.LENGTH_SHORT).show();
        }
    }

    private void VerificarEstadoBT() {
        if (btAdapter == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
