
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

/* These imports only work in later versions (i.e 4.6.0) */
/* Uncomment these to run feature detection */
// import org.opencv.core.MatOfDMatch;
// import org.opencv.core.DMatch;
// import org.opencv.core.MatOfKeyPoint;
// import org.opencv.features2d.*;
// import java.util.ArrayList;
// import java.util.List;

/**
 * A program using computer vision techniques to see if a person
 * can be uniquely identified from their retina images. The aim
 * of the project is to come up with a function or program that
 * will match two retinal images and decide if they are the same
 * individual or not.
 * 
 * @author Harmon Transfield (1317381) and Edward Wang (1144995)
 */
public class RetinalMatch {

    /**
     * Represents the retinal image of a person taken using
     * a retinal fundus camera that scans the eye.
     */
    class RetinalImage {
        private Mat _src;
        private final Double SIMILARITY_THRESHOLD = 0.5d; // values closer to 0 are better matches

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

        /**
         * Writes the image in its current form to a new file.
         */
        public void writeSrc() {
            Imgcodecs.imwrite("RIDB_out/img_final", _src);
        }

        /**
         * Provides access to the src class property.
         * 
         * @return The source image.
         */
        public Mat getSrc() {
            return _src;
        }

        /**
         * Applies a non-linear filter to the image, which will remove noise from an
         * image or signal
         * 
         * Useful Links:
         * https://docs.opencv.org/3.4/dc/dd3/tutorial_gausian_median_blur_bilateral_filter.html
         * https://en.wikipedia.org/wiki/Median_filter
         * 
         * @param src The source image
         * @return An image with a median blur applied
         */
        public Mat medianFilter(Mat src) {

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
         * Useful links:
         * https://www.tutorialspoint.com/explain-opencv-adaptive-threshold-using-java-example
         * 
         * @param src The source image
         * @return A binary image
         */
        public Mat adaptiveThreshold(Mat src) {
            // Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);

            int maxValue = 125;
            int blockSize = 23;
            int c = 12;

            Imgproc.adaptiveThreshold(src, src, maxValue,
                    Imgproc.ADAPTIVE_THRESH_MEAN_C,
                    Imgproc.THRESH_BINARY, blockSize, c);
            Imgcodecs.imwrite("RIDB_out/img_threshold.jpg", src);
            return src.clone();
        }

        /**
         * Increases the contrast of the image.
         * 
         * @param src The source image.
         * @return An iamge with its contrast increased.
         */
        public Mat enhanceContrast(Mat src) {
            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
            src.convertTo(src, -1, 2, 0);
            Imgcodecs.imwrite("RIDB_out/img_contrast.jpg", src);
            return src.clone();
        }

        /**
         * Performs edge detection
         * 
         * https://opencv-java-tutorials.readthedocs.io/en/latest/07-image-segmentation.html
         */
        public Mat edgeDetection(Mat src) {

            Mat detectedEdges = new Mat();
            Mat dst = new Mat();

            // fill dst image with 0s (meaning the image is completely black)
            Core.add(src, Scalar.all(0), src);
            src.copyTo(src, detectedEdges);
            Imgcodecs.imwrite("RIDB_out/img_detection.jpg", src);
            return dst;
        }

        /**
         * Performs erosion removes pixels from the image's
         * boundary.
         * 
         * @param src The source image.
         * @return An eroded image.
         */
        public Mat erosionAndDilation(Mat src) {
            Mat kernel = Mat.ones(4, 4, CvType.CV_32F);

            // perform dilation
            Imgproc.morphologyEx(src, src, Imgproc.MORPH_CLOSE, kernel);
            Imgcodecs.imwrite("RIDB_out/img_dilation.jpg", src);

            // perform erosion
            Imgproc.erode(src, src, new Mat(), new Point(-1, -1), 3);
            Imgcodecs.imwrite("RIDB_out/img_errosion.jpg", src);
            return src.clone();
        }

        /**
         * Converts the colour space the image to a HSV format.
         * 
         * @param src The source image.
         * @return An image with the HSV colour space.
         */
        public Mat convertToHSV(Mat src) {

            Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);
            return src.clone();
        }

        /**
         * Compares two images to determine how similar they are,
         * based on a similarity threshold.
         * 
         * NOTE: The feature detection does not work in 4.2.0
         * 
         * Useful links:
         * https://www.programcreek.com/java-api-examples/?api=org.opencv.features2d.DescriptorExtractor
         * https://docs.opencv.org/2.4/modules/imgproc/doc/object_detection.html?highlight=matchtemplate#matchtemplate
         * 
         * @param src The image being compared and matched.
         */
        public void compareImage(RetinalImage ri) {
            Mat src2 = ri.getSrc();
            Mat matching = new Mat();
            _src = pipeline(_src);
            src2 = pipeline(src2);
            Imgproc.matchTemplate(_src, src2, matching, Imgproc.TM_CCOEFF_NORMED);

            MinMaxLocResult mmr = Core.minMaxLoc(matching);
            Double maxVal = new Double(mmr.maxVal);
            System.out.println(maxVal);
            System.out.print(mmr.maxVal < 0.5 ? 0 : 1);

            /* !!!: Not available in 4.2.0 */
            // SIFT detector = SIFT.create();
            // DescriptorMatcher matcher =
            // DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);

            // // identify keypoint detections
            // MatOfKeyPoint mkp1 = new MatOfKeyPoint();
            // Mat desc = new Mat();

            // detector.detect(_src, mkp1);
            // detector.compute(_src, mkp1, desc);

            // MatOfKeyPoint mkp2 = new MatOfKeyPoint();
            // Mat desc2 = new Mat();
            // detector.detect(src2, mkp2);
            // detector.compute(src2, mkp2, desc2);

            // // match features
            // MatOfDMatch matches = new MatOfDMatch();
            // matcher.match(desc, desc2, matches);

            // List<DMatch> l = matches.toList();
            // List<DMatch> ldm = new ArrayList<DMatch>();

            // for (int i = 0; i < l.size(); i++) {
            // DMatch dmatch = l.get(i);

            // if (Math.abs(dmatch.queryIdx - dmatch.trainIdx) < 10f) {
            // ldm.add(dmatch);
            // }
            // }

            // matches.fromList(ldm);
            // Mat outImg = new Mat();
            // Features2d.drawMatches(_src, mkp1, src2, mkp2, matches, outImg);
            // Imgcodecs.imwrite("img_compare.jpg", outImg);
        }

        /**
         * Computer vision pipline made up of CV operations.
         * 
         * @param image The RetinalImage being processed.
         */
        private Mat pipeline(Mat src) {

            // this.convertToHSV(src);
            this.enhanceContrast(src);
            this.medianFilter(src);
            this.adaptiveThreshold(src);
            this.erosionAndDilation(src);
            // this.edgeDetection(ri);

            return src;
        }
    }

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
            System.exit(0);
        }

        // retrieve images from cli
        String filename1 = args[0];
        String filename2 = args[1];

        RetinalMatch rm = new RetinalMatch();
        RetinalImage image1 = rm.new RetinalImage(filename1);
        RetinalImage image2 = rm.new RetinalImage(filename2);

        // begin comparison
        image1.compareImage(image2);
    }
}