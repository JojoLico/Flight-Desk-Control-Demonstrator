

/*------------------SettingsReader-----------------*/
/*-----Read Default settings from the XML file-----*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SettingsReader {
	
	// ------Attributes------//
	
	private static final String DEFAULT_SETTINGS_XML= "Default_Settings_FDD.xml";
	private static final String INFORMATION_TXT= "Information_FDD.txt";
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document document;
	private Element racine;
	private NodeList racineNoeuds;
	private int nbRacineNoeuds;
	
	// ------Functions------//
	
	public SettingsReader () throws ParserConfigurationException, SAXException, IOException {		//Create new SettingsReader
	        factory = DocumentBuilderFactory.newInstance();											//Create object "DocumentBuilderFactory"
	       	builder = factory.newDocumentBuilder();													//Create parsor
	       	document = builder.parse(getClass().getResourceAsStream("/" + DEFAULT_SETTINGS_XML) );	//Document creation    	
		    racine = document.getDocumentElement();													//Get root
		    racineNoeuds = racine.getChildNodes();													//Get all settings
		    nbRacineNoeuds = racineNoeuds.getLength();												//Number of default settings
	}
	
	public String readNameSettings (int i) {														//Get settings name
		if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
			final Element settings = (Element) racineNoeuds.item(i);
			return settings.getAttribute("name");   
		}
		return null;
	}
	
	public double readPIDsettings (int i, String PID) {												//Get PID values
		if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
			final Element settings = (Element) racineNoeuds.item(i);
			final Element gain = (Element) settings.getElementsByTagName(PID).item(0);
			return Double.parseDouble( gain.getTextContent() );   
		}
		return 0;
	}
	
	public String readInformation () {																//Get Information from the text file
		try{
			InputStream flux = getClass().getResourceAsStream("/" + INFORMATION_TXT);
			InputStreamReader lecture = new InputStreamReader(flux);
			BufferedReader buff      = new BufferedReader(lecture);
			String ligne;
			String text = "";
			while ( (ligne = buff.readLine())!=null ){
				text = text + "\n" + ligne;
			}
			buff.close();
			return text;
	} catch (Exception e) {
		e.printStackTrace();
			return null;
		}
	}
	
	public int getNbRacineNoeuds() {
		return nbRacineNoeuds;
	}
	
}


