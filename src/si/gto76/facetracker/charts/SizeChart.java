package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.Series;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;

import si.gto76.facetracker.MyColor;

public class SizeChart extends JPanel  {
	private static final String TITLE = "Sizes";
	
	private static int RANGE_SECONDS = 60;
	private static double RANGE_SIZE = 40000.0;

	JFreeChart chart;
	final TimeSeriesCollection seriesCollection = new TimeSeriesCollection();

	public SizeChart(final String title) {
		super();
		createChart(seriesCollection);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(1920);
		chartPanel.setMaximumDrawHeight(1200);
		
		this.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(620, 270));
	}

	private void createChart(final XYDataset dataset) {
		chart = ChartFactory.createTimeSeriesChart(TITLE, "Time", "Value",
				dataset, true, true, false);
		final XYPlot plot = chart.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(RANGE_SECONDS * 1000);
		
		NumberAxis axisX = (NumberAxis) plot.getRangeAxis();
		axisX.setRange(0.0, RANGE_SIZE);
		axisX.setAutoRangeIncludesZero(true);
		axisX.setAutoRange(true);
	}

	public void refresh(Map<MyColor,Double> values) {
		final Millisecond now = new Millisecond();
		
		for (MyColor color: values.keySet()) {
			TimeSeries series = seriesCollection.getSeries(color);
			Double value = values.get(color);
			if (series == null) {
				addNewSeries(color, now, value);
			} else {
				series.add(now, value);
			}
		}
	}
	
	private void addNewSeries(MyColor color, Millisecond now, Double value) {
		TimeSeries series = new TimeSeries("Random Data", Millisecond.class);
		series.setKey(color);
		series.add(now, value);
		seriesCollection.addSeries(series);
		setColor(series, color);
	}
	
	private void setColor(Series series, MyColor color) {
		int seriesIndex = seriesCollection.getSeriesIndex(color);
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(seriesIndex, color.c);
	}
}