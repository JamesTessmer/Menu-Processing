package imUtils;


import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;


public final class Harris {

	private final static int MAX_DIST = 70;
	private final static int THRESHOLD = 80;
	private static ArrayList<Point> coords;

	/**
	 * Main method in our Harris class. This method applies the Harris Corner Detection algorithm.
	 * It first converts a BufferedImage into a grayscale, the runs OpenCV's Imgproc.cornerHarris() algorithm.
	 * The coordinates found via Harris Corner Detection are then loaded into a list to be processed into appropriate
	 * selection boxes (rectangles). This list of boxes is then sorted appropriately and returned to be used in the controller
	 * for processing client menu images.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Rectangle> getSelectionBoxes(BufferedImage in) throws IOException {
			
			Mat source = ImageCVT.bImage2Mat(in);
			Mat sourceGray = new Mat();
			Mat fnl = Mat.zeros(sourceGray.size(), CvType.CV_32F);
			
			//Convert to GrayScale
			Imgproc.cvtColor(source, sourceGray, Imgproc.COLOR_BGR2GRAY);
			
			//Do Harris (using default values)
			Imgproc.cornerHarris(sourceGray, fnl, 2, 3, 0.04);
			
			//Normalize and convert to points
			Mat fnlNorm = new Mat();
			Mat fnlNormScaled = new Mat();
			Core.normalize(fnl, fnlNorm, 0, 255, Core.NORM_MINMAX);
			Core.convertScaleAbs(fnlNorm, fnlNormScaled);
			
			//Put normalized matrix into float array
			float[] dstNormData = new float[(int) (fnlNorm.total() * fnlNorm.channels())];
			fnlNorm.get(0, 0, dstNormData);
			
			coords = new ArrayList<Point>();
			//Load points found with Harris into an array:
			for(int i = 0; i < fnlNorm.rows(); i++) {
				for(int n = 0; n < fnlNorm.cols(); n++) {
					if((int) dstNormData[i*fnlNorm.cols() + n] > THRESHOLD) {
						Point tmp = new Point(n, i);
						coords.add(tmp);
					}
				}
			}
			System.out.println("NUM OF UNCLEAN COORDS: " + coords.size());
			ArrayList<Polygon> polys = cleanCoords();
			ArrayList<Rectangle> rtn = new ArrayList<Rectangle>();
			for(Polygon p : polys) {
				rtn.add(p.getBounds());
			}
			stripBadRects(rtn);
			rtn = sortRectangles(rtn);
			resizeRectangles(rtn);
			System.out.println("FINAL SIZE OF RECT ARRAY: " + rtn.size());
			return rtn;
			
			
	}

	/**
	 * This method is reponsible for removing any falsely detected "rectangles" above
	 * y = 900. This is mainly to handle special cases when Canny detects shapes that
	 * have the same area as the selection boxes.
	 * @param rects -- Raw list of Selection Box boundaries
	 */
	private static void stripBadRects(ArrayList<Rectangle> rects) {
		for(int i = 0; i < rects.size(); i++) {
			if(rects.get(i).y < 900) {
				rects.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Some of the box boundaries within the our list of rectangles
	 * may cover the outline of the box. To mitigate errors, this
	 * method resizes and repositions each rectangle in the list
	 * to fit inside their respective selection box boundary.
	 * Each rectangle's top left coordinate is offset by 5 pixels
	 * in both the x and y coordinates. The size of each rectangle
	 * is 36px by 36px.
	 * @param rects
	 */
	private static void resizeRectangles(ArrayList<Rectangle> rects) {

		for(int i = 0; i < rects.size(); i++) {
			int x = rects.get(i).x;
			int y = rects.get(i).y;
			System.out.println(rects.get(i).getWidth());
			rects.set(i, new Rectangle(x + 5, y + 5, 36,36));
		}
	}
	/**
	 * To order our raw list of box boundaries, we take any rectangle's top left coordinate point
	 * and determine if any other rectangles lie within a bounded the region of "y + 17, y - 17".
	 * If other rectangles are detected, place them into a list and sort them via "sortTmpList()"
	 * which will organize them by x-coordinate values. We then add the rectangles from this sorted temp
	 * list into our actual array.
	 * @param rects
	 * @return
	 */
	private static ArrayList<Rectangle> sortRectangles(ArrayList<Rectangle> rects) {
		
		int thresh = 17; // +/- value for X and Y coordinates in determining boxes in row
		
		ArrayList<Rectangle> orderedList = new ArrayList<Rectangle>();
		
		//SORT INITIAL REG/SWEET AND DRINKS:
		//Sort Initial Rectangles:
		Rectangle sugarReg = rects.get(0);
		orderedList.add(sugarReg);
		int iX = sugarReg.x;
		int iY = sugarReg.y;
		for(int i = 0; i < rects.size(); i++) {
			if(rects.get(i).x > iX - 100 && rects.get(i).x < iX + 100
					&& rects.get(i).y < iY + 400 && rects.get(i).y > iY) 
			{
				orderedList.add(rects.get(i));
				rects.remove(i);
			}
		}
		rects.remove(0);
		
		/////////////////////////////////////////////////////
		
		//SORT MEAL SELECTIONS:
		for(int i = 0; i < rects.size(); i++) {
			double currentY = rects.get(i).getY();
			double upper = currentY + thresh; //Upper thresholding for Y coords in row X
			double lower = currentY - thresh; //Lower thresholding ...
			ArrayList<Rectangle> tmp = new ArrayList<Rectangle>();
			for(int n = 0; n < rects.size(); n++) {
				if(rects.get(n).getY() >= lower && rects.get(n).getY() <= upper) {
					tmp.add(rects.get(n));
					rects.remove(n);
					n--; //Decrease 'n' due to removal
				}
			}
			i--; //Decrease 'i' due to removal
			sortTmpList(tmp); //Sort this current Row of boxes
			for(Rectangle r : tmp) {
				orderedList.add(r); //Add to main list
			}
		}
		
		return orderedList;
		
	}
	/**
	 * Sorts the rectangles from a temp list passed in by our sortRectangles()
	 * method. It utilizes a bubblesort to organize the rectangles in ascending
	 * order.
	 * 
	 * @param tmp -- temp list of rectangles to be sorted
	 */
	private static void sortTmpList(ArrayList<Rectangle> tmp) {
		
		//Bubblesort:
		for(int i = 0; i < tmp.size(); i++) {
			for(int n = 1; n < tmp.size(); n++) {
				if(tmp.get(n - 1).getX() > tmp.get(n).getX()) {
					Rectangle r = tmp.get(n);
					tmp.set(n, tmp.get(n - 1));
					tmp.set(n-1, r);
				}
			}
		}
		
	}
	
	/**
	 * Utilizing the distance formula, each point in our coordinate list searches
	 * within 70 px distance. Any points that are within this distance are added into 
	 * a temp Polygon object that is then loaded into a List to be returned.
	 * @return -- List of Polygons
	 */
	private static ArrayList<Polygon> cleanCoords() {

		ArrayList<Point> rtnPoints = coords;
		ArrayList<Polygon> polys = new ArrayList<Polygon>();

		for(int i = 0; i < rtnPoints.size(); i++) {
			Point hinge = rtnPoints.get(i);
			Polygon tmp = new Polygon();
			tmp.addPoint((int)hinge.x, (int)hinge.y);
			for(int n = i+1; n < rtnPoints.size(); n++) {
				//Apply distance formula and determine if point is within range:
				if(distanceFormula(hinge, rtnPoints.get(n)) < MAX_DIST) {
					tmp.addPoint((int)rtnPoints.get(n).x, (int)rtnPoints.get(n).y);
					rtnPoints.remove(n);
					n--; // decrease 'n' due to removal;
				}
			}
			polys.add(tmp);
		}
		
		return polys;
		
	}
	/**
	 * Distance Formula
	 * @param a -- (X_1, Y_1)
	 * @param b -- (X_2, Y_2)
	 * @return -- Distance value between the two points
	 */
	public static double distanceFormula(Point a, Point b) {
		
		double x = b.x - a.x;
		double y = b.y - a.y;
		
		double AB = Math.sqrt(Math.pow(x, 2) + Math.pow(y,2));
		
		return AB;
		
	}
	
}
