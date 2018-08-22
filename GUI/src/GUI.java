
/*--------GUI Frame Creation--------*/
/*----------Buttons action----------*/

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.swing.JSlider;
import javax.bluetooth.BluetoothStateException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import org.jfree.chart.ChartPanel;
import org.xml.sax.SAXException;

import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.Color;
import net.miginfocom.swing.MigLayout;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import java.awt.Font;

public class GUI extends JFrame {

	// ------Attributes------//

	private static final long serialVersionUID = -949825819154771934L;
	private static final double DECIMAL = 100.00;
	private static SettingsReader settingsReader;								//Read default settings from the XML file
	private static ExcelCreator excelCreator;									//Excel Creator
	
	private boolean flagProgress  = false;										//Flag to run the progress bar
	private boolean flagPause     = false;										//Flag put the GUI in pause
	private boolean flagAutomatic = false;										//Flag manual/Automatic capture mode
	
	private int value = 0;
	
	private JPanel window;														//Frame GUI
	private Chart chart;														
	private ChartPanel chartpanel;												//Frame Chart
	private Communicator communicator;											//Communication
	private Animation animation;												//Frame animation
	private JButton btnConnect;													//Buttons
	private JButton btnExport;
	private JButton btnInformation;
	private JButton btnResetChart;
	private JButton btnPause;
	private JButton btnStop;
	private JButton btnGo;
	private JSlider slider_P;													//Sliders
	private JSlider slider_I;
	private JSlider slider_D;
	private JSlider slider_Angle;
	private JSlider slider_Motor;
	private JLabel valuePlabel;													//Changing labels
	private JLabel valueIlabel;
	private JLabel valueDlabel;
	private JLabel valueAlphaLabel;
	private JLabel valueMotorLabel;
	private JComboBox<String> comboBoxDefaultSettings;							//Comboboxes
	private JComboBox<Integer> comboBoxBaudRate;
	private JComboBox<String> comboBoxPort;
	private JTextField txtMonitor;												//Texte fields
	private JTextField txtTest;
	private JTextField txtTimeCapture;
	private JCheckBox chckAutomatic;											//Checkbox capture automatic mode
	private JProgressBar progressBar;											//Progress bar

	// ------Functions------//

