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
     * Represents the retinal image of a person taken using
     * a retinal fundus camera that scans the eye.
     */

    private static Mat src;
    private static Mat dst;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String filename1 = args[1];
        String filename2 = args[2];

        RetinalImage ri1 = new RetinalImage(filename1);
        RetinalImage ri2 = new RetinalImage(filename2);
    }

}