package si.gto76.facetracker.charts;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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

import si.gto76.facetracker.MyColor;

public class CounterChart extends JPanel  {
	private static final String TITLE = "Counter";
	
	private static int RANGE_SECONDS = 60;
	private static double RANGE_FACES = 3.0;

	/** The time series data. */
	private TimeSeries series;

	/** The most recent value added. */
	private double lastValue = 100.0;

	JFreeChart chart;

	public CounterChart(final String title) {
		super();
		this.series = new TimeSeries(TITLE, Millisecond.class);
		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		chart = createChart(dataset);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMinimumDrawWidth(0);
		chartPanel.setMinimumDrawHeight(0);
		chartPanel.setMaximumDrawWidth(1920);
		chartPanel.setMaximumDrawHeight(1200);
		
		this.add(chartPanel);
		// So that its aligned with sizes chart
		this.setBorder(new EmptyBorder(0, 21, 0, 0));
		chartPanel.setPreferredSize(new java.awt.Dimension(600, 185));
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart(TITLE, "Time", "Value",
				dataset, true, true, false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(RANGE_SECONDS * 1000);

		NumberAxis axisX = (NumberAxis) plot.getRangeAxis();
		axisX.setRange(0.0, RANGE_FACES);
		axisX.setAutoRangeIncludesZero(true);
		axisX.setAutoRange(true);

		XYItemRenderer renderer = plot.getRenderer();
		renderer.setPaint(Color.BLACK);
		
		return result;
	}


	public void refresh(double value) {
		this.lastValue = value;
		this.series.add(new Millisecond(), this.lastValue);
	}

}