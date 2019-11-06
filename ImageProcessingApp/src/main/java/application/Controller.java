package application;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bytedeco.javacpp.tesseract.TessBaseAPI;

import Model.Client;
import Model.Model;
import Model.Selections;
import imUtils.CSVWriter;
import imUtils.FXTableUI;
import imUtils.MenuScanner;
import imUtils.OCR;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * This class is responsible for passing/updating information from the Model to the View (Menu_UI.fxml)
 * @author Jacob
 *
 */
public class Controller {
	@FXML
	public Pane pane;
	
	@FXML
	public MenuItem menuItemImport;
	
	@FXML
	public MenuItem templateItemImport;
	
	@FXML
	public MenuItem exportCSV;
	
	@FXML 
	public Text txtFolderLocation, 
				txtNumCompleted;
	
	@FXML
	public Label lblNeedDecisions,
				 lblClientID;
	
	@FXML
	public ProgressBar progressBar;
	
	@FXML
	public ImageView imageView;
	
	@FXML
	public Button scanButton, backButton, nextButton, editSelection;
	
	@FXML
	public ProgressBar pb;
	
	@FXML
	public TableColumn<Selections, String> colDay; 
	
	@FXML
	public TableColumn<Selections, String> colChoice;
	
	@FXML
	public javafx.scene.control.TableView<Selections> tableView;
	
	
	///////////////////////////////
	
	private Model model = new Model(this);
	private int currentPositionInClientList = -1;
	
