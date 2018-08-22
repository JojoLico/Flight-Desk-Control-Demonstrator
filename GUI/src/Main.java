/**                          **/
/* Flight Desk Demonstrator */
 /*                          */
 /*   @author Joris DURAN    */
 /*   @version 1.0           */
/**                          **/

/*----------------Main--------------*/
/*----------Start Processus---------*/

import java.awt.EventQueue;

	//------Functions------//

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Processus processus = new Processus();	//Contain GUI and start thread
					processus.start();
				} catch (Exception e) {
					e.printStackTrace();					//Print errors into the console
				}
			}
		});
	}
	
}