	public GUI() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
																				//Create the GUI
		settingsReader = new SettingsReader();
		communicator   = new Communicator();
		createObjects();														//Create sliders, buttons, chart, animation, text fields
		createLabel();															//Create the labels
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1300, 900);
		setTitle("Flight Desk Demonstrator V2.0");
		setContentPane(window);
	}

	private void createObjects() throws BluetoothStateException, InterruptedException {
																				//Create sliders, buttons, chart, animation, text fields
		window = new JPanel();													//window general layout
		window.setBorder(new EmptyBorder(5, 5, 5, 5));
		window.setLayout(new MigLayout("", "[:120px:180px,grow][10][:120px:180px,grow][10px][:120px:200px,grow][10][550px,grow]", "[10px,grow][30px,grow][10px,grow][30px,grow][30px,grow][10px,grow][30px,grow][30px,grow][10px,grow][30px,grow][30px,grow][10,grow][30px,grow][30px,grow][10px,grow][30px,grow][10px,grow][30px,grow][30px,grow][10px,grow][30px,grow][30px,grow][10px,grow][30px,grow][30px,grow][30px,grow][10px,grow][30px,grow][30px,grow]"));

		animation = new Animation();											//Animation frame creation
		animation.setBackground(new Color(225, 255, 255));
		animation.setLayout(null);
		
		txtTest = new JTextField("Test 1");										//Initial text fields implementation
		txtTest.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtMonitor = new JTextField("Welcome to the Flight Desk Demonstrator GUI V2.0");
		txtMonitor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txtTimeCapture = new JTextField("Enter time in ms");
		txtTimeCapture.setFont(new Font("Tahoma", Font.PLAIN, 15));

		btnConnect = new JButton("Connect");									//Buttons creation
		btnConnect.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnConnectDisconnectActionPerformed();
			}
		});
		
		btnExport = new JButton("Export");
		btnExport.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnExportActionPerformed();
			}
		});
		
		btnPause = new JButton("Pause");
		btnPause.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnPauseResumeActionPerformed();
			}
		});
		
		btnInformation = new JButton("Information");								//Read text file
		btnInformation.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnInformation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				javax.swing.JOptionPane.showMessageDialog(null,
						settingsReader.readInformation() );
			}
		});
		
		btnResetChart = new JButton("Reset Chart");
		btnResetChart.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnResetChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnResetChartActionPerformed();
			}
		});
		
		btnStop = new JButton("Motor Stop !");
		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnStop.setBackground(Color.RED);
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				slider_Motor.setValue(0);											//Stop the motor, safety
				btnGoActionPerformed();
			}
		});
			
		slider_P = new JSlider(0, 300, 0);											//Sliders creation
		slider_P.setFont(new Font("Tahoma", Font.PLAIN, 15));
		slider_P.setMajorTickSpacing(50);
		slider_P.setMinorTickSpacing(10);
		slider_P.setPaintTicks(true);
		slider_P.setPaintLabels(true);
		slider_P.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent arg0){
		    	valuePlabel.setText("" + slider_P.getValue()/DECIMAL);
		    	if ( value == slider_P.getValue() ) btnGoActionPerformed();
		    	else value = slider_P.getValue();
			}
		    });   

		slider_I = new JSlider(0, 300, 0);
		slider_I.setFont(new Font("Tahoma", Font.PLAIN, 15));
		slider_I.setMajorTickSpacing(50);
		slider_I.setMinorTickSpacing(10);
		slider_I.setPaintTicks(true);
		slider_I.setPaintLabels(true);
		slider_I.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent arg0){
		    	valueIlabel.setText("" + slider_I.getValue()/DECIMAL);
		    	if ( value == slider_I.getValue() ) btnGoActionPerformed();
		    	else value = slider_I.getValue();
			}
		    });
		
		slider_D = new JSlider(0, 100, 0);
		slider_D.setFont(new Font("Tahoma", Font.PLAIN, 15));
		slider_D.setPaintTicks(true);
		slider_D.setPaintLabels(true);
		slider_D.setMinorTickSpacing(5);
		slider_D.setMajorTickSpacing(10);
		slider_D.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0){
				valueDlabel.setText("" + slider_D.getValue()/DECIMAL);
		    	if ( value == slider_D.getValue() ) btnGoActionPerformed();
		    	else value = slider_D.getValue();
			}
			}); 

		slider_Angle = new JSlider(-15, 15, 0);
		slider_Angle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		slider_Angle.setPaintTicks(true);
		slider_Angle.setPaintLabels(true);
		slider_Angle.setMinorTickSpacing(1);
		slider_Angle.setMajorTickSpacing(5);
		slider_Angle.addChangeListener(new ChangeListener() {
		      public void stateChanged(ChangeEvent arg0){
		    	valueAlphaLabel.setText("" + slider_Angle.getValue());
			}
		    }); 
		
		slider_Motor = new JSlider(0, 100, 0);
		slider_Motor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		slider_Motor.setPaintTicks(true);
		slider_Motor.setPaintLabels(true);
		slider_Motor.setMinorTickSpacing(5);
		slider_Motor.setMajorTickSpacing(10);
		slider_Motor.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0){
				valueMotorLabel.setText("" + slider_Motor.getValue());
		    	if ( value == slider_Motor.getValue() ) btnGoActionPerformed(); 
		    	else value = slider_Motor.getValue();
			}
			});
		
		chckAutomatic = new JCheckBox("Automatic Export");							//Check box creation
		chckAutomatic.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckAutomatic.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0){
				if (chckAutomatic.isSelected() == true ) {
					txtMonitor.setText("Automatic capture mode, set the time in ms and send settings !");
				} else {
					txtMonitor.setText("Manual capture mode !");
				}
			}
			});
		
		progressBar = new JProgressBar();											//Progress bar creation
		progressBar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		progressBar.setBackground(Color.WHITE);
		progressBar.setForeground(Color.GREEN);
		progressBar.setStringPainted(true);
		progressBar.setString("");
		
		valuePlabel = new JLabel(  "" + slider_P.getValue() );						//Labels initialisation
		valuePlabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		valueIlabel = new JLabel(  "" + slider_I.getValue() );
		valueIlabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		valueDlabel = new JLabel(  "" + slider_D.getValue() );
		valueDlabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		valueAlphaLabel = new JLabel(  "" + slider_Angle.getValue() );
		valueAlphaLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		valueMotorLabel = new JLabel(  "" + slider_Motor.getValue() );
		valueMotorLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		window.add(valuePlabel, "cell 0 4,alignx center,aligny center");			//Add component to the GUI
		window.add(valueIlabel, "cell 0 7,alignx center,aligny center");
		window.add(valueDlabel, "cell 0 10,alignx center,aligny center");
		window.add(valueAlphaLabel, "cell 0 13,alignx center,aligny center");
		
		btnGo = new JButton("Send Command");
		btnGo.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnGoActionPerformed();
			}
		});
		window.add(btnGo, "cell 2 15 3 1,growx,aligny center");
		
		window.add(slider_Motor, "cell 2 17 3 2,growx,aligny center");
		window.add(valueMotorLabel, "cell 0 18,alignx center,aligny center");
		
		window.add(btnConnect, "cell 4 21,growx,aligny center");
		window.add(btnPause, "cell 2 24,growx,aligny center");
		window.add(btnExport, "cell 4 24,growx,aligny center");
		window.add(btnStop, "cell 2 27,growx,aligny center");
		window.add(btnResetChart, "cell 4 27,growx,aligny center");
		
		window.add(slider_P, "cell 2 3 3 2,growx,aligny center");
		window.add(slider_I, "cell 2 6 3 2,growx,aligny center");
		window.add(slider_D, "cell 2 9 3 2,growx,aligny center");
		window.add(slider_Angle, "cell 2 12 3 2,growx,aligny center");
		
		window.add(txtTest, "cell 0 24,growx,aligny center");
		window.add(txtTimeCapture, "cell 0 25,growx,aligny center");
			
		window.add(btnInformation, "cell 4 25,growx,aligny center");
		
		window.add(chckAutomatic, "cell 2 25,growx,aligny center");
		
		window.add(txtMonitor, "cell 0 28 3 1,growx,aligny center");
		
		window.add(progressBar, "cell 4 28,growx,aligny center");
		
		window.add(animation, "cell 6 1 1 11,grow");

		createComboBoxPort();														//Combobox creation as it is special box
		createComboBoxDefaultSettings();											//Combobox default settings as it contains the pre-set
		createComboBoxBaudRate();													//Combobox Baud rate as it contains the BaudRate
		creatNewChart();															//Chart creation and initialisation

	}

	private void createLabel() {													//Static labels creation
		JLabel lblP = new JLabel("Proportionnal");
		lblP.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblP, "flowx,cell 0 3,alignx center,aligny center");

		JLabel lblI = new JLabel("Integral");
		lblI.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblI, "cell 0 6,alignx center,aligny center");

		JLabel lblD = new JLabel("Derivative");
		lblD.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblD, "cell 0 9,alignx center,aligny center");

		JLabel lblAngle = new JLabel("Command Angle degree");
		lblAngle.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblAngle, "cell 0 12,alignx center,aligny center");

		JLabel lblMotorSpeed = new JLabel("Motor Speed %");
		lblMotorSpeed.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblMotorSpeed, "cell 0 17,alignx center,aligny center");
		
		JLabel lblExportFileName = new JLabel("Export File");
		lblExportFileName.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblExportFileName, "cell 0 23 5 1,alignx center,aligny center");
		
		JLabel lblArduinoConnexion = new JLabel("Arduino Connection");
		lblArduinoConnexion.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblArduinoConnexion, "cell 2 20 3 1,alignx center,aligny center");
		
		JLabel lblParameters = new JLabel("Parameters");
		lblParameters.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblParameters, "cell 0 1,alignx center,aligny center");
		
		JLabel lblBaudRateLabel = new JLabel("Baud Rate");
		lblBaudRateLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblBaudRateLabel, "cell 0 20,alignx center,aligny center");
		
		JLabel  lblMonitorLabel = new JLabel("Monitor");
		lblMonitorLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		window.add(lblMonitorLabel, "cell 0 27,alignx center,aligny center");
	}

	private void creatNewChart() {													//Chart creation
		chart = new Chart();
		chartpanel = new ChartPanel(chart.getChart());
		chartpanel.setLayout(null);
		window.add(chartpanel, "cell 6 12 1 17,grow");
	}
	
	private void createComboBoxBaudRate() {											//Combobox Baud rate, contains the BaudRate
		comboBoxBaudRate = new JComboBox<Integer>();
		comboBoxBaudRate.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBoxBaudRate.setBackground(Color.WHITE);
		comboBoxBaudRate.addItem(115200);
		comboBoxBaudRate.addItem(57600);
		comboBoxBaudRate.addItem(38400);
		comboBoxBaudRate.addItem(19200);
		comboBoxBaudRate.addItem(9600);
		comboBoxBaudRate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				communicator.setBaudRate( (int) comboBoxBaudRate.getSelectedItem() );
				txtMonitor.setText( "Baud Rate selected: " + comboBoxBaudRate.getSelectedItem() );
			}
		});
		window.add(comboBoxBaudRate, "cell 0 21,growx,aligny center");
	}	

	private void createComboBoxPort() throws BluetoothStateException, InterruptedException {//Combobox COM Port creation
		comboBoxPort = new JComboBox<String>();
		comboBoxPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBoxPort.setBackground(Color.WHITE);
		window.add(comboBoxPort, "cell 2 21,growx,aligny center");
	}		

	private void createComboBoxDefaultSettings() {									//Combobox default settings
		comboBoxDefaultSettings = new JComboBox<String>();
		comboBoxDefaultSettings.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBoxDefaultSettings.setBackground(Color.WHITE);
		for (int i = 0; i<settingsReader.getNbRacineNoeuds(); i++) {				//Fill the combobox with settings'name from the XML file
			String name = settingsReader.readNameSettings (i);
			if (name!= null) comboBoxDefaultSettings.addItem( name );
		}
		comboBoxDefaultSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setDefaultSettingsActionPerformed();
			}
		});
		window.add(comboBoxDefaultSettings, "cell 2 1 3 1,growx,aligny center");
	}
	
	private void setDefaultSettingsActionPerformed() {								//Read pre-set of each label of the default settings from the XML file
		for (int i = 0; i<settingsReader.getNbRacineNoeuds(); i++) {
			if (comboBoxDefaultSettings.getSelectedItem() == settingsReader.readNameSettings (i)) {
				slider_P.setValue( (int) (settingsReader.readPIDsettings (i, "p")*DECIMAL) );
				slider_I.setValue( (int) (settingsReader.readPIDsettings (i, "i")*DECIMAL) );
				slider_D.setValue( (int) (settingsReader.readPIDsettings (i, "d")*DECIMAL) );
			}
		}
		btnGoActionPerformed();
		txtMonitor.setText( "Settings selected: " + comboBoxDefaultSettings.getSelectedItem() );
	}
	
	public void btnConnectDisconnectActionPerformed() {							  //Action of the connect button following its state
		try {
			if (btnConnect.getText() == "Connect") {
				btnConnectActionPerformed();
			} else {
				btnDisconnectActionPerformed();
			}
		} catch (Exception e) {
			btnConnectDisconnectActionPerformed();
			e.printStackTrace();
		}
	}

	private void btnConnectActionPerformed() throws InterruptedException, IOException, UnsupportedCommOperationException, PortInUseException {	//Connect the Arduino
		communicator.scanBTdevices();
		communicator.connectBT();
		if (communicator.getConnectedBT() == false) {
			communicator.connect( (String) comboBoxPort.getSelectedItem() );
		}
		if (communicator.getConnected()) { 											//Init Stream
			chart.reInitChart();
			btnConnect.setText("Disconnect");										//Change button label
			flagProgress = true;													//Progress bar cycle enable
			txtMonitor.setText("Arduino connected with success !");
		} else {
			txtMonitor.setText("Arduino not connected !");
		}
	}

	public void btnDisconnectActionPerformed() throws IOException {			//Disconnect the Arduino
		communicator.disconnect();
		btnConnect.setText("Connect");
		flagPause = false;														//Re Init the flag Pause and the Pause button anyway
		btnPause.setText("Pause");
		txtMonitor.setText("Arduino disconnected with success !");
	}

	private void btnGoActionPerformed() {										//Send sttings to the Arduino
		try {
			value = -1;
			if (communicator.getConnected()) {
				if (chckAutomatic.isSelected() ) flagAutomatic = true;
				communicator.writeData(slider_P.getValue(), 					//Values sent to the Arduino are int from the sliders and need to be divided by 100.00 at the reception
									   slider_I.getValue(), 
									   slider_D.getValue(),
									   slider_Angle.getValue(),
									   slider_Motor.getValue());
				txtMonitor.setText("New settings sent with success !");
			} else {
				txtMonitor.setText("Arduino not connected !");
			}
		} catch ( IOException e ) {
			e.printStackTrace();	
		}
	}
	
	private void btnPauseResumeActionPerformed() {									//Action of the Pause button following its state
		if (btnPause.getText() == "Pause") {
			btnPauseActionPerformed();
		} else {
			btnResumeActionPerformed();
		}
	}
	
	private void btnResumeActionPerformed() {										//Resume the GUI in Pause
		flagPause = false;															//Flag Pause reinitialised
		btnPause.setText("Pause");
		chart.reInitChart();														//Reinit Chart as the data are discontinued anyway
		txtMonitor.setText("Resume, Chart cleared with success !");
	}
	
	private void btnPauseActionPerformed() {										//Put the GUI in Pause
		if (communicator.getConnected() == true) {	
			flagPause = true;														//Flag Pause
			btnPause.setText("Resume/Reset");
			txtMonitor.setText("PAUSE !");
		} else {
			txtMonitor.setText("Arduino not connected !");
		}
	}
	
	private void btnResetChartActionPerformed() {									//Reinit Chart
		chart.reInitChart();
		txtMonitor.setText("Chart cleared with success !");
	}
	
	public void btnExportActionPerformed() {										//Export what it is displayed in the chart to an Excel file at the location of the applet
		try {
		excelCreator = new ExcelCreator(txtTest.getText() + ".xls",					//Create the workbook
		   slider_P.getValue() / DECIMAL, 
		   slider_I.getValue() / DECIMAL, 
		   slider_D.getValue() / DECIMAL,
		   slider_Angle.getValue(),
		   slider_Motor.getValue());
		excelCreator.addData( this.getChart().getXYSeriesCollection() );			//Add the from the chart
		excelCreator.createFile();													//Create an Excel file at the location of the applet
		flagProgress = true;														//Enable Progress bar cycle
		txtMonitor.setText("New Excel File exported with success !");
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public JComboBox<String> getComboBoxPort() {
		return comboBoxPort;
	}

	public Animation getAnimation() {
		return animation;
	}

	public Communicator getCommunicator() {
		return communicator;
	}
	
	public JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public JTextField getTxtMonitor() {
		return txtMonitor;
	}
	
	public JTextField getTxtTimeCapture() {
		return txtTimeCapture;
	}
	
	public void setTxtMonitor(String txt) {
		txtMonitor.setText(txt);
	}
	
	public void setTxtBtnConnect(String txt) {
		btnConnect.setText(txt);
	}

	public Chart getChart() {
		return chart;
	}
	
	public boolean getFlagProgress() {
		return flagProgress;
	}
	
	public void setFlagProgress( boolean flagP) {
		flagProgress = flagP;
	}
	
	public boolean getFlagPause() {
		return flagPause;
	}
	
	public boolean getFlagAutomatic() {
		return flagAutomatic;
	}
	
	public void setFlagAutomatic(boolean flagP) {
		flagAutomatic = flagP;
	}
	
	public JCheckBox getChckAutomatic() {
		return chckAutomatic;
	}
	
}
