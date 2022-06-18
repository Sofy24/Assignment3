//#include "Scheduler.h"
#include "Config.h"
#include "Arduino.h"
#include "Led_switch.h"
#include "Led_fade.h"
#include "ServoMotor.h"
#include "SoftwareSerial.h"
#include "MsgServiceBT.h"
//#include <avr/sleep.h>  

//SoftwareSerial btChannel();
MsgServiceBT msgService( BLUE_TXD,BLUE_RXD );

/**variabili globali*/


Led_switch* led_s1;
Led_switch* led_s2;
Led_fade* led_f1;
Led_fade* led_f2;

ServoMotor* servo;
/** sveglia della sleep */
void wakeUp(){
  
}

/** setup iniziale */
void setup(){

  //sched.init(100);
  led_s1 = new Led_switch(L1_SWITCH);
  led_s2 = new Led_switch(L2_SWITCH);
  led_f1 = new Led_fade(L1_FADE);
  led_f2 = new Led_fade(L2_FADE);
  servo = new ServoMotor(SERVO_MOTOR_PIN);
  
  msgService.init();  
  Serial.begin(9600);
  while (!Serial){}
  Serial.println("ready to go."); 
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

/** loop principale */
void loop(){
  /**
    MSG FORMAT:
    DEVICE_[ID]_[VALUE]
    DEVICE:
      L= led 
      F= led fade
      S= switch
    ID:
      only in led 1 or 2
    VALUE:
      in fade or switch the intensity
  */
  int len = 50;
  char buf[len];
  String device;
  if (msgService.isMsgAvailable()) {
    Msg* msg = msgService.receiveMsg();
    Serial.println(msg->getContent()); 
    msg->getContent().toCharArray(buf, len);
    device = String(strtok(buf,"_"));
    if (device=="L")
    {
        Serial.println("giusta L");
        if(String(strtok(NULL, "_"))=="1")
        {
          Serial.println("giusto 1");
          led_s1->change();
        }else{
          Serial.println("else");
          led_s2->change();
        }
        
    }else if(device=="F"){
        
        if(String(strtok(NULL, "_"))=="1")
        {
          Serial.println("giusto 1");
          
          led_f1->fade(String(strtok(NULL, "_")).toInt());
        }else{
          Serial.println("else");
          
          led_f2->fade(String(strtok(NULL, "_")).toInt());
        }
    }else if(device=="S"){
        int i=String(strtok(NULL, "_")).toInt();
        Serial.println(i);
        servo->on();
        servo->setSpeed_s(i);
        servo->startIrrigation();
        servo->off();
    }
    delay(500);
    delete msg;
  }
}
