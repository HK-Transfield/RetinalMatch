import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * Represents the retinal image of a person taken using
 * a retinal fundus camera that scans the eye.
 * 
 * @author Harmon Transfield and Edward Wang
 */
public class RetinalImage {
    private Mat src;
    private Mat dst;
    private Mat grayImage = new Mat();

    int DELAY_CAPTION = 1500;
    int DELAY_BLUR = 100;
    int MAX_KERNEL_LENGTH = 31;

    /**
     * Constructor.
     * Instatiates a new RetinalImage object.
     * 
     * @param filename The image file
     */
    public RetinalImage(String filename) {

        src = Imgcodecs.imread(filename);

        // check that the image does exist
        if (src.empty()) {
            System.out.println("Error: Cannot open image");
            System.out.println("Usage java RetinalMatch <path to image 1>.jpg <path to image 2>.jpg");
            System.exit(-1);
        }
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 
     * https://docs.opencv.org/3.4/dc/dd3/tutorial_gausian_median_blur_bilateral_filter.html
     */
    public void medianFilter() {
        for (int i = 1; i < MAX_KERNEL_LENGTH; i += 2) {
            Imgproc.medianBlur(src, dst, i);
        }
    }

    /**
     * Applies thresholding, a techinque for the segmentation of an
     * image. It can be used to create binary images. Pixels greater
     * than a given threshold value are replaced with a standard value.
     * 
     * Adaptive thresholding is the method where the threshold value is
     * calculated for smaller regions.
     * 
     * https://www.tutorialspoint.com/explain-opencv-adaptive-threshold-using-java-example
     */
    public void adaptiveThreshold() {
        Imgproc.adaptiveThreshold(src, dst, 125,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 12);
    }

    /**
     * Increase the contrast of the image
     */
    public void enhanceContrast() {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite("RIDB/IM000001_1.jpg", src);
        Imgproc.equalizeHist(src, dst);
        Imgcodecs.imwrite("enhanced.jpg", dst);
    }

    /**
     * 
     * https://opencv-java-tutorials.readthedocs.io/en/latest/07-image-segmentation.html
     */
    public void imageSegmentation() {
        Mat detectedEdges = new Mat();
        double threshold1 = 20;

        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, threshold1, threshold1 * 3, 3,
                false);

        // fill dst image with 0s (meaning the image is completely black)
        Core.add(dst, Scalar.all(0), dst);
        src.copyTo(dst, detectedEdges);

        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite("dog_gray.jpg", dst);
    }
}
