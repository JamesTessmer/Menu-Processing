package application;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

/**
 * This class is responsible for holding all the data pertaining to the menus
 * @author Jacob
 *
 */
public class Model {

	public int fileCounter, scannedCounter;
	private ArrayList<File> imagesToScan;
	private ArrayList<Rectangle> templateBoxes;
	
	//Only one list of choices for Testing Purposes:
	private char[] selections;
	
	private Controller c;
	
	public Model(Controller c) {
		this.c = c;
		this.fileCounter = 0;
		this.scannedCounter = 0;
		this.imagesToScan = new ArrayList<>();
		this.templateBoxes = new ArrayList<>();
		this.selections = new char[62];
	}

	public ArrayList<File> getImagesToScan() {
		return imagesToScan;
	}

	public void setImagesToScan(ArrayList<File> imagesToScan) {
		this.imagesToScan = imagesToScan;
	}

	public ArrayList<Rectangle> getTemplateBoxes() {
		return templateBoxes;
	}

	public void setTemplateBoxes(ArrayList<Rectangle> templateBoxes) {
		this.templateBoxes = templateBoxes;
	}

	public char[] getSelections() {
		return selections;
	}

	public void setSelections(char[] selections) {
		this.selections = selections;
	}

	

	
	
	
	
}
