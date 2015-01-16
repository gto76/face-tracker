package si.gto76.facetracker;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.ui.ApplicationFrame;

public class ChartsWindow extends ApplicationFrame {
	
	public ChartsWindow(String title, List<Pair<JPanel, Dimension>> chartPanelsWithSizes) {
		super(title);

		final JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		for (Pair<JPanel, Dimension> panelAndSize : chartPanelsWithSizes) {
			JPanel panel = panelAndSize.getFirst();
			Dimension size = panelAndSize.getSecond();
			content.add(panel);
			//panel.setPreferredSize(size);
		}
		setContentPane(content);
	}

}
