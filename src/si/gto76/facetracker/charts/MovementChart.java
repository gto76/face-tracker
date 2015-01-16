package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.opencv.core.Point;

import si.gto76.facetracker.MyColor;

public class MovementChart extends JPanel {

	private static final int MAXIMUM_VALUES = 30;
	private static final long SERIES_AGE_TRESHOLD = 1000;
	private final int width;
	private final int height;

	JFreeChart chart;
	final XYSeriesCollection seriesCollection = new XYSeriesCollection();

	final Map<MyColor, Long> seriesStalenes = new HashMap<MyColor, Long>();

	public MovementChart(final String title, int width, int height) {
		super();
		this.width = width;
		this.height = height;

		createChart(seriesCollection, width, height);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
//		final JPanel content = new JPanel(new BorderLayout());
//		content.add(chartPanel);
//		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
//		setContentPane(content);
		
		this.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	}

	private void createChart(final XYSeriesCollection dataset, int width, int height) {
		chart = ChartFactory.createXYLineChart("test", // chart title
				"X", // x axis label
				"Y", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		final XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setRange(0.0, width);
		axis = plot.getRangeAxis();
		axis.setAutoRange(true);
		axis.setRange(0.0, height);
	}

	public void refresh(Map<MyColor, Point> values) {
		for (MyColor color : values.keySet()) {
			XYSeries series = null;
			try {
				series = seriesCollection.getSeries(color);
			} catch (org.jfree.data.UnknownKeyException e) {

			}
			Point value = values.get(color);
			if (series == null) {
				addNewSeries(color, value);
			} else {
				series.add(value.x, height - value.y);
			}
			Long now = System.currentTimeMillis();
			seriesStalenes.put(color, now);
		}
		removeUnusedSeries();
	}

	private void addNewSeries(MyColor color, Point value) {
		XYSeries series = new XYSeries("test", false);
		series.setMaximumItemCount(MAXIMUM_VALUES);
		series.setKey(color);
		series.add(value.x, height - value.y);
		seriesCollection.addSeries(series);
		setColor(series, color);
	}
	
	private void setColor(Series series, MyColor color) {
		int seriesIndex = seriesCollection.getSeriesIndex(color);
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
					XYSeries series = seriesCollection.getSeries(color);
					seriesCollection.removeSeries(series);
				} catch (org.jfree.data.UnknownKeyException e) {

				}
			}
		}
	}
}
