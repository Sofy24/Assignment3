#include "Temperature.h"
#include "Config.h"
#include "Arduino.h"

Temperature::Temperature(int pin){
    this->pin = pin;
}

int Temperature::getTemperature(){
      //ottieni la temperatura
      int value = analogRead(this->pin);      
      Serial.println(value); 
      double value_in_C = ((((double)value) / 1023) -0.5) * 100;
      Serial.print("Celsius: ");
      Serial.println(value_in_C); 
      value = map(value, 0, 1024, 0, 4);
      Serial.println(value); 
      value_in_C = map(value_in_C, -40, 125, 0, 4);
      Serial.print("Celsius: ");
      Serial.println(value_in_C); 
      return value_in_C;
}
