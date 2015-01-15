package si.gto76.facetracker;

import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.videoio.VideoCapture;

public class Main extends JPanel {

	public static final String LIB_OPENCV_JAVA = "D:\\DESKTOP-DATA\\home\\downloads\\opencv\\opencv\\build\\java\\x64\\opencv_java300.dll";

	static Chart chartNoOfFaces;
	static SizeChart chartSizesOfFaces;

	static FaceLogger faceLogger = new FaceLogger();

	public static void main(String[] args) {
		processVideo();
	}

	public static void processVideo() {
		System.load(LIB_OPENCV_JAVA);
		String window_name = "Capture - Face detection";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		DemoFace my_processor = new DemoFace();
		Display display = new Display();
		frame.setContentPane(display);
		frame.setVisible(true);
		startChart();

		// -- 2. Read the video stream
		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(0);

		if (capture.isOpened()) {

			while (true) {
				capture.read(webcam_image);
				if (webcam_image.empty()) {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
				
				frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
				// -- 3. Apply the classifier to the captured image
				ImageWithData result = my_processor.detect(webcam_image);
				webcam_image = result.image;
				// -- 4. Display the image
				display.MatToBufferedImage(webcam_image);
				display.repaint();

				// -- 5. Analize the data and draw the charts
				MatOfRect data = result.faces;
				faceLogger.tick(data);

				int noOfFaces = faceLogger.getNoOfFaces();
				chartNoOfFaces.refresh(noOfFaces);
				
				Map<MyColor, Double> faceSizes = faceLogger.getFaceSizes();
				chartSizesOfFaces.refresh(faceSizes);
			}
		}
	}

	public static void startChart() {
		chartNoOfFaces = new Chart("Number of faces");
		chartNoOfFaces.pack();
		RefineryUtilities.centerFrameOnScreen(chartNoOfFaces);
		chartNoOfFaces.setVisible(true);

		chartSizesOfFaces = new SizeChart("Sizes of faces");
		chartSizesOfFaces.pack();
		RefineryUtilities.centerFrameOnScreen(chartSizesOfFaces);
		chartSizesOfFaces.setVisible(true);
	}

	public static void processImage() {
		System.load(LIB_OPENCV_JAVA);
		System.out.println("#### loaded library");
		/*
		 * Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0)); System.out.println("OpenCV Mat: " + m); Mat
		 * mr1 = m.row(1); mr1.setTo(new Scalar(1)); Mat mc5 = m.col(5); mc5.setTo(new Scalar(5));
		 * System.out.println("OpenCV Mat data:\n" + m.dump());
		 */
		DemoFace df = new DemoFace();
		df.demo();
	}

}
