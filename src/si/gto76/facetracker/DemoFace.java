package si.gto76.facetracker;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class DemoFace {

	private CascadeClassifier face_cascade;
	private static final String CASCADE_PATH = "D:\\DESKTOP-DATA\\home\\downloads\\opencv\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface.xml";
	private static final String IMAGE_PATH = "D:\\DESKTOP-DATA\\home\\pictures\\Education_in_Iran_0003_Urmia.jpg"; // D:\\DESKTOP-DATA\\home\\pictures\\face.jpg

	public DemoFace() {
		face_cascade = new CascadeClassifier(CASCADE_PATH);
	}

	public ImageWithData detect(Mat inputframe) {
		Mat mRgba = new Mat();
		Mat mGrey = new Mat();
		MatOfRect faces = new MatOfRect();
		inputframe.copyTo(mRgba);
		inputframe.copyTo(mGrey);
		Imgproc.cvtColor(mRgba, mGrey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(mGrey, mGrey);
		face_cascade.detectMultiScale(mGrey, faces);

		int noOfFaces = faces.toArray().length;
		//System.out.println(String.format("Detected %s faces", noOfFaces));
		for (Rect rect : faces.toArray()) {
			Point center = new Point(rect.x + rect.width * 0.5, rect.y + rect.height * 0.5);
			Imgproc.rectangle(mRgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y
					+ rect.height), new Scalar(0, 255, 0));
			// Core.ellipse( mRgba, center, new Size( rect.width*0.5, rect.height*0.5), 0, 0, 360, new Scalar(
			// 255, 0, 255 ), 4, 8, 0 );
		}
		ImageWithData result = new ImageWithData(mRgba, faces);
		return result;
	}

	public void demo2() {
		VideoCapture vc = new VideoCapture(0);
		System.out.println(vc.isOpened());

		CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_PATH);

		while (true) {
			Mat frame = new Mat();
			vc.read(frame);
			System.out.println(frame.cols() + " " + frame.rows());

			MatOfRect faceDetections = new MatOfRect();
			faceDetector.detectMultiScale(frame, faceDetections);

			//System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		}

	}

	public void demo() {
		// Create a face detector from the cascade file in the resources
		// directory.
		CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_PATH);
		Mat image = Imgcodecs.imread(IMAGE_PATH);
		System.out.println("#### image " + image);

		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);

		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		// Draw a bounding box around each face.
		for (Rect rect : faceDetections.toArray()) {
			Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y
					+ rect.height), new Scalar(0, 255, 0));
		}

		// Save the visualized detection.
		String filename = "faceDetection.png";
		System.out.println(String.format("Writing %s", filename));
		Imgcodecs.imwrite(filename, image);
	}

}

class ImageWithData {
	public final Mat image;
	public final MatOfRect faces;

	public ImageWithData(Mat image, MatOfRect faces) {
		this.image = image;
		this.faces = faces;
	}

	public int getNoOfFaces() {
		return faces.toArray().length;
	}

	public List<Double> getSizes() {
		List<Double> sizes = new ArrayList<Double>();
		for (Rect face : faces.toArray()) {
			sizes.add(face.area());
		}
		return sizes;
	}
}
