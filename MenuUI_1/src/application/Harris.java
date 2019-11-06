package application;


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

	public static ArrayList<Rectangle> getSelectionBoxes(BufferedImage in) throws IOException {
			
			Mat source = Utils.bImage2Mat(in);
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
			
			coords = new ArrayList<>();
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
			ArrayList<Rectangle> rtn = new ArrayList<>();
			for(Polygon p : polys) {
				rtn.add(p.getBounds());
			}
			stripBadRects(rtn);
			rtn = sortRectangles(rtn);
			resizeRectangles(rtn);
			
			System.out.println("FINAL SIZE OF RECT ARRAY: " + rtn.size());
			return rtn;
			
			
	}

	private static void stripBadRects(ArrayList<Rectangle> rects) {
		for(int i = 0; i < rects.size(); i++) {
			if(rects.get(i).y < 900) {
				rects.remove(i);
				i--;
			}
		}
	}
	
	private static void resizeRectangles(ArrayList<Rectangle> rects) {

		for(int i = 0; i < rects.size(); i++) {
			int x = rects.get(i).x;
			int y = rects.get(i).y;
			System.out.println(rects.get(i).getWidth());
			rects.set(i, new Rectangle(x + 5, y + 5, 36,36));
		}
	}
	
	private static ArrayList<Rectangle> sortRectangles(ArrayList<Rectangle> rects) {
		
		int thresh = 17;
		ArrayList<Rectangle> orderedList = new ArrayList<>();
		
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
		
		//SORT MEAL SELECTIONS:
		for(int i = 0; i < rects.size(); i++) {
			double currentY = rects.get(i).getY();
			double upper = currentY + thresh; //Upper thresholding for Y coords in row X
			double lower = currentY - thresh; //Lower thresholding ...
			ArrayList<Rectangle> tmp = new ArrayList<>();
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
	
	private static ArrayList<Polygon> cleanCoords() {

		ArrayList<Point> rtnPoints = coords;
		ArrayList<Polygon> polys = new ArrayList<>();

		for(int i = 0; i < rtnPoints.size(); i++) {
			Point hinge = rtnPoints.get(i);
			Polygon tmp = new Polygon();
			tmp.addPoint((int)hinge.x, (int)hinge.y);
			for(int n = i+1; n < rtnPoints.size(); n++) {
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
	
	public static double distanceFormula(Point a, Point b) {
		
		double x = b.x - a.x;
		double y = b.y - a.y;
		
		double AB = Math.sqrt(Math.pow(x, 2) + Math.pow(y,2));
		
		return AB;
		
	}
	
}
