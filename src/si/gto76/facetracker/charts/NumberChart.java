package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
import org.jfree.ui.RefineryUtilities;

/**
 * A demonstration application showing a time series chart where you can dynamically add (random) data by
 * clicking on a button.
 * 
 */
public class NumberChart extends JPanel  {
	
	private static int RANGE_SECONDS = 60;
	private static int RANGE_FACES = 4;

	/** The time series data. */
	private TimeSeries series;

	/** The most recent value added. */
	private double lastValue = 100.0;

	JFreeChart chart;

	public NumberChart(final String title) {
		super();
		this.series = new TimeSeries("Random Data", Millisecond.class);
		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		chart = createChart(dataset);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);

		//final JPanel content = new JPanel(new BorderLayout());
		//content.add(chartPanel);
		//chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		//setContentPane(content);
		
		this.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	}

	private JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart result = ChartFactory.createTimeSeriesChart("Dynamic Data Demo", "Time", "Value",
				dataset, true, true, false);
		final XYPlot plot = result.getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(RANGE_SECONDS * 1000);
		axis = plot.getRangeAxis();
		axis.setRange(0.0, RANGE_FACES);
		axis.setAutoRange(true);
		return result;
	}

	public void refresh(double value) {
		this.lastValue = value;
		final Millisecond now = new Millisecond();
		//System.out.println("Now = " + now.toString());
		this.series.add(new Millisecond(), this.lastValue);
	}

}