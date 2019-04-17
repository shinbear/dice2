package DiceCrawler;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ReadProgress extends JFrame implements Runnable {
	private JFrame frame;
	private JPanel timePanel;
	private JLabel timeLabel;
	private JLabel displayArea;
	private int ONE_SECOND = 1000;
	private int total=0;
	private int page=0;
	private int row=0;
	private int sim_row=0;

	public ReadProgress() {
		timePanel = new JPanel();
		timeLabel = new JLabel("Status:");
		displayArea = new JLabel();

		timePanel.add(timeLabel);
		timePanel.add(displayArea);
		this.add(timePanel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(new Dimension(500, 200));
		this.setLocationRelativeTo(null);
	}
	
	public void setPanel(int total, int page, int row, int sim_row){
		this.total=total;
		this.page=page;  
		this.row=row;  
		this.sim_row=sim_row;  
		repaint();
		// timeLabel = new JLabel("Change: ");
	}

	public void run() {
		while (true) {
			displayArea.setText(" Total page:" + total + ", Current page:" + page + " Current row:" + row
					+ " Current sim_row:" + sim_row);
			try {
				Thread.sleep(ONE_SECOND);
			} catch (Exception e) {
				displayArea.setText("Error!!!");
			}
		}
	}

}
