package si.gto76.facetracker.charts;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.opencv.core.Point;

import si.gto76.facetracker.MyColor;

public class PositionChart extends JPanel {
	private static final String TITLE = "Positions";

	private static final int MAXIMUM_VALUES = 20;
	private static final long SERIES_AGE_TRESHOLD = 1000;

	private static final int DOT_SIZE = 4;
	private final int width;
	private final int height;

	JFreeChart chart;
	XYPlot plot;
	final XYDotRenderer dotRenderer;
	final XYSeriesCollection seriesCollection = new XYSeriesCollection();

	final Map<MyColor, Long> seriesStalenes = new HashMap<MyColor, Long>();

	public PositionChart(final String title, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		
		dotRenderer = new XYDotRenderer();
		dotRenderer.setDotHeight(DOT_SIZE);
		dotRenderer.setDotWidth(DOT_SIZE);

		createChart(seriesCollection, width, height);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(1920);
		chartPanel.setMaximumDrawHeight(1200);
		
		this.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(350, 270));
	}

	private void createChart(final XYSeriesCollection dataset, int width, int height) {
		chart = ChartFactory.createXYLineChart(TITLE, "X", "Y", dataset, 
					PlotOrientation.VERTICAL, true, true, false );
		plot = chart.getXYPlot();
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
			XYSeries dotSeries = null;

			//System.out.println("#### Color "+color);
			//System.out.println("#### Dot color "+getDotColor(color));
			
			try {
				series = seriesCollection.getSeries(color);
			} catch (org.jfree.data.UnknownKeyException e) {
				//System.out.println("#### Series key exception "+e);
			}
			try {
				dotSeries = seriesCollection.getSeries(getDotColor(color));
			} catch (org.jfree.data.UnknownKeyException e) {
				//System.out.println("#### Dot series key exception "+e);
			}
			Point value = values.get(color);
			
			if (series == null) {
				//System.out.println("#### series does not exist");
				addNewSeries(color, value);
			} else {
				//System.out.println("#### series exists");
				series.add(value.x, height - value.y);
			}
			
			if (dotSeries == null) {
				//System.out.println("#### dotseries does not exist");
				MyColor dotColor = getDotColor(color);
				addNewSeries(dotColor, value);
				//int seriesIndex = seriesCollection.getSeriesIndex(dotColor);
				//plot.setRenderer(seriesIndex, dotRenderer);
			} else {
				//System.out.println("#### dotseries exists");
				dotSeries.clear();
				dotSeries.add(value.x, height - value.y);
			}
			
			Long now = System.currentTimeMillis();
			seriesStalenes.put(color, now);
		}
		removeUnusedSeries();
	}
	
	private MyColor getDotColor(MyColor color) {
		return new MyColor(new Color(color.c.getRGB()-1));
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
					// also remove the dot series
					XYSeries dSeries = seriesCollection.getSeries(getDotColor(color));
					seriesCollection.removeSeries(dSeries);
				} catch (org.jfree.data.UnknownKeyException e) {

				}
			}
		}
	}
}
