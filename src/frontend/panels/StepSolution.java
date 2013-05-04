package frontend.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import frontend.swing.CurrentConstants;
import frontend.swing.ScrollPane;

public class StepSolution extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1780266591415441861L;
	private Saved _savePanel;
	private Solution _display, _answer, _comp;
	private List<String> _solList, _compList;
	private int _stepNumber = 0;
	private TeXIcon _ti;
	
	public StepSolution(){
		super();
		this.setLayout(new BorderLayout());
		this.setBackground(CurrentConstants.STEPSOLUTION_BG);
		this.setBorder(CurrentConstants.STEPSOLUTION_BORDER);
		this.setPreferredSize(new Dimension(100,100));

		_solList = new ArrayList<>();
		
		_display = new Solution();
		_display.addMouseListener(new Listener(this));
		ScrollPane scroll = new ScrollPane(_display);
		scroll.setBorder(CurrentConstants.STEPSOLUTION_SCROLL_BORDER);
		scroll.setBackground(CurrentConstants.STEPSOLUTION_SCROLL_BG);
		
		JPanel bottomBar = new JPanel(new BorderLayout());
		bottomBar.setBorder(CurrentConstants.STEPSOLUTION_BOTTOMBAR_BORDER);
		bottomBar.setBackground(CurrentConstants.STEPSOLUTION_BOTTOMBAR_BG);
		
		_comp = new Solution();
		_answer = new Solution(30);
		
		bottomBar.add(_comp, BorderLayout.CENTER);
		bottomBar.add(_answer, BorderLayout.EAST);
		
		this.add(scroll, BorderLayout.CENTER);
		this.add(bottomBar, BorderLayout.SOUTH);
		
		this.revalidate();
	}
	
	
	public void setSavedPanel(Saved savePanel){
		_savePanel = savePanel;
	}
	
	public TeXIcon getIcon(String s){
		TeXFormula formula = new TeXFormula(s);
		return formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
	}
	
	public void setSolution(List<List<String>> steps, String answer){
		_solList = steps.get(1);
		_compList = steps.get(0);
		_stepNumber = 0;
		_display.setTex("\\text{Step " + (_stepNumber + 1) + "}\\\\" + _solList.get(0));
		_comp.setTex(_compList.get(0));
		_answer.setTex(answer);
		this.revalidate();
		this.repaint();
	}
	
	public void next(){
		if(_stepNumber < _solList.size() - 1){
			_stepNumber++;
		}
		_display.setTex("\\text{Step " + (_stepNumber + 1) + "}\\\\" + _solList.get(_stepNumber));
		_comp.setTex(_compList.get(_stepNumber));
		this.repaint();
	}
	
	private class Listener extends MouseAdapter{
		StepSolution _ss;
		public Listener(StepSolution ss){
			_ss = ss;
		}
		
		public void mouseClicked(MouseEvent e){
			_ss.next();
		}
	}
	
}
