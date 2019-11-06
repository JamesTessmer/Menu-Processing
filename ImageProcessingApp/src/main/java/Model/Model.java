package Model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import application.Controller;

/**
 * This class is responsible for holding all the data pertaining to the menus
 * @author Jacob
 *
 */
public class Model {

	public int fileCounter, scannedCounter;
	private ArrayList<File> imagesToScan;
	private ArrayList<Rectangle> templateBoxes;
	private ArrayList<String> imagesInList;
	private ArrayList<Character> selections;
	private ArrayList<Client> clientList;
	
	private ArrayList<Client> needAttentionList;
	
	
	///////////////////////////
	private Controller c;
	
	public Model(Controller c) {
		this.c = c;
		this.fileCounter = 0;
		this.scannedCounter = 0;
		this.imagesToScan = new ArrayList<File>();
		this.templateBoxes = new ArrayList<Rectangle>();
		this.imagesInList = new ArrayList<String>();
		this.selections = new ArrayList<Character>();
		this.clientList = new ArrayList<Client>();
		this.needAttentionList = new ArrayList<Client>();
	}
	
	/**
	 * Whenever the "Import Menus" button is pushed, clear lists to prevent duplicated
	 * lists/ prevent combining two different client menu image folders
	 */
	public void clearImageLists()
	{
		imagesToScan.clear();
		imagesInList.clear();
		selections.clear();
		fileCounter = 0;
		scannedCounter = 0;
	}
	
	/**
	 * Creates a new client and inputs their selections and ID into a list
	 * After client's selections have been added, clear the list to prepare for
	 * then next client's selections to be added.
	 */
	public void addSelectionsToClient(String clientIm, int clientID)
	{
		boolean clientNeedsAttention = false;
		for(Character c : selections)
		{
			if(c == '0')
			{
				clientNeedsAttention = true;
				System.out.println("NEEDS ATTENTION");
				break;
			}
		}
		System.out.println(clientNeedsAttention);
		Client clientTmp = new Client(clientID, clientNeedsAttention, clientIm); //add current selections list to client // Default random value to ID until OCR is added
		for(Character c : selections)
		{
			clientTmp.getSelections().add(c); //add the current selections in the list to a client object
		}
		
		if(clientNeedsAttention) 
		{
			needAttentionList.add(clientTmp);
		}
		clientList.add(clientTmp);
		selections.clear(); //Clear the list to prepare for the next client image to be processed

	}
	
	/**
	 * Log all client's selections into console
	 */
	public void debugClientList()
	{
		for(Client c : clientList)
		{
			c.debugSelections();
		}
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

	public ArrayList<Character> getSelections() {
		return selections;
	}

	public void setSelections(ArrayList<Character> selections) {
		this.selections = selections;
	}

	public ArrayList<String> getImagesInList() {
		return imagesInList;
	}

	public void setImagesInList(ArrayList<String> imagesInList) {
		this.imagesInList = imagesInList;
	}

	public ArrayList<Client> getClientList() {
		return clientList;
	}

	public void setClientList(ArrayList<Client> clientList) {
		this.clientList = clientList;
	}

	public ArrayList<Client> getNeedAttentionList() {
		return needAttentionList;
	}

	public void setNeedAttentionList(ArrayList<Client> needAttentionList) {
		this.needAttentionList = needAttentionList;
	}
	
	
}