	@FXML
	public void onImportTemplateClick() throws IOException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Blank Menu Template Image");
		String filePath = fc.showOpenDialog(templateItemImport.getParentPopup().getScene().getWindow()).toString();
		if(filePath.equals(null)) return;
		scanTemplateImage(filePath);
	}
	
	@FXML
	public void onImportClick()
	{
		//Clear current lists:
		model.clearImageLists();
		filterFileList(getFilesInDirectory());
		updateProgressBar();
		
		turnButtonsOnOrOff(false);
	}
	
	@FXML
	public void onExportCSV()
	{
		System.out.println("BOOP");
		CSVWriter.writeToCSV(model.getClientList());
	}
	
	@FXML
	public void onScanClick() throws IOException {
		turnButtonsOnOrOff(true); //Turn buttons off
		Thread startProcessingFiles = new Thread(new bg_Thread());
		startProcessingFiles.start();
	}
	
	@FXML
	public void onBackClick() throws IOException
	{
		pageBackward();
	}
	
	@FXML
	public void onNextClick() throws IOException
	{
		pageForward();
	}
	
	@FXML
	public void onEditClick(CellEditEvent<Selections, String> event)
	{
		String nSelection = event.getNewValue();
		Selections s = tableView.getSelectionModel().getSelectedItem();
		//This will both update the UI view, and the tableValues in a client
		s.setSelection(nSelection);
	}
	
	@FXML
	public void onViewClick() throws IOException
	{
		String imPath = null;
		//Run a loop determining the first index and client that has "needsDecision == true" 
		//Then jump to that client and update UI
		for(int i = 0; i < model.getClientList().size(); i++)
		{
			if(model.getClientList().get(i).isNeedsDecision())
			{
				currentPositionInClientList = i;
				imPath = model.getClientList().get(i).getImPath();
				break;
			}
		}
		//Update the user's view and update client ID, and reset table to match client info
		updateImageView(imPath);
		updateText();
		setTable();
	}
	
	@FXML
	public void onCommitClick()
	{
		//Remove client from parallel array that is keeping track of those that need attention:
		Client currentClient = model.getClientList().get(currentPositionInClientList);
		model.getNeedAttentionList().remove(currentClient);
		//Update actual clients needs decisions variable
		currentClient.setNeedsDecision(false);
		updateText();
	}
	
	/**
	 * Turns buttons off if isOn is true
	 * Turns buttons on if isOn is false
	 * @param isOn
	 */
	private void turnButtonsOnOrOff(boolean isOn)
	{
		backButton.setDisable(isOn);
		nextButton.setDisable(isOn);
		scanButton.setDisable(isOn);
	}
	
	/**
	 * Passes in the list of client menu images to be processed, and adds them into
	 * our model's list.
	 * @param targetFolder - list of files in target directory;
	 */
	private void filterFileList(File[] targetFolder) {
		for(File f : targetFolder) {
			if(f.isFile() 
					&& !model.getImagesInList().contains(f.getAbsolutePath())) {
				model.fileCounter++;
				model.getImagesToScan().add(f);
				model.getImagesInList().add(f.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Return a list of marked image files in a directory
	 * @return List of scanned in image files
	 */
	private File[] getFilesInDirectory() {
		//Show Directory Dialog
		DirectoryChooser dc = new DirectoryChooser();
		dc.setTitle("Select Menu File Directory");
		String folderPath = dc.showDialog(menuItemImport.getParentPopup().getScene().getWindow()).toString();
		
		//Update Folder location text
		txtFolderLocation.setText("Import Folder: " + folderPath);
		//Now return a list of all the files in the directory
		File targetFolder = new File(folderPath);
		
		return targetFolder.listFiles(); //TODO: This returns the names of ALL files in a dir, including subfolders
	}
	
	private void updateImageView(String imPath) throws IOException
	{
		Image currentImage = new Image(imPath);
		imageView.setImage(currentImage);
	}
	
	private void pageForward() throws IOException
	{
		if(currentPositionInClientList + 1 < model.getClientList().size())
		{
			currentPositionInClientList++;
		}

		String nextClientPath = model.getClientList().get(currentPositionInClientList).getImPath();
		updateImageView(nextClientPath);
		updateText();
		setTable();
	}
	
	private void pageBackward() throws IOException
	{
		if(currentPositionInClientList - 1 > -1) 
		{
			currentPositionInClientList--;
		}
		System.out.println(currentPositionInClientList);
		String previousClientPath = model.getClientList().get(currentPositionInClientList).getImPath();
		updateImageView(previousClientPath);
		updateText();
		setTable();
	}

	/**
	 * Set Table values with corresponding day and selections
	 */
	private void setTable()
	{
		Client currentClient = model.getClientList().get(currentPositionInClientList);
		FXTableUI.setTable(currentClient, tableView, colChoice, colDay);

	}
	
	/**
	 * Update the current progress of the scanning process
	 */
	private void updateProgressBar() {
		double current = model.scannedCounter;
		double total = model.fileCounter;
		double percentage = (double)(current/total);
		
		//Update Progress indicators
		txtNumCompleted.setText((int) current + " of " + (int) total + " Completed (" + Math.round(percentage*100) + "%)");
		pb.setProgress(percentage);
	}
	
	/**
	 * update the Label "needs decisions"
	 */
	private void updateText()
	{
		lblNeedDecisions.setText("⚠  " + model.getNeedAttentionList().size() + " Need Decisions");
		
		String clientID = "Client ID: " + model.getClientList().get(currentPositionInClientList).getClientID();
		if(model.getClientList().get(currentPositionInClientList).isNeedsDecision() == true)
		{
			clientID += " (NEEDS ATTENTION)";
		}
		lblClientID.setText(clientID);
	}
	
	/**
	 * Run an additional thread to update the current progress in our Progress Bar.
	 * and perform determineMenuSelection() to each image.
	 * @author Jacob
	 *
	 */
	class bg_Thread implements Runnable
	{

		public void run() {
			
			
			ArrayList<File> targetFolder = model.getImagesToScan();
			//Process files from target folder using Canny/Harris/Pixel Extraction
			for(int i = 0; i < targetFolder.size(); i++) {
				System.out.println(targetFolder.get(i).getAbsolutePath());
				model.scannedCounter++; //increment the number of images that have been scanned
				currentPositionInClientList++;//increment current position in list
				try {
					determineMenuSelection(targetFolder.get(i).getAbsolutePath()); //Scan current image
					int clientID = scanClientID(targetFolder.get(i).getAbsolutePath());
//					int clientID = (int) (Math.random() * 100);
					model.addSelectionsToClient(targetFolder.get(i).toURI().toURL().toExternalForm(), clientID);
					model.getClientList().get(i).fillTableValues();
					updateImageView(targetFolder.get(i).toURI().toURL().toExternalForm()); //Update the imageView with the current client image
					updateProgressBar(); //Update actual progress bar
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			tableView.setEditable(true);
			colChoice.setEditable(true);
			colChoice.setCellFactory(TextFieldTableCell.forTableColumn()); //Allow for individual cells to be edited
			setTable();
			turnButtonsOnOrOff(false);//Turn buttons on
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					//Update Text after thread has finished
					updateText();
				}
			});
		}
		
	}
	
	/**
	 * Utilize OCR to read image and pull Client ID from text
	 * **NOTE: this scans the current client image once, then return a string with
	 * ALL the text read in from the image.**
	 * @param filePath
	 * @return
	 */
	private int scanClientID(String filePath)
	{
		TessBaseAPI nInstance = OCR.generateTessInstance("");
		String allText = OCR.readImage(filePath, nInstance);
		String clientID = OCR.pullIDNum(allText);
		return Integer.parseInt(clientID);
	}
	
	/**
	 * Take the path to the current template image (filePath : String) and
	 * perform Canny/Harris methods to obtain selection box boundary coordinates.
	 * This list of rectangles is then passed to our Model to be used in scanning the
	 * client menu images;
	 * @param filePath - path to template image
	 * @throws IOException
	 */
	private void scanTemplateImage(String filePath) throws IOException {
		MenuScanner.scanTemplateImage(imageView, filePath, model);
	}


	/**
	 * determineMenuSelection(String filePath)
	 * This method is responsible for determining whether the client has chosen selection A
	 * or selection B for this months menu. Starts index at '7' to account for the initial selection boxes.
	 * This method defaults to 'A' if both boxes are checked/empty
	 * 
	 * @param filePath -- absolute file path to current menu being processed
	 * @throws IOException
	 */
	private void determineMenuSelection(String filePath) throws IOException {
		MenuScanner.determineMenuSelection(filePath, model);
	}
}
