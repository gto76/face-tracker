package si.gto76.facetracker;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.ui.ApplicationFrame;

public class ChartsWindow extends ApplicationFrame {
	
	public ChartsWindow(String title, List<JPanel> chartPanelsWithSizes) {
		super(title);

		final JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
		for (JPanel panel : chartPanelsWithSizes) {
			content.add(panel);
		}
		setContentPane(content);
	}

}
