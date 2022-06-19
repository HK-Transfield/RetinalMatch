import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.*;
import org.opencv.core.Rect;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;

/**
 * Represents the retinal image of a person taken using
 * a retinal fundus camera that scans the eye.
 * 
 * @author Harmon Transfield and Edward Wang
 */
public class RetinalImage {
    private Mat _src;

    private static final int DELAY_CAPTION = 1500;
    private static final int DELAY_BLUR = 100;
    private static final int MAX_KERNEL_LENGTH = 31;

    /**
     * Constructor.
     * Instatiates a new RetinalImage object.
     * 
     * @param filename The filepath of the image
     */
    public RetinalImage(String filename) {

        _src = Imgcodecs.imread(filename);

        // check that the image does exist
        if (_src.empty()) {
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
        Imgcodecs.imwrite("RIDB_out/img_final", _src);
    }

    /**
     * Provides access to the src class property.
     * 
     * @return The Mat source image of the RetinalImage of the class.
     */
    public Mat getSrc() {
        return _src;
    }

    /**
     * Applies a non-linear filter to the image, which will remove noise from an
     * image or signal
     * 
     * https://docs.opencv.org/3.4/dc/dd3/tutorial_gausian_median_blur_bilateral_filter.html
     * https://en.wikipedia.org/wiki/Median_filter
     */
    public static Mat medianFilter(Mat src) {

        for (int i = 1; i < 4; i += 2) {
            Imgproc.medianBlur(src, src, i);
        }

        Imgcodecs.imwrite("RIDB_out/img_median.jpg", src);
        return src.clone();
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
    public static Mat adaptiveThreshold(Mat src) {
        // Imgproc.cvtColor(ri, ri, Imgproc.COLOR_BGR2GRAY);

        Imgproc.adaptiveThreshold(src, src, 125,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY, 11, 12);

        Imgcodecs.imwrite("RIDB_out/img_threshold.jpg", src);
        return src.clone();
    }

    /**
     * Increase the contrast of the image
     */
    public static Mat enhanceContrast(Mat src) {
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        src.convertTo(src, -1, 2, 0);
        return src.clone();
    }

    /**
     * Dilation adds pixels to the boundaries of the image
     * 
     * https://opencv-java-tutorials.readthedocs.io/en/latest/07-image-segmentation.html
     */
    public static Mat edgeDetection(Mat src) {

        Mat detectedEdges = new Mat();
        Mat dst = new Mat();

        // fill dst image with 0s (meaning the image is completely black)
        Core.add(dst, Scalar.all(0), dst);
        src.copyTo(dst, detectedEdges);

        return dst;
    }

    /**
     * 
     * @param src
     * @return
     */
    public static Mat erosionAndDilation(Mat src) {
        Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 1));
        Mat kernel2 = Mat.ones(5, 5, CvType.CV_8U);

        Imgproc.erode(src, new Mat(), kernel2);
        Imgproc.dilate(src, src, kernel1);
        Imgcodecs.imwrite("RIDB_out/img_errosion.jpg", src);
        return src.clone();
    }

    /**
     * Changes the image to a HSV format
     */
    public static Mat convertToHSV(Mat src) {

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
        return src.clone();
    }

    /**
     * Compares two images to determine how similar they are
     * 
     * https://www.programcreek.com/java-api-examples/?api=org.opencv.features2d.DescriptorExtractor
     * 
     * @param ri The image being compared and matched.
     */
    public void compareImage(RetinalImage ri) {
        Mat src2 = ri.getSrc();
        SIFT detector = SIFT.create();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

        // identify keypoint detections
        MatOfKeyPoint mkp1 = new MatOfKeyPoint();
        Mat desc = new Mat();
        _src = pipeline(_src);
        src2 = pipeline(src2);

        detector.detect(_src, mkp1);
        detector.compute(_src, mkp1, desc);

        MatOfKeyPoint mkp2 = new MatOfKeyPoint();
        Mat desc2 = new Mat();
        detector.detect(src2, mkp2);
        detector.compute(src2, mkp2, desc2);

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
        Features2d.drawMatches(_src, mkp1, src2, mkp2, matches, outImg);
        Imgcodecs.imwrite("img_compare.jpg", outImg);

        Mat matching = new Mat();
        Imgproc.matchTemplate(_src, src2, matching, Imgproc.TM_SQDIFF);

        MinMaxLocResult mmr = Core.minMaxLoc(matching);
        System.out.println("Max: " + mmr.maxVal);
        System.out.println("Min: " + mmr.minVal);
    }

    /**
     * Computer vision pipline made up of CV operations.
     * 
     * @param image The RetinalImage being processed.
     */
    private static Mat pipeline(Mat src) {

        // convertToHSV(src);
        medianFilter(src);
        enhanceContrast(src);
        adaptiveThreshold(src);
        erosionAndDilation(src);
        // edgeDetection(ri);

        return src;

    }
}
