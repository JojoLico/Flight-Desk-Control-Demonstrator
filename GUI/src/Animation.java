
/*------------Animation-------------*/
/*-------Display the Aerofoil-------*/

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.JLabel;

public class Animation extends JPanel {

	// ------Attributes------//

	private static final long serialVersionUID = 6585426869841017126L;
	private static final int FACTOR_AEROFOIL = 500;																			//Factor to scale the aerofoil displayed
	private static final int FACTOR_AILERON = 1800;																			//actor to scale the aileron displayed
	private static final int[] xAEROFOIL = { 0, 2, 4, 10, 50, 100, 200, 300, 400, 500, 600, 700, 810, 800 };				//Abscisses aerofoil geometry
	private static final int[] yAEROFOIL = { 0, 6, 13, 20, 36, 47, 57, 60, 58, 53, 46, 37, 26, 0 };							//Thickness aerofoil geometry
	private static final int[] xAILERON  = { 0, 2, 4, 10, 50, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000 };			//Abscisses aileron geometry
	private static final int[] yAILERON  = { 0, 6, 25, 60, 70, 80, 75, 70, 65, 55, 45, 35, 26, 14, 0 };						//Thickness aileron geometry
	private double angleAerofoil;
	private double angleAileron;
	private double airVelocity;
	private JLabel aerofoilAngleLabel;
	private JLabel aileronAngleLabel;
	private JLabel airVelocityLabel;

	// ------Functions------//

	public Animation() {											//Create the panel animation
		setLayout(null);

		aerofoilAngleLabel = new JLabel();
		aerofoilAngleLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		aerofoilAngleLabel.setBounds(10, 10, 170, 20);
		add(aerofoilAngleLabel);

		aileronAngleLabel = new JLabel();
		aileronAngleLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		aileronAngleLabel.setBounds(10, 40, 170, 20);
		add(aileronAngleLabel);
		
		airVelocityLabel = new JLabel();
		airVelocityLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		airVelocityLabel.setBounds(10, 70, 170, 20);
		add(airVelocityLabel);

		updateLabel();												//Write initial values
	}

	public void paintComponent(Graphics graph) {					//Homotetie on the geometries displayed
		super.paintComponent(graph);

		int width = getWidth() / 2;
		int height = getHeight() / 2;

		Graphics2D aerofoil = (Graphics2D) graph;																				//Create the object to modify the geometries
		
		aerofoil.rotate(Math.toRadians(-angleAerofoil), width, height);															//Rotation of the aerofoil
		aerofoil.setColor(Color.blue);
		aerofoil.fillPolygon( xAerofoilSizing (width), yAerofoilSizing (height), 2*xAEROFOIL.length);							//Draw aerofoil

		aerofoil.rotate(Math.toRadians(angleAileron), width + width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL), height);	//Rotation of the aileron
		aerofoil.setColor(Color.blue);
		aerofoil.fillPolygon( xAileronSizing (width), yAileronSizing (height), 2*xAILERON.length);								//Draw aileron

		aerofoil.rotate(Math.toRadians(-angleAileron), width + width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL), height);//Back before the aileron rotation to keep the labels horizontal
		aerofoil.rotate(Math.toRadians(angleAerofoil), width, height);															//Back before the aerofoil rotation to keep the labels horizontal
	}

	public void updateLabel() {																	//Display the new values of the angles
		aerofoilAngleLabel.setText("Aerofoil Angle: " + angleAerofoil + " deg");
		aileronAngleLabel.setText("Aileron Angle: " + angleAileron + " deg");
		airVelocityLabel.setText("Air Velocity: " + airVelocity + " m/s");
	}

	public void updateAnimation(Chart chart) {													//Refresh the frame to keep it up to date
		
		int index = chart.getAoAserie().getItemCount() - 1;
		
		if (index >= 0) {
			angleAerofoil   = (double) chart.getAoAserie().getY(index);
			angleAileron    = (double) chart.getFlapAngleSerie().getY(index);
			airVelocity     = (double) chart.getAirVelocitySerie().getY(index);
			updateLabel();
			repaint();
		}
	}
	
	private int[] xAerofoilSizing (int width) {													//Resize the abscisses of aerofoil geometry to fit the window
		
		int nbPoint = xAEROFOIL.length;
		int[] xAerofoilShape = new int [nbPoint * 2];											//Time 2 because of the symmetry
		
		for(int i=0; i<nbPoint; i++) {
			xAerofoilShape[i] = width - width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL) + xAEROFOIL[i] * width/(2*FACTOR_AEROFOIL);			//Time 2 because of the thickness below is not divided by 2
			xAerofoilShape[nbPoint+i] = width - width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL) + xAEROFOIL[i] * width/(2*FACTOR_AEROFOIL);
		}
		return xAerofoilShape;
	}
	
	private int[] yAerofoilSizing (int height) {												//Resize the ordinates of the aerofoil geometry to fit the window
		
		int nbPoint = yAEROFOIL.length;
		int[] yAerofoilShape = new int [nbPoint * 2];											//Time 2 because of the symmetry
		
		for(int i=0; i<nbPoint; i++) {
			yAerofoilShape[i] = height + yAEROFOIL[i] * height/FACTOR_AEROFOIL;					//Thickness not divided by 2 to keep it simple but taken into account above
			yAerofoilShape[nbPoint+i] = height - yAEROFOIL[i] * height/FACTOR_AEROFOIL;			//Symmetry
		}
		return yAerofoilShape;
	}
	
	private int[] xAileronSizing (int width) {													//Resize the abscisses of aileron geometry to fit the window
		
		int nbPoint = xAILERON.length;
		int[] xAileronShape = new int [nbPoint * 2];											//Time 2 because of the symmetry
		
		for(int i=0; i<nbPoint; i++) {
			xAileronShape[i] = width + width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL) + xAILERON[i] * width/(2*FACTOR_AILERON);		//Time 2 because of the thickness below is not divided by 2
			xAileronShape[nbPoint+i] = width + width*xAEROFOIL[xAEROFOIL.length-1]/(4*FACTOR_AEROFOIL) + xAILERON[i] * width/(2*FACTOR_AILERON);
		}
		return xAileronShape;
	}
	
	private int[] yAileronSizing (int height) {													//Resize the ordinates of the aerofoil geometry to fit the window
		
		int nbPoint = yAILERON.length;
		int[] yAileronShape = new int [nbPoint * 2];											//Time 2 because of the symmetry

		for(int i=0; i<nbPoint; i++) {
			yAileronShape[i] = height + yAILERON[i] * height/FACTOR_AILERON;					//Thickness not divided by 2 to keep it simple but taken into account above
			yAileronShape[nbPoint+i] = height - yAILERON[i] * height/FACTOR_AILERON;			//Symmetry
		}
		return yAileronShape;
	}
}
