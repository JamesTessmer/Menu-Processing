import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;

public class View extends JFrame {

	private JFrame mainFrame;
	private JPanel mainPanel;
	private ImagePanel imagePanel;
	private JScrollPane scrollPane;
	private JButton start;
	//25 Seems to allow for the best outline, Increase to ignore less emphasized edges, Decrease to capture most edges
	private final int THRESHOLD = 100;
	private final int MIN_AREA = 140;
	private final int MAX_AREA = 100000;
	private BufferedImage image;
	private String filePath;
	
	public View() throws IOException {
		
		this.filePath = "MenuSample.jpg";
		
		this.image = Utils.readInImage(this.filePath);
		this.mainFrame = new JFrame("Canny Edge Detection Test | OpenCV_3.4.5");
		this.mainFrame.setPreferredSize(new Dimension(1600,900));
		this.mainPanel = new JPanel();
		this.imagePanel = new ImagePanel(image);
		
		this.mainPanel.setLayout(new BorderLayout());
		this.mainPanel.add(this.imagePanel, BorderLayout.CENTER);
		
		this.start = new JButton("Start");
		this.start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				onStartPressed();
			}
			
		});
		
		this.mainPanel.add(start, BorderLayout.SOUTH);
		this.mainPanel.setPreferredSize(new Dimension(1000,2000));
		this.scrollPane = new JScrollPane(this.mainPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.scrollPane.setWheelScrollingEnabled(true);
		this.mainFrame.setContentPane(this.scrollPane);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.pack();
		this.mainFrame.setVisible(true);
		
		
		
	}

	protected void onStartPressed() {
		// TODO Auto-generated method stub
		System.out.println("Bingo");
		Mat imgMat = this.doCanny(this.filePath);
		this.image = Utils.matToBufferedImage(imgMat);
//		this.imagePanel.setImage(this.image);
//		this.imagePanel.repaint();
		
		getCoords(imgMat);
	}
	
	/**
	 * Apply Canny
	 * 
	 * @param frame
	 *            the current frame
	 * @return an image Mat elaborated with Canny
	 */
	private Mat doCanny(String filePath)
	{
		// init
		Mat grayImage = new Mat();
		Mat detectedEdges = new Mat();

		Mat source = Imgcodecs.imread(filePath);

		// convert to grayscale
		Imgproc.cvtColor(source, grayImage, Imgproc.COLOR_BGR2GRAY);
		// reduce noise with blur and a 3x3 Sobel kernel
		Imgproc.blur(grayImage, detectedEdges, new Size(2, 2));
		
		// canny detector, with ratio of upper:lower 1:3
		Imgproc.Canny(detectedEdges, detectedEdges, THRESHOLD, THRESHOLD * 3, 3, false);
		
		// using Canny's output as a mask, display the result
		Mat dest = new Mat();
		Core.add(dest,  Scalar.all(0), dest);
		source.copyTo(dest, detectedEdges);
		
		return dest;
	}
	
	private void getCoords(Mat img) {		
		
		List<MatOfPoint> contourList = new ArrayList<>();
		Mat grayImage = new Mat();
		
		//Convert to grayscale
		Imgproc.cvtColor(img, grayImage, Imgproc.COLOR_BGR2GRAY);

		Imgproc.findContours(grayImage, contourList, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		
		List<MatOfPoint> betterCNT = new ArrayList<>();
		
		for(int i = 0; i < contourList.size(); i++) {
			
			if(Imgproc.contourArea(contourList.get(i)) > MIN_AREA && Imgproc.contourArea(contourList.get(i)) < MAX_AREA) {
				betterCNT.add(contourList.get(i));
			}
			
		}

		Mat contours = new Mat();
		contours.create(img.rows(), img.cols(), CvType.CV_8UC3);

		
		//Draw on Contours to ensure correct points are being recieved
		for(int i = 0; i < betterCNT.size(); i++) {
			System.out.println(i + " of " + betterCNT.size());
			Imgproc.drawContours(contours, betterCNT, i, new Scalar(0,0,255), 1);
		}
		
		BufferedImage nImg = Utils.matToBufferedImage(contours);
		this.image = nImg;
		this.imagePanel.setImage(this.image);
		this.imagePanel.repaint();
//		
//
//		
////		//TODO: Get Contour points and insert them into a list
//		this.imagePanel.grayImage = grayImage;
//		this.imagePanel.contours = contourList;
//
//		
//		
//		for(int i = 0; i < contourList.size(); i++) {
//
//				List<Point> pointTmp = new ArrayList<>();
//				
//
//				Converters.Mat_to_vector_Point(contourList.get(i), pointTmp);
//				
//				this.imagePanel.array.add(pointTmp);
//			
//		}

	}
	

	
}
