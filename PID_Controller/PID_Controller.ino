
//---------- PID Controller ----------//
//--------Cranfield University--------//
//-------------Version V3-------------//
//-------------Joris Duran------------//

//---------- Includes ----------//

#include "Communicator.h"
#include "Simulation.h"

//---------- Setup ----------//

void setup() {
  pinMode(PIN_SERVO, OUTPUT);                   //Servo output
  pinMode(PIN_MOTOR, OUTPUT);                   //Motor output
  pinMode(PIN_BUZZER, OUTPUT);                  //Buzzer output
  pinMode(PIN_LED_GREEN, OUTPUT);               //LED output
  pinMode(PIN_LED_GREEN, OUTPUT);               //LED output
  pinMode(POTENTIOMETRE, INPUT);                //Potentiometre input
  pinMode(PIN_SERVO_POT, INPUT);                //Potentiometre input from servoflap
  pinMode(PITOT_TUBE, INPUT);                   //Pitot tube input
  pinMode(BT_STATE, INPUT);                     //Bluetooth module connection state input
  pinMode(PIN_RESET_ZERO_ANGLE, INPUT_PULLUP);

  attachInterrupt(digitalPinToInterrupt(PIN_RESET_ZERO_ANGLE), resetZeroAngle, FALLING);

  initSimulation();                             //Pin, tone, LED
  
  digitalWrite(PIN_LED_RED, HIGH);              //Turn on LED RED
  analogReference(INTERNAL);
  
  Serial.begin(BAUD_RATE);                      //Default 115200
  
}

//---------- Main ----------//

void loop() {
  process();                                    //PID ouput
  buzzerTone();
}


