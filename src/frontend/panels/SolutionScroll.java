package frontend.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import frontend.swing.ScrollPane;

public class SolutionScroll extends JPanel {
	
	Solution _sol;
	JScrollPane _scroll;

	public SolutionScroll() {
		// TODO Auto-generated constructor stub
		this.setLayout(new BorderLayout());
		_sol = new Solution(this);
		
		_scroll = new ScrollPane(_sol);
		this.setPreferredSize(new Dimension(100,100));
		this.add(_scroll, BorderLayout.CENTER);
		_scroll.getVerticalScrollBar().setUnitIncrement(20);
	}
	
	public Solution getSolPanel(){
		return _sol;
	}
	
	public void resetScroll(){
		_scroll.getVerticalScrollBar().setValue(0);
		_scroll.getHorizontalScrollBar().setValue(0);
	}
}
