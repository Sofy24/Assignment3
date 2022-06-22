package com.example.gardenapp;



import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.gardenapp.netutils.Http;
import com.example.gardenapp.utils.C;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {
    private BluetoothChannel btChannel;
    private HashMap<String, String> param = new HashMap<>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_marfoglia);

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if(btAdapter != null && !btAdapter.isEnabled()) {
            startActivityForResult(
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                    C.bluetooth.ENABLE_BT_REQUEST
            );
        }

        param.put("ciao", "d");
        initUIBluetooth();
        initUI();
    }

    private void initUIBluetooth() {
        findViewById(R.id.connectBtn).setOnClickListener(l -> {
            l.setEnabled(false);
            try {
                connectToBTServer();
            } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                Toast.makeText(this, "Bluetooth device not found !", Toast.LENGTH_LONG)
                        .show();
                bluetoothDeviceNotFound.printStackTrace();
            } finally {
                l.setEnabled(true);
            }
        });

        findViewById(R.id.sendBtn).setOnClickListener(l -> {
            String message = ((EditText)findViewById(R.id.editText)).getText().toString();
            btChannel.sendMessage(message);
            ((EditText)findViewById(R.id.editText)).setText("");
        });
    }

    private void initUI() {
        findViewById(R.id.connectionStatusBtn).setOnClickListener(v -> {
            final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            final NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

            if(activeNetwork.isConnectedOrConnecting()){
                ((TextView)findViewById(R.id.statusLabel2)).setText("Network is connected");
            }
        });

        findViewById(R.id.getBtn).setOnClickListener(v -> {
            tryHttpGet();
        });

        findViewById(R.id.postBtn).setOnClickListener(v -> {

            try {
                makePost();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }

    private void tryHttpGet(){
        final String url = "http://192.168.1.80:8000/api/";//"https://dummy.restapiexample.com/api/v1/employee/1";

        Http.get(url, response -> {
            if(response.code() == HttpURLConnection.HTTP_OK) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ((TextView) findViewById(R.id.resLabel)).setText(response.contentAsString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        });
    }

    /*private void tryHttpPost() throws JSONException {

        final String url = "http://localhost:8000/api/prova";//"http://dummy.restapiexample.com/api/v1/create";
        final String content = new JSONObject()
                .put("name","test" + new Random().nextLong())
                .put("salary","1000")
                .put("age","30").toString();

        Http.post(url, content.getBytes(), response -> {
            System.out.println("response-code"+response.code());
            if(response.code() == HttpURLConnection.HTTP_OK){
                try {
                    ((TextView)findViewById(R.id.resLabel)).setText(response.contentAsString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    private void makePost() throws IOException, JSONException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.1.80:8000/api/prova");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("timestamp", 1488873360);
                    jsonParam.put("uname", "message.getUser()");
                    jsonParam.put("message", "message.getMessage()");
                    jsonParam.put("latitude", 0D);
                    jsonParam.put("longitude", 0D);

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        btChannel.close();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_OK) {
            Log.d(C.APP_LOG_TAG, "Bluetooth enabled!");
        }

        if (requestCode == C.bluetooth.ENABLE_BT_REQUEST && resultCode == RESULT_CANCELED) {
            Log.d(C.APP_LOG_TAG, "Bluetooth not enabled!");
        }
    }

    private void connectToBTServer() throws BluetoothDeviceNotFound {
        final BluetoothDevice serverDevice = BluetoothUtils
                .getPairedDeviceByName(C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME);
        // !!! Choose the right UUID value
        final UUID uuid = BluetoothUtils.getEmbeddedDeviceDefaultUuid();
//        final UUID uuid = BluetoothUtils.generateUuidFromString(C.bluetooth.BT_SERVER_UUID);

        new ConnectToBluetoothServerTask(serverDevice, uuid, new ConnectionTask.EventListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onConnectionActive(final BluetoothChannel channel) {
                ((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                        "Status : connected to server on device %s",
                        serverDevice.getName()
                ));

                findViewById(R.id.connectBtn).setEnabled(false);

                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        ((TextView) findViewById(R.id.chatLabel)).append(String.format(
                                "> [RECEIVED from %s] %s\n",
                                btChannel.getRemoteDeviceName(),
                                receivedMessage
                        ));
                    }

                    @Override
                    public void onMessageSent(String sentMessage) {
                        ((TextView) findViewById(R.id.chatLabel)).append(String.format(
                                "> [SENT to %s] %s\n",
                                btChannel.getRemoteDeviceName(),
                                sentMessage
                        ));
                    }
                });
            }

            @Override
            public void onConnectionCanceled() {
                ((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                        "Status : unable to connect, device %s not found!",
                        C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME
                ));
            }
        }).execute();
    }
}
