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
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
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
	private static final String CASCADE_PATH = "D:\\DESKTOP-DATA\\home\\downloads\\opencv\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml";
	private CascadeClassifier face_cascade;

	private static final int SIZE_WINDOW = 10;
	private static final int MOVEMENT_WINDOW = 3;
	private static final int NUMBER_WINDOW = 10;

	// Class that stores face locations...
	FaceLogger faceLogger = new FaceLogger();

	// Classes that average the data before its send to the charts
	SizeAverager faceSizesAverager = new SizeAverager(SIZE_WINDOW);
	MovementAverager faceMovementAverager = new MovementAverager(MOVEMENT_WINDOW);
	CountAverager faceNumberAverager = new CountAverager(NUMBER_WINDOW);

	// Charts
	ChartsWindow chartsWindow;
	CounterChart noOfFacesChart;
	SizeChart sizeChart;
	PositionChart positionChart;
	MovementChart movementChart;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Error: No parameters provided (use 0 for camera or pass a path to file)");
			return;
		}
		Main main = new Main();
		main.processVideo(args[0]);
	}

	// ////////////////
	// //// SETUP /////
	// ////////////////

	public void processVideo(String path) {
		// Set library
		System.load(LIB_OPENCV_JAVA);
		face_cascade = new CascadeClassifier(CASCADE_PATH);

		// Open empty main window
		Display display = new Display();
		JFrame frame = getVideoFrame(display);

		// Set capture device
		VideoCapture capture;
		if (path.equals("0")) {
			capture = new VideoCapture(0);
		} else {
			capture = new VideoCapture(path);
		}

		// Check capture device
		if (!capture.isOpened()) {
			System.out.println("Could not open video.");
			return;
		}

		// Check input
		Mat lastFrame = new Mat();
		capture.read(lastFrame);
		if (lastFrame.empty()) {
			System.out.println(" --(!) No captured frame -- Break!");
			return;
		}

		// Open stats window and adjust main window
		startCharts(lastFrame.width(), lastFrame.height());
		frame.setSize(lastFrame.width() + 40, lastFrame.height() + 60);

		// Start main loop
		while (mainLoop(capture, display)) {
		}
	}

	private static JFrame getVideoFrame(Display display) {
		String window_name = "Capture - Face detection";
		JFrame frame = new JFrame(window_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setContentPane(display);
		frame.setVisible(true);
		return frame;
	}

	private void startCharts(int width, int height) {
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

	// ///////////////////
	// /// MAIN LOOP /////
	// ///////////////////

	private boolean mainLoop(VideoCapture capture, Display display) {
		Mat lastFrame = new Mat();
		capture.read(lastFrame);
		if (lastFrame.empty()) {
			System.out.println(" --(!) No captured frame -- Break!");
			return false;
		}

		// 1. Apply the classifier to the captured image
		MatOfRect faces = detect(lastFrame);
		// 2. Analyze data
		faceLogger.tick(faces);
		// 3. Update the charts
		updateCharts();
		// 4. Mark faces on image
		faceLogger.markFaces(lastFrame);
		// 5. Display the image
		display.MatToBufferedImage(lastFrame);
		display.repaint();

		return true;
	}

	private MatOfRect detect(Mat frameIn) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		frameIn.copyTo(mRgba);
		frameIn.copyTo(mGrey);
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);

		MatOfRect faces = new MatOfRect();
		face_cascade.detectMultiScale(mGrey, faces);

		return faces;
	}

	private void updateCharts() {
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
