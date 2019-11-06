package imUtils;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import Model.Model;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

public final class MenuScanner {

	
	/**
	 * determineInitSelections(...)
	 * This method is reponsible for the initial selections in the menu --
	 * Regular Dessert / Low Sugar Dessert
	 * Type of Drink (Skim Milk, 2% milk, Chocolate Milk, Orange Juice, Unsweet Tea)
	 * @param rects -- List of ORDERED selection box boundaries
	 * @param used -- List of USED selection box boundaries
	 * @param currentImage -- used to pass into searchBlackPixels(Rectangle r, BufferedImage b)
	 */
	public static void determineMenuSelection(String filePath, Model model) throws IOException 
	{
		BufferedImage currentImage = ImageCVT.readInImage(filePath);
		ArrayList<Rectangle> boxes = model.getTemplateBoxes();
		ArrayList<Rectangle> used = new ArrayList<Rectangle>();
		determineInitSelections(boxes, used, currentImage,model);
		for(int i = 7; i < boxes.size(); i++) { // Start at i = 2 for initial selections
			
			if(!used.contains(boxes.get(i))) {
				boolean a = searchBlackPixels(boxes.get(i), currentImage);
				boolean b = searchBlackPixels(boxes.get(i + 5), currentImage);
				
				if(a && b)
				{
					model.getSelections().add('0');
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				}
				else if(a)
				{
					model.getSelections().add('A');
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				} 
				else if(b)
				{
					model.getSelections().add('B');
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				} 
				else 
				{
					model.getSelections().add('0');
					used.add(boxes.get(i));
					used.add(boxes.get(i + 5));
				}
			}
		}
	}
	
	/**
	 * determineInitSelections(...)
	 * This method is reponsible for the initial selections in the menu --
	 * Regular Dessert / Low Sugar Dessert
	 * Type of Drink (Skim Milk, 2% milk, Chocolate Milk, Orange Juice, Unsweet Tea)
	 * @param rects -- List of ORDERED selection box boundaries
	 * @param used -- List of USED selection box boundaries
	 * @param currentImage -- used to pass into searchBlackPixels(Rectangle r, BufferedImage b)
	 */
	private static void determineInitSelections(ArrayList<Rectangle> rects, ArrayList<Rectangle> used, BufferedImage currentImage, Model model) 
	{
		//Regular or Sweet:
		boolean reg = searchBlackPixels(rects.get(0), currentImage);
		boolean sweet = searchBlackPixels(rects.get(1), currentImage);
		used.add(rects.get(0));
		used.add(rects.get(1));
		
		//Determine if desert is regular or low sugar
		if(reg && sweet)
		{
			model.getSelections().add('0');
		}
		else if(reg) 
		{
			model.getSelections().add('R');
		} else if(sweet) {
			model.getSelections().add('L');
		}
		
		//Determine the type of drink:
		//	7 boxes to cycle through
		for(int i = 2; i < 7; i++) {
			boolean drink = searchBlackPixels(rects.get(i), currentImage);
			if(drink) {
				switch(i) {
				case 2:
					model.getSelections().add('S');
					return;
				case 3:
					model.getSelections().add('2');
					return;
				case 4:
					model.getSelections().add('C');
					return;
				case 5:
					model.getSelections().add('O');
					return;
				case 6:
					model.getSelections().add('T');
					return;
					default:
						model.getSelections().add('0');
						break;
				}
			}
		}
	}
	
	/**
	 * Iterates through every pixels found within our boundary area. Determines
	 * whether the current pixel is shaded (RGB < (10,10,10)). If the pixel is
	 * shaded, increment the shaded counter. Finally, if the number of pixels shaded
	 * is greater than 25, infer that the box has been selected and return a boolean
	 * value. 
	 * @param r --The selection box area to be searched
	 * @param currentImage -- The current client menu image to be processed
	 * @return boolean value for whether numShaded is >= 25, or <=25
	 */
	private static boolean searchBlackPixels(Rectangle r, BufferedImage currentImage) {
		int numShaded = 0;
		for(int x = r.x; x < r.x + r.width; x++) {
			for(int y = r.y; y < r.y + r.height; y++) {
				int[] pixel = currentImage.getRaster().getPixel(x, y, new int[3]);
				RGB color = new RGB(pixel[0], pixel[1], pixel[2]);
				
				if(color.isShaded()) {
					numShaded++;
				}
			}
		}

		if(numShaded >= 25) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Take the path to the current template image (filePath : String) and
	 * perform Canny/Harris methods to obtain selection box boundary coordinates.
	 * This list of rectangles is then passed to our Model to be used in scanning the
	 * client menu images;
	 * @param filePath - path to template image
	 * @throws IOException
	 */
	public static void scanTemplateImage(ImageView iv, String filePath, Model model) throws IOException {
		//Take the path to the current Template Image
		//Put through Canny/Harris to obtain pertinent coordinates, then
		//perform actual client scans
		
		//Do Canny First
		//Then Harris
		//Set template boxes
		BufferedImage tmp = Canny.getCImage(filePath);
		iv.setImage(SwingFXUtils.toFXImage(tmp, null));
		ArrayList<Rectangle> rects = Harris.getSelectionBoxes(tmp);
		model.setTemplateBoxes(rects);
	}
}
