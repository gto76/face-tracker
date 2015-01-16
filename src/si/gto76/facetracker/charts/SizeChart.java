package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;

import si.gto76.facetracker.MyColor;

public class SizeChart extends ApplicationFrame  {
	
	private static int RANGE_SECONDS = 60;
	private static int RANGE_SIZE = 4;

	final TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
	JFreeChart chart;

	public SizeChart(final String title) {
		super(title);
		chart = createChart(seriesCollection);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		final JPanel content = new JPanel(new BorderLayout());
		content.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(content);
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart("Dynamic Data Demo", "Time", "Value",
				dataset, true, true, false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(RANGE_SECONDS * 1000);
		axis = plot.getRangeAxis();
		axis.setRange(0.0, RANGE_SIZE);
		axis.setAutoRange(true);
		return result;
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
	}
}