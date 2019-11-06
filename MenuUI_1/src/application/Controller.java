package application;


import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
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
	public Text txtFolderLocation, 
				txtNumCompleted;
	@FXML
	public ProgressBar progressBar;
	
	@FXML
	public ImageView imageView;
	
	@FXML
	public Button scanButton;
	
	///////////////////////////////
	
	private Model model = new Model(this);
	private HashMap<Integer, File> fileHash = new HashMap<>();
	
	@FXML
	public void onImportTemplateClick() throws IOException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Blank Menu Template Image");
		String filePath = fc.showOpenDialog(templateItemImport.getParentPopup().getScene().getWindow()).toString();
		scanTemplateImage(filePath);
	}
	
	@FXML
	public void onImportClick() {
		filterFileList(getFilesInDirectory());
		updateProgressBar();
	}
	
	@FXML
	public void onScanClick() throws IOException {
		System.out.println("boop");
		ArrayList<File> targetFolder = model.getImagesToScan();
		
		//Process files from target folder using Canny/Harris/Pixel Extraction
		for(int i = 0; i < targetFolder.size(); i++) {
			System.out.println(targetFolder.get(i).getAbsolutePath());
			model.scannedCounter++;
			determineMenuSelection(targetFolder.get(i).getAbsolutePath());
			for(char c : model.getSelections()) {
				System.out.println(c);
			}
			updateProgressBar();
		}

	}
	
	private void filterFileList(File[] targetFolder) {
		Integer idx = 0; //Key
		for(File f : targetFolder) {
			if(f.isFile() && !fileHash.containsValue(f)) {
				fileHash.put(idx, f);
				idx++;
				model.fileCounter++;
				model.getImagesToScan().add(f);
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
	/**
	 * Update the current progress of the scanning process
	 */
	private void updateProgressBar() {
		double current = model.scannedCounter;
		double total = model.fileCounter;
		double percentage = (double)(current/total);
		
		//Update Progress indicators
		txtNumCompleted.setText((int) current + " of " + (int) total + " Completed (" + Math.round(percentage*100) + "%)");
		progressBar.setProgress(percentage);
	}
	
	private void scanTemplateImage(String filePath) throws IOException {
		//Take the path to the current Template Image
		//Put through Canny/Harris to obtain pertinent coordinates, then
		//perform actual client scans
		
		//Do Canny First
		//Then Harris
		//Then Pixel Extraction
		BufferedImage tmp = Canny.getCImage(filePath);
		Image currentMenu = SwingFXUtils.toFXImage(tmp, null);
		imageView.setImage(currentMenu);
		
		ArrayList<Rectangle> rects = Harris.getSelectionBoxes(tmp);
		model.setTemplateBoxes(rects);
	}
	
	private void determineInitSelections(ArrayList<Rectangle> rects, ArrayList<Rectangle> used, BufferedImage currentImage) {
		//Regular or Sweet:
		boolean reg = searchBlackPixels(rects.get(0), currentImage);
		boolean sweet = searchBlackPixels(rects.get(1), currentImage);
		used.add(rects.get(0));
		used.add(rects.get(1));
		
		if(reg) {
			model.getSelections()[0] = 'R';
		} else if(sweet) {
			model.getSelections()[0] = 'W';
		}
		
		//Drink:
		for(int i = 2; i < 7; i++) {
			boolean drink = searchBlackPixels(rects.get(i), currentImage);
			if(drink) {
				switch(i) {
				case 2:
					model.getSelections()[1] = 'S';
					return;
				case 3:
					model.getSelections()[1] = '2';
					return;
				case 4:
					model.getSelections()[1] = 'C';
					return;
				case 5:
					model.getSelections()[1] = 'O';
					return;
				case 6:
					model.getSelections()[1] = 'T';
					return;
					default:
						model.getSelections()[1] = '0';
						break;
				}
			}
		}
	}

	private void determineMenuSelection(String filePath) throws IOException {
		// TODO Auto-generated method stub
		BufferedImage currentImage = Utils.readInImage(filePath);
		ArrayList<Rectangle> boxes = model.getTemplateBoxes();
		ArrayList<Rectangle> used = new ArrayList<>();
		determineInitSelections(boxes, used, currentImage);
		for(int i = 7; i < boxes.size(); i++) { // Start at i = 2 for initial selections
			
			if(!used.contains(boxes.get(i))) {
				boolean a = searchBlackPixels(boxes.get(i), currentImage);
				boolean b = searchBlackPixels(boxes.get(i + 5), currentImage);
				if(a) {
					model.getSelections()[i] = 'A';
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				} else if(b) {
					model.getSelections()[i] = 'B';
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				} else {
					model.getSelections()[i] = 'A';
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				}
			}
		}
	}
	
	private boolean searchBlackPixels(Rectangle r, BufferedImage currentImage) {
		int numShaded = 0;
		for(int x = r.x; x < r.x + r.width; x++) {
			for(int y = r.y; y < r.y + r.height; y++) {
				int[] pixel = currentImage.getRaster().getPixel(x, y, new int[3]);
				RGB color = new RGB(pixel[0], pixel[1], pixel[2]);
				
				if(color.isShaded()) {
					numShaded++;
					int[] c = {0,255,0};
					currentImage.getRaster().setPixel(x, y, c);
				}
			}
		}
		Image tmp = SwingFXUtils.toFXImage(currentImage, null);
		this.imageView.setImage(tmp);
		if(numShaded >= 25) {
			return true;
		} else {
			return false;
		}
	}
	
	
}
