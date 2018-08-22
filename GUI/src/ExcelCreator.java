
/*-------------ExcelCreator-------------*/
/*-----Export Data in an Excel File-----*/

import java.io.File;
import java.io.IOException;

import org.jfree.data.xy.XYSeriesCollection;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelCreator {
	
	// ------Attributes------//
	
	private WritableWorkbook workbook;																//Object to crate a new Excel file
	private WritableSheet sheetResults;																//Excel sheet where data are written
	private final static WritableFont TITLE_FONT = new WritableFont(WritableFont.ARIAL, 			//Font for titles
			12, 
			WritableFont.BOLD, 
			true,
			UnderlineStyle.NO_UNDERLINE, 
			Colour.BLUE, 
			ScriptStyle.NORMAL_SCRIPT);
	private final static WritableCellFormat TITLE_FORMAT = new WritableCellFormat(TITLE_FONT);		//Format for titles

	// ------Functions------//
	
	public ExcelCreator(String fileName, double Pp, double Ip, double Dp, int angleP, int motorP) throws IOException, RowsExceededException, WriteException {
																									//Create new Excel file
			workbook = Workbook.createWorkbook(new File(fileName));
			
			sheetResults = workbook.createSheet("Results", 0);                   
			sheetResults.addCell(new Label(0, 0, "Fligh Desk Demonstrator", TITLE_FORMAT)); 		// Create new label at column 0, line 0 with a format
			sheetResults.addCell(new Label(0, 1, "Results", TITLE_FORMAT));
			
			sheetResults.addCell(new Label(0, 2, "time in s", TITLE_FORMAT));
			sheetResults.addCell(new Label(1, 2, "alpha in degree", TITLE_FORMAT));
			
			sheetResults.addCell(new Label(2, 2, "time in s", TITLE_FORMAT));
			sheetResults.addCell(new Label(3, 2, "beta in degree", TITLE_FORMAT));
			
			sheetResults.addCell(new Label(4, 2, "time in s", TITLE_FORMAT));
			sheetResults.addCell(new Label(5, 2, "Command angle in degree", TITLE_FORMAT));
			
			sheetResults.addCell(new Label(6, 2, "time in s", TITLE_FORMAT));
			sheetResults.addCell(new Label(7, 2, "Air velocity", TITLE_FORMAT));

			sheetResults.addCell(new Label(9, 0, "Parameters", TITLE_FORMAT));
			sheetResults.addCell(new Label(9, 1, "Proportionnal: " + Pp, TITLE_FORMAT));			//Parameters
			sheetResults.addCell(new Label(9, 2, "Integral: " + Ip, TITLE_FORMAT));
			sheetResults.addCell(new Label(9, 3, "Derivative: " + Dp, TITLE_FORMAT));
			sheetResults.addCell(new Label(9, 4, "Angle command: " + angleP + "deg", TITLE_FORMAT));
			sheetResults.addCell(new Label(9, 5, "Motor Speed: " + motorP + "%", TITLE_FORMAT));
	}

	public void addData(XYSeriesCollection dataset) throws RowsExceededException, WriteException  {															//Write data into the Excel sheet

			int starLine = 4;
			for(int i = 0; i < dataset.getSeriesCount(); i++) {
				for(int j = 0; j < dataset.getSeries(i).getItemCount(); j++) {
					sheetResults.addCell( new Number(2*i, starLine+j, (double) dataset.getSeries(i).getX(j)) );	//Write time in ms
					sheetResults.addCell(new Number(2*i+1, starLine+j, (double) dataset.getSeries(i).getY(j)) );//Write angle in degree
				}
			}			
	}
	
	public void createFile() throws IOException, WriteException  {					//Export the Excel file at the location of the applet
			workbook.write();
			workbook.close();	
	}
	
}





