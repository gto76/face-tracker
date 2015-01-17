package si.gto76.facetracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import si.gto76.facetracker.averagers.CountAverager;
import si.gto76.facetracker.averagers.MovementAverager;
import si.gto76.facetracker.averagers.SizeAverager;
import si.gto76.facetracker.charts.PositionChart;
import si.gto76.facetracker.charts.CounterChart;
import si.gto76.facetracker.charts.SizeChart;
import si.gto76.facetracker.charts.MovementChart;

public class Main extends JPanel {

	public static final String LIB_OPENCV_JAVA = "D:\\DESKTOP-DATA\\home\\downloads\\opencv\\opencv\\build\\java\\x64\\opencv_java300.dll";

	private static final int SIZE_WINDOW = 10;
	private static final int MOVEMENT_WINDOW = 3;
	private static final int NUMBER_WINDOW = 7;

	ChartsWindow chartsWindow;
	static CounterChart noOfFacesChart;
	static SizeChart sizeChart;
	static PositionChart positionChart;
	static MovementChart movementChart;

	static FaceLogger faceLogger = new FaceLogger();

	public static void main(String[] args) {
		processVideo();
	}

	public static void processVideo() {
		System.load(LIB_OPENCV_JAVA);
		Display display = new Display();
		JFrame frame = getVideoFrame(display);
		DemoFace my_processor = new DemoFace();
		
		// SET AVERAGERS
		SizeAverager faceSizesAverager = new SizeAverager(SIZE_WINDOW);
		MovementAverager faceMovementAverager = new MovementAverager(MOVEMENT_WINDOW);
		CountAverager faceNumberAverager = new CountAverager(NUMBER_WINDOW);

		// -- 2. Read the video stream
		Mat webcam_image = new Mat();
		VideoCapture capture = new VideoCapture(0);
		
		capture.read(webcam_image);
		startCharts(webcam_image.width(), webcam_image.height());

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
				noOfFacesChart.refresh(noOfFaces);
				
				Map<MyColor, Double> faceSizes = faceLogger.getFaceSizes();
				faceSizes = faceSizesAverager.tick(faceSizes);
				sizeChart.refresh(faceSizes);
				
				Map<MyColor, Point> facePositions = faceLogger.getFacePositions();
				positionChart.refresh(facePositions);

				Map<MyColor, Point> faceMovements = faceLogger.getFaceMovements();
				faceMovements = faceMovementAverager.tick(faceMovements);
				movementChart.refresh(faceMovements);
			}
		}
	}
	
	public static JFrame getVideoFrame(Display display) {
		String window_name = "Capture - Face detection";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setContentPane(display);
		frame.setVisible(true);
		return frame;
	}

	public static void startCharts(int width, int height) {
		List<JPanel> chartPanelsWithSizes = new ArrayList<JPanel>();
        
		noOfFacesChart = new CounterChart("Number of faces");
		sizeChart = new SizeChart("Sizes of faces");
		positionChart = new PositionChart("Movement of faces", width, height);
		movementChart = new MovementChart("Vectors of movement");
		
		JPanel positionAndVector = new JPanel();
		positionAndVector.add(positionChart);
		positionAndVector.add(movementChart);

		chartPanelsWithSizes.add((JPanel) noOfFacesChart);
		chartPanelsWithSizes.add((JPanel) sizeChart);
		chartPanelsWithSizes.add(positionAndVector);

		ChartsWindow chartsWindow = new ChartsWindow("Stats", chartPanelsWithSizes);
		chartsWindow.pack();
        RefineryUtilities.centerFrameOnScreen(chartsWindow);
        chartsWindow.setVisible(true);
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
