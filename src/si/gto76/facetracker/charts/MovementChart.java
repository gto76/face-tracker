package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.xy.VectorSeries;
import org.jfree.data.xy.VectorSeriesCollection;
import org.jfree.data.xy.VectorXYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.opencv.core.Point;

import si.gto76.facetracker.MyColor;

public class MovementChart extends JPanel {
	private static final String TITLE = "Movements";
	
	private static final int RANGE = 60;
	private static final long SERIES_AGE_TRESHOLD = 200;

	JFreeChart chart;
	final VectorSeriesCollection seriesCollection = new VectorSeriesCollection();

	private Map<MyColor,Integer> collectionIndexes = new HashMap<MyColor, Integer>();
	final Map<MyColor, Long> seriesStalenes = new HashMap<MyColor, Long>();

	public MovementChart(final String title) {
		super();

		createChart(seriesCollection);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(1200);
		chartPanel.setMaximumDrawHeight(1200);
		
		this.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(270, 270));
	}

	private void createChart(VectorXYDataset dataset) { 
		NumberAxis xAxis = new NumberAxis("X");
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setRange(-RANGE, RANGE);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis("Y");
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		yAxis.setRange(-RANGE, RANGE);
		yAxis.setAutoRangeIncludesZero(false);
		//
		VectorRenderer renderer = new VectorRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		plot.setOutlinePaint(Color.black);
		chart = new JFreeChart(TITLE, plot);
		chart.setBackgroundPaint(Color.white);
	}

	public void refresh(Map<MyColor, Point> values) {
		for (MyColor color : values.keySet()) {
			Point value = values.get(color);
			
			VectorSeries series = null;
			
			try {
				series = getSeries(color);
			} catch (org.jfree.data.UnknownKeyException e) {
			}
			
			if (series == null) {
				addNewSeries(color, value);
			} else {
				addToSeries(series, value);
			}
			
			Long now = System.currentTimeMillis();
			seriesStalenes.put(color, now);
		}
		removeUnusedSeries();
	}

	private void addNewSeries(MyColor color, Point value) {
		VectorSeries series = new VectorSeries("test");
		series.setKey(color);
		addToSeries(series, value);
		seriesCollection.addSeries(series);
		setColor(series, color);
	}
	
	private static void addToSeries(VectorSeries series, Point point) {
		series.clear();
		series.add(0, 0, point.x, -point.y);
	}
	
	private VectorSeries getSeries(MyColor color) {
		int seriesIndex = seriesCollection.indexOf(color);
		if (seriesIndex == -1) {
			throw new org.jfree.data.UnknownKeyException("getSeries fail");
		}
		return seriesCollection.getSeries(seriesIndex);
	}

	private void setColor(Series series, MyColor color) {
		int seriesIndex = seriesCollection.indexOf(color);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(seriesIndex, color.c);
	}

	private void removeUnusedSeries() {
		Long now = System.currentTimeMillis();
		for (MyColor color : seriesStalenes.keySet()) {
			Long lastSeen = seriesStalenes.get(color);
			long age = now - lastSeen;
			if (age > SERIES_AGE_TRESHOLD) {
				try {
					VectorSeries series = getSeries(color);
					series.clear();
				} catch (org.jfree.data.UnknownKeyException e) {

				}
			}
		}
	}
}