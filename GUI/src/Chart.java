
/*----------------Chart--------------*/
/*-------=---Chart creation=---------*/

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart {

	// ------Attributes------//

	private static final int MAX_POINT  = 600;												//Maximum samples displayed on the chart
	private static final double DECIMAL = 100.00;											//To divide int values from the buffer
	private static final double MILLIS  = 1000.00;											//To divide int ms from the buffer
	private JFreeChart chart;
	private XYSeriesCollection dataset = new XYSeriesCollection();
	private XYSeries AoAserie          = new XYSeries("Angle alpha");
	private XYSeries flapAngleSerie    = new XYSeries("Angle beta");
	private XYSeries CommandAngleSerie = new XYSeries("Angle Command");
	private XYSeries airVelocitySerie  = new XYSeries("Air Velocity");
	private double lastX = 0.00;															//Abscisses of the samples
	private int period   = 0;

	// ------Functions------//
	
	public Chart() {
		dataset.addSeries(AoAserie);
		dataset.addSeries(flapAngleSerie);
		dataset.addSeries(CommandAngleSerie);
		dataset.addSeries(airVelocitySerie);
		
		for(int i = 0; i < dataset.getSeriesCount(); i++) {									//Set the number max of samples displayed for each serie
			dataset.getSeries(i).setMaximumItemCount(MAX_POINT);
		}
		
		XYSeriesCollection datasetDisplayed = new XYSeriesCollection();
		datasetDisplayed.addSeries(AoAserie);
		datasetDisplayed.addSeries(flapAngleSerie);
		datasetDisplayed.addSeries(CommandAngleSerie);
		
		chart = ChartFactory.createXYLineChart("PID Controller Response", 					//Chart legend and titles
				"time in s", "Angles in degrees", datasetDisplayed,
				PlotOrientation.VERTICAL, true, true, true);
	}

	public void updateChart(Communicator communicator) {				 					     		//Update the chart
		
		double periodReceived = communicator.readData(Communicator.PERIOD_POS);					 		//Read data from the buffer
		double setPoint  	  = communicator.readData(Communicator.ECHO_SETPOINT_POS)  / DECIMAL;
		double yAerofoil 	  = communicator.readData(Communicator.AEROFOIL_ANGLE_POS) / DECIMAL;		//Values received from the serial port are int*100
		double yFlap          = communicator.readData(Communicator.AILERON_ANGLE_POS)  / DECIMAL;
		double airVelocity    = communicator.readData(Communicator.AIR_VELOCITY_POS)   / DECIMAL;
		
		if (period == 0 && periodReceived > 0 && periodReceived < 1000) period = (int) periodReceived;	 //Update the period when the Arduino is connected for the first time											
		
		//System.out.println(period);														//Debug
		
		if (periodReceived == period && period > 0) {										//Filtre in case of Port Com fail, s*** happens
			lastX = period/MILLIS + lastX;													//Incrementation of the abscisse
			AoAserie.add(lastX, yAerofoil);													//Add samples to the series
			flapAngleSerie.add(lastX, yFlap);
			CommandAngleSerie.add(lastX, setPoint);
			airVelocitySerie.add(lastX, airVelocity);
		} else if (periodReceived != 0) {													//Data corrupted, show must go on
			lastX = period/MILLIS + lastX;
		}
	}

	public void reInitChart() {																//Clear the chart
		for(int i = 0; i < dataset.getSeriesCount(); i++) {
			dataset.getSeries(i).clear();
		}
		lastX = 0.00;																		//Abscisse re-equal to 0
	}
	
	public XYSeries getAoAserie() {
		return AoAserie;
	}

	public XYSeries getAirVelocitySerie() {
		return airVelocitySerie;
	}

	public XYSeries getFlapAngleSerie() {
		return flapAngleSerie;
	}

	public XYSeries getCommandAngleSerie() {
		return CommandAngleSerie;
	}
	
	public JFreeChart getChart() {
		return chart;
	}
	
	public XYSeriesCollection getXYSeriesCollection() {
		return dataset;
	}

}
