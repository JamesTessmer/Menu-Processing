package imUtils;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import Model.Client;

public final class ImageCVT {
	
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
		return img;
	}
	
	private static final String OUTPUT_DIR = "temp"; //whatever we want the final output directory to be for images from menu scans should be here
	
	 /**
     * Converts a pdf file to a jpg file using the given filepath and the PDFRenderer library.
     *
     * @param filePath is the path to the desired
     * @return returns the filepath of the saved image, or the filepath to the original pdf if it couldn't be opened
     */
    public static String PDFtoImage(String filePath) {
        try(final PDDocument document = PDDocument.load(new File(filePath))){ //opening the PDF to be converted
            PDFRenderer rend = new PDFRenderer(document);
           
            /*
             * creating a buffered image of the PDF and then saving as a .jpg file
             */
           
            BufferedImage menuImage = rend.renderImageWithDPI(0,300,ImageType.RGB); //0 because it should be the only page
            ImageIOUtil.writeImage(menuImage, OUTPUT_DIR + "MenuImage.jpg", 300); //saving the image to the disk
            document.close();
            return OUTPUT_DIR + "MenuImage.jpg"; //returns the filepath to the saved image
           
           
        } catch (IOException e) { //if there's an issue opening the pdf print the error
            System.err.println("Exception while trying to create document - " + e);
            System.err.println("Could not open file, returning filePath");
            return filePath;
        }
    }
	
	public static Mat bImage2Mat(BufferedImage in) throws IOException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	    ImageIO.write(in, "jpg", byteArrayOutputStream);
	    byteArrayOutputStream.flush();
	    return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
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
