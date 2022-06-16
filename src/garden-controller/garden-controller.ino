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
MsgServiceBT msgService( BLUE_RXD,BLUE_TXD );

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
  /*
  led_s1->switchOn();
  led_s2->switchOn();
  led_f1->fade(1);
  led_f2->fade(3);
  servo->on();
  for(int s=1;s<=5;s++)
  {
    servo->setSpeed_s(s);
    servo->startIrrigation();
  }
  
  servo->off();
  */
  if (msgService.isMsgAvailable()) {
    Msg* msg = msgService.receiveMsg();
    Serial.println(msg->getContent());    
    if (msg->getContent() == "ping"){
       msgService.sendMsg(Msg("pong"));
       delay(500);
    }
    delete msg;
  }
}
