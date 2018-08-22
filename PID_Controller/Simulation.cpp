#include <PID_v2.h>


//---------- Includes ----------//

#include "Arduino.h"
#include "Simulation.h"
#include "Communicator.h"
#include <Servo.h>

//---------- Variables ----------//

double Setpoint;
double OutputServo;
double Input;
double airVelocity;

int motorFanCommand;
int motorFanSpeed;

int flapMillP15;
int flapMillM15;

int angleMillP15;
int angleMillM15;

int zeroAngle = 680;

long currentMillisMotor;
long currentMillisTone;
long currentMillisZero;
long currentMillisData;

bool pflagTone;

Servo flapActuator;
Servo motorFan;
PID PIDsettings(&Input, &OutputServo, &Setpoint, 0, 0, 0, DIRECT);                     //Open loop all PID = 0, P on Error

//---------- Functions ----------//

void process() { 
  Input       = 0.988*Input + 0.012*analogRead(POTENTIOMETRE);                         //Read value from the potentiometre
  airVelocity = 0.9998*airVelocity + 0.0002*analogRead(PITOT_TUBE);                    //Read value from the pitot tube
  
  PIDsettings.Compute(); 
  flapActuator.writeMicroseconds(OutputServo);                                         //Limit of the actuator due to the system included in the PID code
  
  if ( (millis() - currentMillisMotor >= 100) ) setMotorFanSpeed();
  if ( (millis() - currentMillisData >= PERIOD_UART) ) serialSendData();

  //flapActuator.writeMicroseconds( map(Input, angleMillM15, angleMillP15, flapMillM15, flapMillP15) );    //Debug
  //Serial.println( (String) "PID: " + OutputServo );                                                      //Debug
}

void initSimulation() {
  
  flapActuator.attach(PIN_SERVO);
  motorFan.attach(PIN_MOTOR);

  setMotorFanSpeed();                                           //Ensure the motor is not spinning

  Setpoint    = 1500;                                           //Default = 90deg
  OutputServo = 1500;
  
  flapMillP15 = 1000 + 1000*(90+3*LIMITE_ANGLE)/180;            //The angle to send to the servo is  times the command to reach to right angle
  flapMillM15 = 1000 + 1000*(90-3*LIMITE_ANGLE)/180;            //The angle to send to the servo is  times the command to reach to right angle

  angleMillP15 = zeroAngle + 1024*LIMITE_ANGLE/74.8;            //1.1V at the potentiometre output is 74.8deg
  angleMillM15 = zeroAngle - 1024*LIMITE_ANGLE/74.8;            //1.1V at the potentiometre output is 74.8deg

  PIDsettings.SetWeightFilter(0.1);                                                     //Derivative filter weightning (1 means no filter, 0 means full filter) filter=1-exp(-2*pi*samplingTime*cutOffFrequency)
  PIDsettings.SetSampleTime(SAMPLE_TIME);                                               //PID samples
  PIDsettings.SetOutputLimits(flapMillM15,flapMillP15);                                  //Angle limites
  PIDsettings.SetInputLimits(angleMillM15, angleMillP15);                               //Potentiometer limites  for -15deg/15deg
  PIDsettings.SetSetpointLimits(flapMillM15, flapMillP15);                              //Servo limites
  PIDsettings.SetMode(AUTOMATIC);

  pflagTone = true;
}

void updateSimulation() {

  double Pp          = serialReadData(P) / DECIMAL;    
  double Ip          = serialReadData(I) / DECIMAL;      
  double Dp          = serialReadData(D) / DECIMAL;   
  double setPointp   = serialReadData(SETPOINT);
  double motorSpeedp = serialReadData(MOTOR_SPEED);     

  if (motorSpeedp <= 100 && motorSpeedp >=0) {                                           //Filter
    pflagTone = true;                                                                    //Alert data received
    PIDsettings.SetTunings(Pp, Ip, Dp);
    Setpoint = mapf(setPointp, -LIMITE_ANGLE, LIMITE_ANGLE, flapMillM15, flapMillP15);
    motorFanCommand = motorSpeedp;
  }
}

void setMotorFanSpeed() {                                 //Ramp to smooth the motor command
 currentMillisMotor = millis();
 if (motorFanCommand == 0) {
    motorFanSpeed = motorFanCommand;
    motorFan.writeMicroseconds(1000 + 10*motorFanSpeed);
 } else if (motorFanCommand > motorFanSpeed) {
    motorFanSpeed++;
    motorFan.writeMicroseconds(1000 + 10*motorFanSpeed);
 } else if (motorFanCommand < motorFanSpeed) {
    motorFanSpeed--;
    motorFan.writeMicroseconds(1000 + 10*motorFanSpeed);
 }
}

void buzzerTone() {
  if ( pflagTone && (millis() - currentMillisTone >= BUZZER_TIME) ) {
    currentMillisTone = millis();
    pflagTone = false;
    digitalWrite(PIN_BUZZER, HIGH);
    digitalWrite(PIN_LED_GREEN, HIGH); //Turn off LED GREEN                  //Data sent
  } else if (!pflagTone && millis() - currentMillisTone >= BUZZER_TIME) {
    digitalWrite(PIN_BUZZER, LOW);
    digitalWrite(PIN_LED_GREEN, LOW); //Turn off LED GREEN                   //Data sent
  }
}

void resetZeroAngle() {
  if (micros() - currentMillisZero > 2000000) {                              //To avoid switch bouncing
    currentMillisZero = micros();
    zeroAngle = analogRead(PIN_RESET_ZERO_ANGLE);
    angleMillP15 = zeroAngle + 1024*LIMITE_ANGLE/74.8;            //1.1V at the potentiometre output is 74.8deg
    angleMillM15 = zeroAngle - 1024*LIMITE_ANGLE/74.8;            //1.1V at the potentiometre output is 74.8deg
    PIDsettings.SetInputLimits(angleMillM15, angleMillP15);       //Potentiometer limites  for -15deg/15deg
  }
}



