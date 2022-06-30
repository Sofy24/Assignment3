import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;



public class Test {



    public static void main(String[] args) throws Exception {

        CommChannel channel = new SerialCommChannel("COM3",9600);

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
                    int luminosity = Integer.parseInt(command.split("_")[0].replace(" ",""));
                    int temperature = Integer.parseInt(command.split("_")[1]);

                    if (luminosity < 5){
                        System.out.println("L_1");
                        System.out.println("L_2");
                    } else {
                        System.out.println("L_1_OFF");
                        System.out.println("L_2:OFF");
                    }

                    switch (luminosity){
                        case 0:
                            System.out.println("F_1_4");
                            System.out.println("F_2_4");
                            break;
                        case 1:
                            System.out.println("F_1_3");
                            System.out.println("F_2_3");
                            break;
                        case 2:
                            System.out.println("F_1_2");
                            System.out.println("F_2_2");
                            break;
                        case 3:
                            System.out.println("F_1_1");
                            System.out.println("F_2_1");
                            break;
                        default:
                            System.out.println("F_1_0");
                            System.out.println("F_2_0");
                    }

                    if (luminosity < 2){
                        System.out.println("S_ON");
                    } else {
                        System.out.println("S_OFF");
                    }

                    System.out.println("S_"+temperature);
                }
            }


            if (false){
                alarm = true;
            }


            String response = "This is the response post";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }



}