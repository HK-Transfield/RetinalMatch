import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.*;;

/**
 * Represents the retinal image of a person taken using
 * a retinal fundus camera that scans the eye.
 * 
 * @author Harmon Transfield and Edward Wang
 */
public class RetinalImage {
    private Mat src, dst, original;

    private final int DELAY_CAPTION = 1500;
    private final int DELAY_BLUR = 100;
    private final int MAX_KERNEL_LENGTH = 31;

    /**
     * Constructor.
     * Instatiates a new RetinalImage object.
     * 
     * @param filename The filepath of the image
     */
    public RetinalImage(String filename) {

        src = original = Imgcodecs.imread(filename);

        // check that the image does exist
        if (src.empty()) {
            System.out.println("Error: Cannot read image");
            System.out.println("Usage java RetinalMatch <path to image 1>.jpg <path to image 2>.jpg");
            System.exit(0);
        }
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * Writes the image in its current form to a new file
     */
    public void writeSrc() {
        Imgcodecs.imwrite("RIDB_out/img_final", src);
    }

    /**
     * Provides access to the src class property.
     * 
     * @return The Mat source image of the RetinalImage of the class.
     */
    public Mat getSrc() {
        return src;
    }

    /**
     * Applies a non-linear filter to the image, which will remove noise from an
     * image or signal
     * 
     * https://docs.opencv.org/3.4/dc/dd3/tutorial_gausian_median_blur_bilateral_filter.html
     * https://en.wikipedia.org/wiki/Median_filter
     */
    public void medianFilter() {
        dst = new Mat();
        for (int i = 1; i < MAX_KERNEL_LENGTH; i += 2) {
            Imgproc.medianBlur(src, src, i);
        }

        Imgcodecs.imwrite("RIDB_out/img_median.jpg", src);
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
        dst = new Mat(src.rows(), src.cols(), src.type());
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

        Imgproc.adaptiveThreshold(src, src, 125,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 12);

        Imgcodecs.imwrite("RIDB_out/img_threshold.jpg", src);
    }

    /**
     * Increase the contrast of the image
     */
    public void enhanceContrast() {
        dst = new Mat();
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(src, src);
        Imgcodecs.imwrite("RIDB_out/img_contrast.jpg", src);
    }

    /**
     * Dilitation adds pixels to the boundaries of the image
     * 
     * https://opencv-java-tutorials.readthedocs.io/en/latest/07-image-segmentation.html
     */
    public void imageDilatation() {

        Mat grayImage = new Mat();
        Mat detectedEdges = new Mat();
        dst = new Mat();
        double threshold = 20;

        Imgproc.cvtColor(src, grayImage, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, threshold, threshold * 3, 3,
                false);

        // fill dst image with 0s (meaning the image is completely black)
        Core.add(dst, Scalar.all(0), dst);
        src.copyTo(dst, detectedEdges);

        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);
        Imgcodecs.imwrite("RIDB_out/img_dilatation.jpg", dst);
    }

    /**
     * Changes the image to a HSV format
     */
    public void convertToHSV() {
        dst = new Mat();

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
        Imgcodecs.imwrite("RIDB_out/img_hsv.jpg", src);
    }

    /**
     * Compares two images to determine how similar they are
     * 
     * https://www.programcreek.com/java-api-examples/?api=org.opencv.features2d.DescriptorExtractor
     * 
     * @param ri The image being compared and matched.
     */
    public void compareImage(RetinalImage ri) {
        Mat other = ri.getSrc();
        SIFT detector = SIFT.create();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

        // identify keypoint detections
        MatOfKeyPoint mkp1 = new MatOfKeyPoint();
        Mat desc = new Mat();
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        detector.detect(src, mkp1);
        detector.compute(src, mkp1, desc);

        MatOfKeyPoint mkp2 = new MatOfKeyPoint();
        Mat desc2 = new Mat();
        Imgproc.cvtColor(other, other, Imgproc.COLOR_BGR2GRAY);
        detector.detect(other, mkp2);
        detector.compute(other, mkp2, desc2);

        // match features
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(desc, desc2, matches);

        List<DMatch> l = matches.toList();
        List<DMatch> ldm = new ArrayList<DMatch>();

        for (int i = 0; i < l.size(); i++) {
            DMatch dmatch = l.get(i);

            if (Math.abs(dmatch.queryIdx - dmatch.trainIdx) < 10f) {
                ldm.add(dmatch);
            }
        }

        matches.fromList(ldm);
        Mat outImg = new Mat();
        Features2d.drawMatches(src, mkp1, other, mkp2, matches, outImg);
        Imgcodecs.imwrite("img_compare.jpg", outImg);
    }
}