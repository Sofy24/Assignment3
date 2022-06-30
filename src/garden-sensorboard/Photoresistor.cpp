#include "Photoresistor.h"
#include "Config.h"
#include "Arduino.h"


Photoresistor::Photoresistor(int pin){
    this->pin = pin;
}

int Photoresistor::getLuminosity(){
      int value = analogRead(this->pin);   
         Serial.println("photores: "); 
      Serial.println(value); 
      value = map(value,119,4095,0,7);
      Serial.println("photores: "); 
      Serial.println(value); 
      return value;
}
