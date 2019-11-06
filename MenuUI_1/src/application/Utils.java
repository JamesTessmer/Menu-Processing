package application;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public final class Utils {
	
	public static BufferedImage readInImage(String filePath) throws IOException {
		
		BufferedImage img = null;
		File f = null;
		
		//Read in file
		try {
			
			f = new File(filePath);
			img = ImageIO.read(f);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		//Before returning, lets scale the image
//		BufferedImage before = img;
//		int w = img.getWidth();
//		int h = img.getHeight();
//		BufferedImage after = new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
//		AffineTransform at = new AffineTransform();
//		
//		at.scale(0.15, 0.15);
//		AffineTransformOp scaleOp =
//				new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//		after = scaleOp.filter(before, after);
//		
		return img;
	}
	
	public static Mat bImage2Mat(BufferedImage in) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(in, "jpg", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
	}
	
	public static BufferedImage matToBufferedImage(Mat original)
	{
		// init
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);
		
		if (original.channels() > 1)
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		else
		{
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
		
		return image;
	}
	
}
