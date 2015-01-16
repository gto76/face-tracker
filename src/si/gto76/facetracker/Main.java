package si.gto76.facetracker;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.ui.RefineryUtilities;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import si.gto76.facetracker.charts.MovementChart;
import si.gto76.facetracker.charts.NumberChart;
import si.gto76.facetracker.charts.SizeChart;
import si.gto76.facetracker.charts.VectorChart;

public class Main extends JPanel {

	public static final String LIB_OPENCV_JAVA = "D:\\DESKTOP-DATA\\home\\downloads\\opencv\\opencv\\build\\java\\x64\\opencv_java300.dll";

	ChartsWindow chartsWindow;
	static NumberChart noOfFacesChart;
	static SizeChart sizeChart;
	static MovementChart positionChart;
	static VectorChart vectorChart;

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
				sizeChart.refresh(faceSizes);
				
				Map<MyColor, Point> facePositions = faceLogger.getFacePositions();
				positionChart.refresh(facePositions);

				Map<MyColor, Point> faceMovements = faceLogger.getFaceMovements();
				vectorChart.refresh(faceMovements);
			}
		}
	}

	public static void startCharts(int width, int height) {
		
//		noOfFacesChart = new NumberChart("Number of faces");
//		noOfFacesChart.pack();
//		RefineryUtilities.centerFrameOnScreen(noOfFacesChart);
//		noOfFacesChart.setVisible(true);
//
//		sizeChart = new SizeChart("Sizes of faces");
//		sizeChart.pack();
//		RefineryUtilities.centerFrameOnScreen(sizeChart);
//		sizeChart.setVisible(true);
//
//		positionChart = new MovementChart("Movement of faces", width, height);
//		positionChart.pack();
//		RefineryUtilities.centerFrameOnScreen(positionChart);
//		positionChart.setVisible(true);
//		
//		vectorChart = new VectorChart("Vectors of movement");
//		vectorChart.pack();
//        RefineryUtilities.centerFrameOnScreen(vectorChart);
//        vectorChart.setVisible(true);
		

		List<Pair<JPanel, Dimension>> chartPanelsWithSizes = new ArrayList<Pair<JPanel,Dimension>>();
        
		noOfFacesChart = new NumberChart("Number of faces");
		sizeChart = new SizeChart("Sizes of faces");
		positionChart = new MovementChart("Movement of faces", width, height);
		vectorChart = new VectorChart("Vectors of movement");

		chartPanelsWithSizes.add(Pair.of((JPanel) noOfFacesChart, (Dimension) null));
		chartPanelsWithSizes.add(Pair.of((JPanel) sizeChart, (Dimension) null));
		chartPanelsWithSizes.add(Pair.of((JPanel) positionChart, (Dimension) null));
		chartPanelsWithSizes.add(Pair.of((JPanel) vectorChart, (Dimension) null));

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
