package imUtils;



import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public final class Canny {
	
	private final static int THRESHOLD = 100;
	private final static int MIN_AREA = 2000;
	private final static int MAX_AREA = 3000;
	
	/**
	 * This is the main method to use from this class.
	 * It will return a buffered image ready for Harris Corner Detection.
	 * This method first converts our image into grayscale, then utilizes
	 * OpenCV's Imgproc.findContours() method to add the shape coordinates 
	 * into list. This list is then passed into Imgproc.drawContours() which
	 * will draw a new image of the contours that have a valid area.
	 * @param filePath - path to template image
	 * @return A new image with a black background, and the shapes necessary to find the template image's selection boxes
	 */
	public static BufferedImage getCImage(String filePath) {
		List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
		Mat grayImage = new Mat();
		Mat img = doCanny(filePath);
		
		//Convert to grayscale
		Imgproc.cvtColor(img, grayImage, Imgproc.COLOR_BGR2GRAY);
		//Find the relevant candidates from the Matrix
		Imgproc.findContours(grayImage, contourList, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		
		List<MatOfPoint> betterCNT = new ArrayList<MatOfPoint>();
		
		for(int i = 0; i < contourList.size(); i++) {
			if(Imgproc.contourArea(contourList.get(i)) > MIN_AREA && Imgproc.contourArea(contourList.get(i)) < MAX_AREA) {
				betterCNT.add(contourList.get(i));
			}
		}

		Mat contours = new Mat();
		contours.create(img.rows(), img.cols(), CvType.CV_8UC3);
		
		for(int i = 0; i < betterCNT.size(); i++) {
			System.out.println(i + " of " + betterCNT.size());
			Imgproc.drawContours(contours, betterCNT, i, new Scalar(0,255,0), -1);
		}
		
		BufferedImage rtnImg = ImageCVT.matToBufferedImage(contours);
		return rtnImg;
	}
	/**
	 * Helper method to prep in finding contours of the image. We perform OpenCV's Canny Edge Detection
	 * method after applying grayscale and a bilateral filter. This matrix will then be processed in getCImage()
	 * 
	 * @param filePath - The path to the image template file
	 * @return A Matrix representing the image after Canny Edge Detection is applied
	 */
	private static Mat doCanny(String filePath) {
		// init
		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();
		Mat source = Imgcodecs.imread(filePath);

		// convert to grayscale
		Imgproc.cvtColor(source, grayImage, Imgproc.COLOR_BGR2GRAY);
		// reduce noise with blur/filter
		Imgproc.bilateralFilter(grayImage, detectedEdges, 13, 75, 75);
		
		// canny detector, with ratio of upper:lower 1:3
		Imgproc.Canny(detectedEdges, detectedEdges, THRESHOLD, THRESHOLD * 3, 3, false);
		
		
		// using Canny's output as a mask, display the result
		Mat dest = new Mat();
		Core.add(dest,  Scalar.all(0), dest);
		source.copyTo(dest, detectedEdges);
		
		return dest;
	}
	
}
