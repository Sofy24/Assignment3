import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import jdk.jfr.ContentType;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class Logic {

    public static CommChannel channel;

    public static void main(String[] args) throws Exception {

        channel = new SerialCommChannel("COM3",9600);

        System.out.println("Waiting Arduino for rebooting...");
        Thread.sleep(4000);
        System.out.println("Ready.");
        sendPost();

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/prova", new MyHandlerPost());
        server.createContext("/api", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public static void sendPost() throws IOException {
        URL url = new URL("http://192.168.43.157/Assignment3/src/garden-dashboard/GardenDashboard.php"); // URL to your application
        /*Map<String,Object> params = new LinkedHashMap<>();
        params.put("temp", 5); // All parameters, also easy
        params.put("light", 17);
        params.put("modality", 17);
        params.put("alarm", "allarm");

        JSONObject json = new JSONObject();
        json.put("someKey", "someValue");

        StringBuilder postData = new StringBuilder();
        // POST as urlencoded is basically key-value pairs, as with GET
        // This creates key=value&key=value&... pairs
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            try {
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            postData.append('=');
            try {
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        // Convert string to byte array, as it should be sent
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");*/

        // Connect, easy
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json"); //x-www-form-urlencoded
        //conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        // Tell server that this is POST and in which format is the data
        conn.setRequestMethod("POST");
        /*JSONObject payload = new JSONObject();
        payload.put("temp", 5);
        payload.put("light", 17);
        payload.put("modality", 17);
        payload.put("alarm", "alarm");*/

        String jsonInputString = "{\"temp\": 5, \"light\": 17, \"modality\": 17, \"alarm\": \"alarm\"}";
        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        }



        /*conn.getOutputStream().write(postDataBytes);

        // This gets the output from your server
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        for (int c; (c = in.read()) >= 0;)
            System.out.print((char)c);*/


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

                    if (luminosity < 5){
                        //channel.sendMsg("L_1");
                        commands = commands.concat("L_1,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(60);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        //channel.sendMsg("L_2");
                        commands = commands.concat("L_2,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(60);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        //channel.sendMsg("L_1");
                        commands = commands.concat("L_1,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(60);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        //channel.sendMsg("L_2");
                        commands = commands.concat("L_2,");
                        try {
                            TimeUnit.MILLISECONDS.sleep(60);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(luminosity);
                    switch (luminosity){
                        case 0:
                            //channel.sendMsg("F_1_4");
                            commands = commands.concat("F_1_4,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //channel.sendMsg("F_2_4");
                            commands = commands.concat("F_2_4,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case 1:
                            //channel.sendMsg("F_1_3");
                            commands = commands.concat("F_1_3,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //channel.sendMsg("F_2_3");
                            commands = commands.concat("F_2_3,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case 2:
                            //channel.sendMsg("F_1_2");
                            commands = commands.concat("F_1_2,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //channel.sendMsg("F_2_2");
                            commands = commands.concat("F_2_2,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case 3:
                            //channel.sendMsg("F_1_1");
                            commands = commands.concat("F_1_1,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //channel.sendMsg("F_2_1");
                            commands = commands.concat("F_2_1,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        default:
                            //channel.sendMsg("F_1_0");
                            commands = commands.concat("F_1_0,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            //channel.sendMsg("F_2_0");
                            commands = commands.concat("F_2_0,");
                            try {
                                TimeUnit.MILLISECONDS.sleep(60);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                    }

                    //channel.sendMsg("S_"+temperature);
                    commands = commands.concat("S_"+temperature+",");
                    try {
                        TimeUnit.MILLISECONDS.sleep(60);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (luminosity < 2){
                        //channel.sendMsg("S_ON");
                        commands = commands.concat("S_ON");
                        irrigation = true;
                    } else {
                        //channel.sendMsg("S_OFF");
                        commands = commands.concat("S_OFF");
                        irrigation = false;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(60);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    if (!alarm) {
                        System.out.println(commands);
                        Logic.channel.sendMsg(commands);
                    }
                    else {
                        System.out.println("ALARM");
                        Logic.channel.sendMsg("ALARM");
                    }
                }
            }


            System.out.println("SEND DATA TO DASHBOARD: temp-" + temperature + " lum-" + luminosity + " mode-" + mode + " -alarm" +alarm);



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