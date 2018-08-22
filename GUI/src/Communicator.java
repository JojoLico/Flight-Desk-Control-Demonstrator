
/*------------Communicator------------*/
/*-------OPen/Close Serial Port-------*/

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Communicator {

	// ------Attributes------//

	private byte[] dataSent     = new byte[10];														//Buffer to send to the Arduino
	private byte[] dataReceived = new byte[10];														//Buffer received from the Arduino

	public static final int AEROFOIL_ANGLE_POS = 0;												//Data addresses inside the dataReceived
	public static final int AILERON_ANGLE_POS  = 2;
	public static final int PERIOD_POS         = 4;
	public static final int AIR_VELOCITY_POS   = 6;
	public static final int ECHO_SETPOINT_POS  = 8;

	public static final int P_POS         = 0;													//Data addresses inside dataSent
	public static final int I_POS         = 2;
	public static final int D_POS         = 4;
	public static final int MOTOR_POS     = 6;
	public static final int SETPOINT_POS  = 8;

	private static final int TIMEOUT   = 2000;   									    			 //The timeout value for connecting with the port
	                                                
	private HashMap<String, CommPortIdentifier> portMap = new HashMap<String, CommPortIdentifier>(); //Map the port names to CommPortIdentifiers
	private SerialPort serialPort;
	private StreamConnection serialBTconnection;
	private String urlBTdevice;
	private InputStream input;                                                            //Input and output streams for sending and receiving data
	private OutputStream output;
	
	private int baudRate       = 115200;                                                          	 //Baud rate default idem Arduino
	private int timeDisconnect = 0;                                                                	 //to disconnect the arduino if nothing is received after a certain amount of time
	
	private boolean bConnected    = false;                                                           //Is connected to a serial port or not
	private boolean bConnectedBT  = false;
	private boolean scanConnectBT = false;
	
	// ------Functions------//

	public Communicator() {
	}

	public void searchForPorts(GUI gui) {															//Add COM port into the combobox from the GUI
		Enumeration<?> ports = CommPortIdentifier.getPortIdentifiers();                             //For containing the ports that will be found
		List<String> currentPortList = new ArrayList<String>();		
		int nbPort = 0;																				//Number of different COM Port
		portMap.clear();																			//No dead Com port inside the map
		while (ports.hasMoreElements()) {
			CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();
			if ( curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {							 //Check if it is a COM Port and if it is not already in the combobox from the GUI
				currentPortList.add(curPort.getName());
				portMap.put(curPort.getName(), curPort);
				nbPort++;
			}
		}
		if ( !(gui.getComboBoxPort().getItemCount() == nbPort) ) {
			gui.getComboBoxPort().removeAllItems();													//No dead COM Port inside the combobox
			for(int i = 0; i < currentPortList.size(); i++) {
				gui.getComboBoxPort().addItem( currentPortList.get(i) );							//Add COM port into the combobox from the GUI
			}
		}
	}
	   
	public void scanBTdevices() throws InterruptedException, BluetoothStateException {
        if ( LocalDevice.isPowerOn() && (urlBTdevice == null) ) {
        	scanConnectBT = true;
			LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, new DiscoveryListener() {
				public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				        try {
							if (btDevice.getFriendlyName(false).matches("FLIGHT_DESK_DEMONSTRATOR")) {
							    urlBTdevice = "btspp://" + btDevice.getBluetoothAddress() + ":1;authenticate=false;encrypt=false;master=falsel";
							    scanConnectBT = false;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				@Override
				public void inquiryCompleted(int discType) {
				    scanConnectBT = false;
				}
				@Override
				public void serviceSearchCompleted(int transID, int respCode) {
					scanConnectBT = false;
				}
				@Override
				public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				}
			});
			while (scanConnectBT) {
				Thread.sleep(250);
	        }
        }
    }


	public void connect(String selectedPort) throws UnsupportedCommOperationException, IOException, PortInUseException {//Connect the Arduino
			CommPortIdentifier selectedPortIdentifier = portMap.get(selectedPort);                  //This is the object that contains the opened port
			serialPort = (SerialPort) selectedPortIdentifier.open(selectedPort, TIMEOUT);
			serialPort.setSerialPortParams(baudRate, 												//COM Port settings
					SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			input  = serialPort.getInputStream();													//Init stream
			output = serialPort.getOutputStream();
			bConnected = true;
	}
	
	public void connectBT() throws IOException, InterruptedException {						//Connect the Arduino bluetooth 
			if ( !(urlBTdevice == null) && LocalDevice.isPowerOn() ) {
	            serialBTconnection = (StreamConnection) Connector.open(urlBTdevice);
			    input  = serialBTconnection.openInputStream();
			    output = serialBTconnection.openOutputStream();
			    bConnectedBT = true;
			    bConnected   = true;
			}
	}

	public void disconnect() throws IOException {											//Disconnect the Arduino
		writeData(0,0,0,0,0);																//Safety
		if (bConnectedBT) {
			serialBTconnection.close();
		} else {
			serialPort.removeEventListener();
			serialPort.close();
		}	
			input.close();
			output.close();
			bConnected    = false;
			bConnectedBT  = false;
			timeDisconnect = 0;
	}
	
	public void serialReadData(GUI gui) throws IOException {												//Fill dataReceived with the incoming data
		if (input.available() == 0) {
			timeDisconnect++;
		} else if (input.available() == dataReceived.length) {
			input.read(dataReceived, 0, dataReceived.length);
			timeDisconnect = 0;
		} else if (input.available() % dataReceived.length == 0) {
			input.skip(input.available() - dataReceived.length);
			input.read(dataReceived, 0, dataReceived.length);
			timeDisconnect = 0;
		} else {
			input.skip(input.available());
			timeDisconnect = 0;
		}
		if (timeDisconnect > 50) gui.btnConnectDisconnectActionPerformed();
	}

	public void writeData(double P, double I, double D, double setPoint, double motorSpeed) throws IOException {
																									//Write the settings from the GUI in dataSent
			dataSent[P_POS]          = (byte) byteDivider(P);										//Each int is divided into two byte to be sent
			dataSent[P_POS+1]        = (byte) P;
			dataSent[I_POS]          = (byte) byteDivider(I);
			dataSent[I_POS+1]        = (byte) I;
			dataSent[D_POS]          = (byte) byteDivider(D);
			dataSent[D_POS+1]        = (byte) D;
			dataSent[SETPOINT_POS]   = (byte) byteDivider(setPoint);
			dataSent[SETPOINT_POS+1] = (byte) setPoint;
			dataSent[MOTOR_POS]      = (byte) byteDivider(motorSpeed);
			dataSent[MOTOR_POS+1]    = (byte) motorSpeed;
			
			output.write(dataSent);																	//Send the data
			output.flush();																			//Ensure the stream is cleaned
	}
	
	public int readData(int Pos) {																	//Assembly the two received to recreate the initial data, the lowest byte must be not signed
		return ( dataReceived[Pos] << 8) | ( dataReceived[Pos + 1] & 0xFF );
	}
	
	public static int byteDivider (double number) {													//the biggest byte of a int is now coded on one byte
		  return ((int) number)>>8;
		}

	public boolean getConnected() {
		return bConnected;
	}
	
	public void setConnected(boolean connectedP) {
		bConnected = connectedP;
	}
	
	public void setBaudRate(int baudRateP) {
		baudRate = baudRateP;
	}
	
	public int getBaudRate() {
		return baudRate;
	}
	
	public boolean getScanConnectBT() {
		return scanConnectBT;
	}
	
	public boolean getConnectedBT() {
		return bConnectedBT;
	}
	
}
