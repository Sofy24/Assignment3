import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.util.concurrent.TimeUnit;



public class Logic {

    public static CommChannel channel;

    public static void main(String[] args) throws Exception {

        channel = new SerialCommChannel("COM3",9600);

        System.out.println("Waiting Arduino for rebooting...");
        Thread.sleep(4000);
        System.out.println("Ready.");

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/prova", new MyHandlerPost());
        server.createContext("/api", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
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

                    if (luminosity < 2){
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
                    }

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



                    if (!alarm) {
                        System.out.println(commands);
                        //channel.sendMsg(commands);
                    }
                    else {
                        System.out.println("ALARM");
                        channel.sendMsg("ALARM");
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