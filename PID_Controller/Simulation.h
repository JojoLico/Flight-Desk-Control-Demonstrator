#ifndef SIMULATION_h
#define SIMULATION_h

//---------- Defines ----------//

#define PIN_SERVO     5
#define PIN_MOTOR     6
#define PIN_BUZZER    9
#define PIN_LED_GREEN 7
#define PIN_LED_RED   8
#define BT_STATE      2
#define PIN_RESET_ZERO_ANGLE   3
#define POTENTIOMETRE A1
#define PITOT_TUBE    A2
#define PIN_SERVO_POT A3

#define SAMPLE_TIME 2
#define BUZZER_TIME 300

#define P           0
#define I           2
#define D           4
#define MOTOR_SPEED 6
#define SETPOINT    8

#define LIMITE_ANGLE 15.0                 //CAN'T BE AN INT !!! Because of later calculation

//---------- External Data ----------//

extern byte dataReceived[];

//---------- Functions ----------//

void process();
void initSimulation();
void updateSimulation();
void setMotorFanSpeed();
void buzzerTone();
void resetZeroAngle();

#endif
