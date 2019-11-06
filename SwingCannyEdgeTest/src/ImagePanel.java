import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ImagePanel extends JPanel {

	private BufferedImage image;
	public double x, y;
	public List<List<Point>> array;
	public Mat grayImage;
	public List<MatOfPoint> contours;
	
	public ImagePanel(BufferedImage image) {
		this.image = image;
		x = 0;
		y = 0;
		array = new ArrayList<List<Point>>();
		grayImage = new Mat();
		contours = new ArrayList<>();
//		array.get(0);
	}
	
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 250, 0, this);
//		Imgproc.drawContours(this.grayImage, contours, -1, new Scalar(0,255,0));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.RED);
		if(array != null) {
			for(int i = 0; i < array.size(); i++) {
				for(int n = 0; n < array.get(i).size(); n++) {
					g2d.fillOval((int)array.get(i).get(n).x + 250, (int)array.get(i).get(n).y, 5, 5);
				}
			}
		}
		g2d.setColor(Color.GREEN);
		g2d.fillOval(89, 461, 5, 5);
		
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	
	
}
