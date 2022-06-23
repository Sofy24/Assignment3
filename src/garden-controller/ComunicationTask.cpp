#include "ComunicationTask.h"

ComunicationTask::ComunicationTask(Task *t0, Task *t1){
  taskIrrigation = t0;
  taskIllumination = t1;
}
  
void ComunicationTask::init(int period){
  Serial.begin(9600);
  while (!Serial){}
  MsgService.init();
  MsgServiceBT.init();
  Serial.println("ready to go.");
  Task::init(period);
//  led = new Led_switch(pin); 
/*  
  
  */
  state = CHEK_NEW_MESSAGE;    
}

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
      in switch the intensity
      in servo if number speed else on/off
  */
void ComunicationTask::tick(){
  switch (state){
    case CHEK_NEW_MESSAGE:
      Serial.println(".");
      if (MsgService.isMsgAvailable()) {
        this->msg = MsgService.receiveMsg();
        this->state = EVALUATE_MESSAGE;
        MsgService.sendMsg("pong");
      }
      if (MsgServiceBT.isMsgAvailable()) {
        this->msg = MsgServiceBT.receiveMsg();
        this->state = EVALUATE_MESSAGE;
        MsgServiceBT.sendMsg("ricevuto");
      }
      break;

    case EVALUATE_MESSAGE:
      Serial.println("entro evaluate");
      msg->getContent().toCharArray(buf, 50);
      device = String(strtok(buf,"_"));
      
      if (device=="L")
      {
          led_type=device;
          MsgServiceBT.sendMsg("pong");
          Serial.println("giusta L");
          if(String(strtok(NULL, "_"))=="1")
          {
            Serial.println("giusto 1");
            led_id=1;
            //led_s1->change();
          }else{
            Serial.println("else");
            led_id=2;
            //led_s2->change();
          }
          taskIllumination->setActive(true);
      }else if(device=="F"){
          led_type=device;
          if(String(strtok(NULL, "_"))=="1")
          {
            Serial.println("giusto 1");
            led_id=1;
              //led_f1->fade(String(strtok(NULL, "_")).toInt());
          }else{
            Serial.println("else");
            led_id=2;
              //led_f2->fade(String(strtok(NULL, "_")).toInt());
          }
          value=String(strtok(NULL, "_")).toInt();
          taskIllumination->setActive(true);
      }else if(device=="S"){
          Serial.println("servo");
          String val = String(strtok(NULL, "_"));
          if(val=="ON"){
            Serial.println("on");
            taskIrrigation->setActive(true);
          }else if(val=="OFF"){
            taskIrrigation->setActive(false);
          }else{
            value=val.toInt();
            taskIrrigation->init(1000-(val.toInt()*10));
          }
            /*
            servo->on();
            servo->setSpeed_s(i);
            servo->startIrrigation();
            servo->off();*/
      }
      delete msg;
      this->state = CHEK_NEW_MESSAGE;
      break;
  }
}
