package imUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
 
/**
 * This class incorporates static methods that will be used to perform OCR on a Meals on Wheels of Tarrant County menu and then analyze that text to pull important information. The class
 * utilizes the Tesseract API and ByteDeco to read a scanned menu image and create a string. This string can then be analyzed by functions in this class to separate out information such as the
 * dates of each menu item and the client ID. It is important to note that the generateTessInstance() must be used before the OCR function can be called as it requires the Tesseract instance to
 * read the image at all. The text analysis functions do not use the Tesseract API directly, however they rely on being given the proper string, which comes from OCR, to function properly so, it is
 * very necessary to instantiate the Tesseract API before using any other functions.
 *
 * @author James
 *
 */
public class OCR {
   
    /**
     * This function will create and return an instance of the Tesseract api. All OCR functions will use this instance as a parameter.
     *
     * @param trainedDataPath this should be the path to the trained data, without this data the api cannot function
     * @return an instance of the Tess API
     */
    public static TessBaseAPI generateTessInstance(String trainedDataPath) {
        //
        TessBaseAPI instance = new TessBaseAPI();
       
        //load the trained data for English
        if( instance.Init(trainedDataPath, "eng") != 0) {
        	System.out.println("GIVEN PATH : " + trainedDataPath);
            System.out.println("unable to load trained data");
            String errorPrint = "UNABLE TO LOAD TRAINED DATA";
//            Files.write("C:\\Users\\Desktop\\", errorPrint.getBytes(), StandardOpenOption.CREATE_NEW);
        }
       
        return instance;
    }
   
    /**
     * This function takes the given filepath (to an image) and opens it as a PIX image and sets it as the image of the given Tess API.
     * The function will then OCR the image and return the resulting string.
     *
     * @param filePath the filepath of the desired image
     * @param instance an instance of the TessBaseAPI
     * @return The OCRd, unprocessed text pulled from the image
     */
    public static String readImage(String filePath, TessBaseAPI instance) {
        //opening the file as an image and setting it into the instance
        PIX image = lept.pixRead(filePath);
        instance.SetImage(image);
        //getting the text as UTF8, then converting to string and printing
        BytePointer bytePointer = instance.GetUTF8Text();
        String text = bytePointer.getString();
       
        return text;
    }
   
   
    /**
     * This function will take in the String produced by OCR and process it, pulling the menu dates for each item.
     *
     * @param text A string (presumably the text pulled from a menu using OCR) that will be analyzed for menu dates.
     * @return A list of strings containing dates. Each index is a string with all the dates for the given box. The order goes from top left of the menu to bottom right
     */
    public static String[] pullDates(String text) {
        int stringIndex = 0;
        String[] dateList = new String[30]; //one slot for each box on the menu
        int dateListCount = 0; //used to track the index of the date list array
       
        while(dateListCount < 30) { //Using a counter going to 30 because there will always be 30 boxes on the menu. It also serves as the index for the current date in the array
            stringIndex = text.indexOf("Menu Dates:", stringIndex);
            stringIndex += 11; //adding 11 because that's the length of "Menu Dates:"
            String dates = text.substring(stringIndex,stringIndex+25); //adding 25 to make sure all the dates are included, some overshot is ok
            /*
             * replacing undesirable characters
             */
            dates = dates.replace(" ", ""); //stripping white space
            dates = dates.replaceAll("[a-zA-Z]", ""); //replacing all letters
            dates = dates.trim();
            //System.out.println(dates);
            dateList[dateListCount] = dates; //adding the dates to the array
            dateListCount ++;
        }
       
        return dateList;
    }
   
    /**
     * This function will analyze the given text, hopefully from a menu, and pull the client ID out. It relies on the ID number being formatted properly and having four(4) leading zeroes(0)
     *
     * @param text A string (presumably the text pulled from a menu using OCR) that will be analyzed for a client ID number.
     * @return the client ID number as a String
     */
    public static String pullIDNum(String text) {
        String id;
        int idIndex = 0;
        idIndex = text.indexOf("0000") + 4; //adding 4 to account for the 4 leading 0s
        id = text.substring(idIndex, idIndex + 8); //pulling the id number, and some extra to account for variable length
        id = id.replaceAll("[a-zA-Z]", ""); //replacing any letters that may have been grabbed
        return id;
    }
 
}