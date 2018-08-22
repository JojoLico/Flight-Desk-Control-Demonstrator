import java.io.IOException;
import javax.swing.JProgressBar;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/*-------------Processus-------------*/
/*-----Update Chart and Animation----*/

public class Processus extends Thread {

	// ------Attributes------//
	
	private final GUI gui;																			//The frame GUI
	private int progress       = 0;																	//Variable used to for the progression bar displayed on the GUI
	private int timeCapture    = 0;
	private boolean flagFilter = true;																//Allow or not the data received to be used especially right after the Arduino connection
	private final static int THREAD_SLEEP = 24;														//Delay of the loop on ms, inferior to the Arduino data sending loop

	// ------Functions------//

	public void run() {																				//Contain the loop, run automatically
		while (true) {																				//Loop
			try {
				//System.out.println();                                                				//Debug
				if (gui.getFlagProgress()) inProgress(gui.getProgressBar());						//Run the progress bar
				if (gui.getFlagAutomatic() ) automaticCapture();									//Run Automatic capture
				if (gui.getCommunicator().getConnected()) {											//Update chart and animation from the GUI
					if (gui.getFlagPause() == false && !flagFilter) {
						gui.getCommunicator().serialReadData(gui);
						gui.getChart().updateChart(gui.getCommunicator());
					    gui.getAnimation().updateAnimation(gui.getChart());
					}
				} else {
					if (gui.getCommunicator().getScanConnectBT() == false) gui.getCommunicator().searchForPorts(gui);//Search for new Serial ports
					flagFilter = true;																				 //Reallow the filter in case of a new connection
				}
				Processus.sleep(THREAD_SLEEP);
			} catch (InterruptedException | IOException e) {
				gui.btnConnectDisconnectActionPerformed();
				e.printStackTrace();
			}
		}
	}

	public Processus() throws ParserConfigurationException, SAXException, IOException, InterruptedException {
		gui = new GUI();									//Create the GUI
		gui.setVisible(true);
	}
	
	private void inProgress(JProgressBar progressBar) {		//Fill the progress bar and 
		progressBar.setValue(progress);						//Fill in %
		progressBar.setString(progress + "%");				//Add the value to be displayed in %
		progress++;											//Increment to fill the progression bar displayed on the GUI
		if ( progress > progressBar.getMaximum() ) {		//Reinit progress bar
			gui.setFlagProgress(false);
			progress = 0;									
			flagFilter = false;								//Disable the filter
			progressBar.setValue(progress);
			progressBar.setString("");
		}
	}
	
	private void automaticCapture () {															//Run the automatic capture feature
		timeCapture = timeCapture + THREAD_SLEEP;
		try {
			if (timeCapture >= Integer.parseInt( gui.getTxtTimeCapture().getText() )) {			//Read the value from the user
				gui.setFlagAutomatic(false);													//Reinit the automatic capture feature
				gui.getChckAutomatic().setSelected(false);
				timeCapture = 0;
				gui.btnExportActionPerformed();													//Export the Excel file
			}
		} catch ( NumberFormatException e) {													//In case the user made a mistake
			gui.setTxtMonitor("Error, Capture Automatic, input time (ms) not a number !");
			gui.setFlagAutomatic(false);														//Reinit the automatic capture feature
			gui.getChckAutomatic().setSelected(false);
			timeCapture =0;
		}
	}

}
