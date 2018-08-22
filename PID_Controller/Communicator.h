#ifndef Communicator_h
#define Communicator_h

//---------- Defines ----------//

#define BAUD_RATE 115200
#define PERIOD_UART 25

#define BUFFER_LENGTH_SENT     10
#define BUFFER_LENGTH_RECEIVED 10

#define INPUT_READ    0
#define OUTPUT_PID    2
#define PERIOD        4
#define AIR_VELOCITY  6
#define ECHO_SETPOINT 8

#define DECIMAL 100.00

//---------- External Data ----------//

extern double Input;
extern double Setpoint;
extern double airVelocity;
extern double OutputServo;

extern long currentMillisData;

extern int flapMillP15;
extern int flapMillM15;

extern int angleMillP15;
extern int angleMillM15;

extern bool pflagTone;

//---------- Functions ----------//

void serialSendData();
int serialReadData(int Pos);
void serialEvent();
int byteDivider(int number);
int decimalDivider (double number);
double mapf(double val, double in_min, double in_max, double out_min, double out_max);

#endif
