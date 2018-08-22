
//---------- Includes ----------//

#include "Arduino.h"
#include "Communicator.h"
#include "Simulation.h"

//---------- Variables ----------//

byte dataSent[BUFFER_LENGTH_SENT];
byte dataReceived[BUFFER_LENGTH_RECEIVED];

//---------- Functions ----------//

void serialSendData() {
  currentMillisData = millis();
  if (Serial || digitalRead(BT_STATE) == HIGH) {
    int inputSent       = decimalDivider( mapf(Input, angleMillM15, angleMillP15, -LIMITE_ANGLE, LIMITE_ANGLE) );
    int outputSent      = decimalDivider( mapf(OutputServo, flapMillM15, flapMillP15, -LIMITE_ANGLE, LIMITE_ANGLE) ); 
    int airVelocitySent = decimalDivider( sqrt(mapf(airVelocity, 896, 0, 0, 1796)) );         //Vout = VS × (0.2 × P(kPa)+0.5) ± 6.25% VFSS, VS = 5.0V, VFSS = 4.0V, and Bernoulli to vitesse, rho = 1.225 Kg/m3, With the differential OpAmp maximal value from the Pitot Tube is 1796Pa for Vout = 3.6V
    int setpointSent    = decimalDivider( mapf(Setpoint, flapMillM15, flapMillP15, -LIMITE_ANGLE, LIMITE_ANGLE) );
    
    dataSent[INPUT_READ]          = byte( byteDivider(inputSent) );
    dataSent[INPUT_READ + 1]      = byte(inputSent);
    dataSent[OUTPUT_PID]          = byte( byteDivider(outputSent) );
    dataSent[OUTPUT_PID + 1]      = byte(outputSent);
  
    dataSent[AIR_VELOCITY]        = byte( byteDivider(airVelocitySent) );
    dataSent[AIR_VELOCITY + 1]    = byte(airVelocitySent);
    
    dataSent[PERIOD]              = byte( byteDivider(PERIOD_UART) );
    dataSent[PERIOD + 1]          = byte(PERIOD_UART);
  
    dataSent[ECHO_SETPOINT]       = byte( byteDivider(setpointSent) );
    dataSent[ECHO_SETPOINT + 1]   = byte(setpointSent);
    
    Serial.write( dataSent, BUFFER_LENGTH_SENT );
  } else {
    pflagTone = true;                                                       //Alert data NO sent
  }
}

int serialReadData(int Pos) {
    return ( dataReceived[Pos] << 8) | ( dataReceived[Pos + 1] & 0xFF );     //Assembly the byte received to built an int
  }

void serialEvent() {
  if (Serial.available() == BUFFER_LENGTH_RECEIVED) {
    Serial.readBytes(dataReceived, BUFFER_LENGTH_RECEIVED);
    updateSimulation();                                                     //Update PID, setpoint, fan
  } else {
    Serial.flush();
  }
}

int byteDivider (int number) {                                              //Divide byte to be send
  return number >> 8;
}

int decimalDivider (double number) {                                        //To int double after
  return number*DECIMAL;
}

double mapf(double val, double in_min, double in_max, double out_min, double out_max) {
    return (val - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}




