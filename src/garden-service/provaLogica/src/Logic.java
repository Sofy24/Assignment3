import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jdk.jfr.ContentType;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class Logic {

    public static CommChannel channel;

    public static void main(String[] args) throws Exception {

        //channel = new SerialCommChannel("COM3",9600);

        System.out.println("Waiting Arduino for rebooting...");
        //Thread.sleep(4000);
        System.out.println("Ready.");
        sendPost3();
        //sendGet();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/prova", new MyHandlerPost());
        server.createContext("/api", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    /*public static void sendPost2() throws IOException {
        URL url = new URL("http://192.168.43.157/Assignment3/src/garden-dashboard/GardenDashboard.php");
        String result = "";
        String data = "fName=" + URLEncoder.encode("Atli", "UTF-8");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            // Send the POST data
            DataOutputStream dataOut = new DataOutputStream(
                    connection.getOutputStream());
            dataOut.writeBytes(data);
            dataOut.flush();
            dataOut.close();

            BufferedReader in = null;
            try {
                String line;
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    result += line;
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }

            String g;
            while ((g = in.readLine()) != null) {
                result += g;
            }
            in.close();

        } finally {
            connection.disconnect();
            System.out.println(result);
        }

    }*/


    public static void sendPost3(){
        try{
            //String erg = JOptionPane.showInputDialog("2+3");
            BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://192.168.43.220/Assignment3/src/garden-dashboard/GardenDashboard.php?test="+"7").openStream()));
            String b = br.readLine();
            System.out.println(b); // print the string b
            if(b.equalsIgnoreCase("true")){
                //System.out.println("It is true");
            }
            else {
                //System.out.println("False");
            }

        } catch(IOException e){
            System.out.println("error");
        }
    }


    

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("received get");
            String response = "This is the response get";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class MyHandlerPost implements HttpHandler{

        public String mode = "MODE_AUTO";
        public Boolean alarm = false;
        public Boolean irrigation;
        public int luminosity;
        public  int temperature;

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream stream = t.getRequestBody();
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
            for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }


            String command = out.toString()
                    .replace("{","")
                    .replace("}","")
                    .split(",")[0].split(":")[1];

            if (command.equals("MODE_MANUAL")){
                mode = "MODE_MANUAL";
            }
            if (command.equals("MODE_AUTO")){
                mode = "MODE_AUTO";
            }
            if (command.equals("ALARM_OFF")){
                alarm = false;
            }

            if (mode.equals("MODE_AUTO")){//esp command
                if (command.contains("_")) {
                    String commands = "";
                    luminosity = Integer.parseInt(command.split("_")[0].replace(" ",""));
                    temperature = Integer.parseInt(command.split("_")[1]);

                    if (temperature == 5 && !irrigation ){
                        alarm = true;
                    }
                    //added

                    /*if (luminosity < 2){
                        channel.sendMsg("S_ON");
                        commands = commands.concat("S_ON");
                        irrigation = true;
                    } else {
                        channel.sendMsg("S_OFF");
                        commands = commands.concat("S_OFF");
                        irrigation = false;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }*/

//finished part1
                    if (luminosity < 5){
                        channel.sendMsg("L_1_ON");
                        commands = commands.concat("L_1_ON,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        channel.sendMsg("L_2_ON");
                        commands = commands.concat("L_2_ON,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        channel.sendMsg("L_1_OFF");
                        commands = commands.concat("L_1_OFF,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        channel.sendMsg("L_2_OFF");
                        commands = commands.concat("L_2_OFF,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(luminosity);
                    switch (luminosity){
                        case 0:
                            channel.sendMsg("F_3_4");
                            commands = commands.concat("F_1_4,");
                            //channel.sendMsg("F_2_4");
                            commands = commands.concat("F_2_4,");
                            break;
                        case 1:
                            channel.sendMsg("F_3_3");
                            commands = commands.concat("F_1_3,");
                            //channel.sendMsg("F_2_3");
                            commands = commands.concat("F_2_3,");
                            break;
                        case 2:
                            channel.sendMsg("F_3_2");
                            commands = commands.concat("F_1_2,");
                            commands = commands.concat("F_2_2,");
                            break;
                        case 3:
                            channel.sendMsg("F_3_1");
                            commands = commands.concat("F_1_1,");
                            //channel.sendMsg("F_2_1");
                            commands = commands.concat("F_2_1,");
                            break;
                        default:
                            channel.sendMsg("F_3_0");
                            commands = commands.concat("F_1_0,");
                            //channel.sendMsg("F_2_0");
                            commands = commands.concat("F_2_0,");

                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    channel.sendMsg("S_"+temperature);
                    commands = commands.concat("S_"+temperature+",");
                    try {
                        TimeUnit.MILLISECONDS.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//tolta
/*
                    if (!alarm) {
                        System.out.println(commands);
                        //channel.sendMsg(commands);
*/
//aggiunta
                    if (luminosity < 2){
                        //channel.sendMsg("S_ON");
                        commands = commands.concat("S_ON");
                        irrigation = true;
                    } else {
                        //channel.sendMsg("S_OFF");
                        commands = commands.concat("S_OFF");
                        irrigation = false;
//fine add2
/*
                    }
                    else {
                        System.out.println("ALARM");
                        channel.sendMsg("ALARM");
                    }
                    */
//agguiunta 3


                    if (!alarm) {
                        System.out.println(commands);
                        Logic.channel.sendMsg(commands);
                    }
                    else {
                        System.out.println("ALARM");
                        Logic.channel.sendMsg("ALARM");
                    }
//fine aggiunta
                }
            }



            //System.out.println("SEND DATA TO DASHBOARD: temp-" + temperature + " lum-" + luminosity + " mode-" + mode + " -alarm" +alarm);




            String response = "MODE_MANUAL";
            if (alarm){
                response ="MODE_ALARM";
            }
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }



}