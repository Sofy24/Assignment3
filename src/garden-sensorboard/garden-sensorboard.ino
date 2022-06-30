#include <WiFi.h>
#include <HTTPClient.h>
//#include "SoftwareSerial.h"
#include <Wire.h>
#include "Config.h"
#include "Photoresistor.h"
#include "Temperature.h"
#include "Led.h"

const char* ssid = "Mi 10 Lite 5G";//"Sera";
const char* password = "2cae0ed0b086";//"Bestiel1";


const char *serviceURI = "http://192.168.43.220:8000/api/";
Led* led ;
Photoresistor* photores;
Temperature* temp;
/**variabili globali*/

//Scheduler sched;

/** sveglia della sleep */
void wakeUp(){
  
}


/** gestione sleep 
void setSleep(){
  
  attachInterrupt(digitalPinToInterrupt(PIR_PIN), wakeUp ,HIGH);
  set_sleep_mode(SLEEP_MODE_PWR_DOWN);  
  sleep_enable();
  sleep_mode();  
  // The program will continue from here.  
  isSleeping = false;
  // First thing to do is disable sleep. 
  detachInterrupt(digitalPinToInterrupt(PIR_PIN));
  sleep_disable(); 

}*/


void connectToWifi(const char* ssid, const char* password){
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void setup() {
  Serial.begin(115200); 
  connectToWifi(ssid, password);
//  sched.init(100);
  led = new Led(LED);
  photores = new Photoresistor(PHOTORES);
  temp = new Temperature(TEMP);
}

int sendData(String address, String value, String place){  
  
   HTTPClient http;    
   http.begin(address + "prova");      
   http.addHeader("Content-Type", "application/json");    
   Serial.print(address);
   Serial.println("prova");
   String msg = 
    String("{ \"value\": ") + String(value) + 
    ", \"place\": \"" + place +"\" }";
   Serial.println("SENDING post");
   Serial.println(msg);
   int retCode = http.POST(msg);
   Serial.print("RETURNED CODE: ");
   Serial.println(retCode);
   http.end();  
      
   return retCode;
}

void loop() {
  led->switchOn();
  Serial.println("BANANA"); 
  Serial.println(photores->getLuminosity()); 
  Serial.println(temp->getTemperature()); 
  delay(5000); 
  if (WiFi.status()== WL_CONNECTED){  
    //GET
    HTTPClient http;
  
    String servicePath = String(serviceURI);
 
    /*http.begin(servicePath);
      
    // Send HTTP GET request
    int httpResponseCode = http.GET();
      
    if (httpResponseCode>0) {
      Serial.print("HTTP Response code: ");
      Serial.println(httpResponseCode);
      String payload = http.getString();
      Serial.println(payload);
    } else {
      Serial.print("Error code: ");
      Serial.println(httpResponseCode);
    }
    
    // Free resources
    http.end();*/

    //POST
    String value = String(photores->getLuminosity())+"_"+String(temp->getTemperature());
    int code = sendData(servicePath, value, "home");
    if (code == 200){
       Serial.println("ok");   
     } else {
       Serial.println(String("error: ") + code);
     }
    
    delay(5000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }
}











/** loop principale 
void loop(){
  if(isSleeping){
    setSleep();
  }
  sched.schedule();
  
}*/
