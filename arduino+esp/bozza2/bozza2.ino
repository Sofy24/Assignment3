#include <WiFi.h>
#include <HTTPClient.h>
//#include "SoftwareSerial.h"
#include <Wire.h>
#include "Config.h"
#include "Led.h"
#include "Photoresistor.h"
#include "Temperature.h"


const char* ssid = "LittleBarfly";
const char* password = "esiot2122";


const char *serviceURI = "https://1fcf-137-204-20-125.ngrok.io";
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
  //connectToWifi(ssid, password);
//  sched.init(100);
  led = new Led(LED);
  photores = new Photoresistor(PHOTORES);
  temp = new Temperature(TEMP);
}

int sendData(String address, float value, String place){  
  
   HTTPClient http;    
   http.begin(address + "/api/data");      
   http.addHeader("Content-Type", "application/json");    
    
   String msg = 
    String("{ \"value\": ") + String(value) + 
    ", \"place\": \"" + place +"\" }";
   
   int retCode = http.POST(msg);   
   http.end();  
      
   return retCode;
}

void loop() {
  led->switchOn();
  Serial.println("BANANA"); 
  Serial.println(photores->getLuminosity()); 
  Serial.println(temp->getTemperature()); 
  delay(5000); 
  /*
  if (WiFi.status()== WL_CONNECTED){      
    int value = random(15,20);
    int code = sendData(serviceURI, value, "home");
    if (code == 200){
       Serial.println("ok");   
     } else {
       Serial.println(String("error: ") + code);
     }
    
    delay(5000);

  } else {
    Serial.println("WiFi Disconnected... Reconnect.");
    connectToWifi(ssid, password);
  }*/
}











/** loop principale 
void loop(){
  if(isSleeping){
    setSleep();
  }
  sched.schedule();
  
}*/
