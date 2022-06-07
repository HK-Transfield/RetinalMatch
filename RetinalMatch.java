import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * A program using computer vision techniques to see if a person
 * can be uniquely identified from their retina images. The aim
 * of the project is to come up with a function or program that
 * will match two retinal images and decide if they are the same
 * individual or not.
 * 
 * @author Harmon Transfield and Edward Wang
 */
public class RetinalMatch {

    /**
     * Main entry point to the program.
     * 
     * @param args Array of image filenames
     */
    public static void main(String[] args) {

        // load the OpenCV core library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        if (args.length < 2) { // basic cli arg checking
            System.out.println("Error: Not enough arguments");
            System.out.println("Usage java RetinalMatch <path to image 1>.jpg <path to image 2>.jpg");
            System.exit(-1);
        }

        // retrieve images from cli
        String filename1 = args[0];
        String filename2 = args[1];

        RetinalImage image1 = new RetinalImage(filename1);
        RetinalImage image2 = new RetinalImage(filename2);

        RetinalMatch.pipeline(image1);
    }

    /**
     * Computer vision pipline made up of CV operations
     * 
     * @param image The RetinalImage being processed.
     */
    private static void pipeline(RetinalImage image) {
        // image.convertToHSV();
        // image.medianFilter();
        // image.adaptiveThreshold();
        image.imageDilatation();
    }
}