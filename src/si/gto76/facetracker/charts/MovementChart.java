package si.gto76.facetracker.charts;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.opencv.core.Point;

import si.gto76.facetracker.MyColor;

public class MovementChart extends ApplicationFrame {

	private static int RANGE_SECONDS = 60;
	private static int RANGE_SIZE = 4;

	private final int width;
	private final int height;
	
	final XYSeriesCollection seriesCollection = new XYSeriesCollection();
	JFreeChart chart;

	public MovementChart(final String title, int width, int height) {
		super(title);
		this.width = width;
		this.height = height;

		chart = createChart(seriesCollection, width, height);
		chart.removeLegend();

		final ChartPanel chartPanel = new ChartPanel(chart);
		final JPanel content = new JPanel(new BorderLayout());
		content.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(content);
	}

	private JFreeChart createChart(final XYSeriesCollection dataset, int width, int height) {
		final JFreeChart chart = ChartFactory.createXYLineChart("test", // chart title
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

		return chart;
	}
	
	public void refresh(Map<MyColor,Point> values) {
		for (MyColor color: values.keySet()) {
			XYSeries series = null;
			try {
				series = seriesCollection.getSeries(color);
			} catch(org.jfree.data.UnknownKeyException e) {
				
			}
			Point value = values.get(color);
			if (series == null) {
				addNewSeries(color, value);
			} else {
				series.add(value.x, height - value.y);
			}
		}
	}
	
	private void addNewSeries(MyColor color, Point value) {
		XYSeries series = new XYSeries("test", false);
		series.setKey(color);
		series.add(value.x, height - value.y);
		seriesCollection.addSeries(series);
	}
}
