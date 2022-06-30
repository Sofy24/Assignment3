package com.example.gardenapp;



import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.gardenapp.exceptions.BluetoothDeviceNotFound;
import com.example.gardenapp.netutils.Http;
import com.example.gardenapp.utils.C;
import com.google.android.material.slider.Slider;

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
    private Boolean isServoOn = false;
    private Button led1Button;
    private Button led2Button;
    private Button irrigationButton;
    private Slider led3Slider;
    private Slider led4Slider;
    private Slider irrigationSlider;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        led1Button = findViewById(R.id.led1_button);
        led2Button = findViewById(R.id.led2_button);
        irrigationButton = findViewById(R.id.irrigation_button);
        led4Slider = findViewById(R.id.led4_slider);
        irrigationSlider = findViewById(R.id.irrigation_slider);
        led3Slider = findViewById(R.id.led3_slider);
        led2Button.setEnabled(false);
        led1Button.setEnabled(false);
        irrigationButton.setEnabled(false);
        led3Slider.setEnabled(false);
        led4Slider.setEnabled(false);
        irrigationSlider.setEnabled(false);

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


    private void initUIBluetooth() { //connectBtn
        findViewById(R.id.manual_control_button).setOnClickListener(l -> {
            l.setEnabled(false);
            led1Button.setEnabled(true);
            led2Button.setEnabled(true);
            irrigationButton.setEnabled(true);
            led3Slider.setEnabled(true);
            led4Slider.setEnabled(true);
            irrigationSlider.setEnabled(true);

            try {
                makePost("MODE_MANUAL");
                connectToBTServer();
            } catch (BluetoothDeviceNotFound bluetoothDeviceNotFound) {
                Toast.makeText(this, "Bluetooth device not found !", Toast.LENGTH_LONG)
                        .show();
                bluetoothDeviceNotFound.printStackTrace();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                l.setEnabled(true);
            }
        });

        /*findViewById(R.id.sendBtn).setOnClickListener(l -> {
            String message = ((EditText)findViewById(R.id.editText)).getText().toString();
            btChannel.sendMessage(message);
            ((EditText)findViewById(R.id.editText)).setText("");
        });*/

        led1Button.setOnClickListener(l -> {
            String message = "L_1";
            if(btChannel != null){
                btChannel.sendMessage(message);
            }
        });

        led2Button.setOnClickListener(l -> {
            String message = "L_2";
            if(btChannel != null){
                btChannel.sendMessage(message);
            }
        });

        led3Slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String message = "F_1_";
                System.out.println("valueeeee "+ (int) value);
                System.out.println(message + String.valueOf((int) value));
                if(btChannel != null){
                    btChannel.sendMessage(message + String.valueOf((int) value));
                }
            }
        });

        led4Slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String message = "F_2_";
                System.out.println("valueeeee "+ (int) value);
                System.out.println(message + String.valueOf((int) value));
                if(btChannel != null){
                    btChannel.sendMessage(message + String.valueOf((int) value));
                }
            }
        });

    irrigationButton.setOnClickListener(l -> {
        if(isServoOn){
            String message = "S_OFF";
            if(btChannel != null){
                btChannel.sendMessage(message);
                isServoOn = false;
            }
        } else {
            String message = "S_ON";
            if (btChannel != null) {
                btChannel.sendMessage(message);
                isServoOn = true;
            }

        }
    });

        irrigationSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                String message = "S_";
                System.out.println("valueeeee "+ (int) value);
                System.out.println(message + String.valueOf((int) value));
                if(btChannel != null && isServoOn){
                    btChannel.sendMessage(message + String.valueOf((int) value));
                }
            }
        });

        ImageButton alarmButton = findViewById(R.id.alarm);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    makePost("ALARM_OFF");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });




    }

    private void initUI() {
        //findViewById(R.id.connectionStatusBtn).setOnClickListener(v -> {
        final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        if(activeNetwork.isConnectedOrConnecting()){
            Toast.makeText(MainActivity.this, "You're connect", Toast.LENGTH_LONG)
                    .show();
            //((TextView)findViewById(R.id.statusLabel2)).setText(R.string.network_is_connected);
        }
       // });

        //DECOMMENT FOR DEBUG
        /*findViewById(R.id.getBtn).setOnClickListener(v -> {
            tryHttpGet();
        });

        findViewById(R.id.postBtn).setOnClickListener(v -> {

            try {
                makePost("MODE_MANUAL");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });*/

    }

    /*private void tryHttpGet(){
        final String url = "http://192.168.43.20:8000/api/";//"https://dummy.restapiexample.com/api/v1/employee/1";

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
    }*/


    private void makePost(String command) throws IOException, JSONException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.43.157:8000/api/prova");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("command", command); //"MODE_MANUAL"
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
                    if(conn.getResponseMessage() == "MODE_MANUAL"){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                led1Button.setEnabled(true);
                                led2Button.setEnabled(true);
                                irrigationButton.setEnabled(true);
                                led3Slider.setEnabled(true);
                                led4Slider.setEnabled(true);
                                irrigationSlider.setEnabled(true);
                            }
                        });
                    } else if (conn.getResponseMessage() == "MODE_ALARM"){
                        Toast.makeText(MainActivity.this, "The allarm is on", Toast.LENGTH_LONG)
                                .show();
                    }

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
        if (btChannel != null){
            btChannel.close();
        }
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
                System.out.println(serverDevice.getName() + "connected");

                /*((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                        "Status : connected to server on device %s",
                        serverDevice.getName()
                ));*/

                findViewById(R.id.manual_control_button).setEnabled(false);

                btChannel = channel;
                btChannel.registerListener(new RealBluetoothChannel.Listener() {
                    @Override
                    public void onMessageReceived(String receivedMessage) {
                        /*((TextView) findViewById(R.id.chatLabel)).append(String.format(
                                "> [RECEIVED from %s] %s\n",
                                btChannel.getRemoteDeviceName(),
                                receivedMessage
                        ));*/
                    }

                    @Override
                    public void onMessageSent(String sentMessage) {
                        /*((TextView) findViewById(R.id.chatLabel)).append(String.format(
                                "> [SENT to %s] %s\n",
                                btChannel.getRemoteDeviceName(),
                                sentMessage
                        ));*/
                    }
                });
            }

            @Override
            public void onConnectionCanceled() {
                /*((TextView) findViewById(R.id.statusLabel)).setText(String.format(
                        "Status : unable to connect, device %s not found!",
                        C.bluetooth.BT_DEVICE_ACTING_AS_SERVER_NAME
                ));*/
                System.out.println("unable to connect, device not found!");
            }
        }).execute();
    }
}
