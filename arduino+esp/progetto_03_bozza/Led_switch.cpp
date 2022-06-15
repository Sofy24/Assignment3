#include "Led_switch.h"
#include "Arduino.h"
#include "Config.h"

Led_switch::Led_switch(int pin){
    this->pin = pin;
    pinMode(pin,OUTPUT);
}

void Led_switch::switchOn(){
      
      digitalWrite(pin, HIGH);
}

void Led_switch::switchOff(){
      digitalWrite(pin, LOW);
      
}
