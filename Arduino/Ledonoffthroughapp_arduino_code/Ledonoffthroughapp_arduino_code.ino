
#include <SoftwareSerial.h>

SoftwareSerial mySerial(0,1);
int dataFromBT;
const int ledPin = 9;
byte brightness;

void setup() {
  
  Serial.begin(9600);
  Serial.println("LOF Starting...");
  
  mySerial.begin(9600);
  pinMode(ledPin, OUTPUT);
}

void loop() {
  if (mySerial.available())
    dataFromBT = mySerial.read();
    
  if (dataFromBT == '0'){
    digitalWrite(ledPin, LOW);
  }
  
  else if (dataFromBT == '1'){
    digitalWrite(ledPin, HIGH);
  }
  
  else {
    if(Serial.available()){
      brightness = Serial.read();
      analogWrite(ledPin,brightness);
}
  }
}
    

