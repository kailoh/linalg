package frontend.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import frontend.blocks.BracketBlock;
import frontend.blocks.OpBlock;
import frontend.utils.WrapLayout;

import backend.blocks.Countable;
import backend.blocks.Numerical;
import backend.blocks.Op;
import backend.computations.infrastructure.Step;
import backend.main.Parser;
import backend.main.ParseNode;

public class Compute extends JPanel {

	Map<Integer, Numerical> _numericals;
	JPanel _computeBar, _pages, _ops, _bar;
	Integer _id = 0;
	Solution _solPanel;
	Solution _stepPanel;
	
	public Compute(Solution sol, Solution step) {
		_numericals = new LinkedHashMap<>();
		_solPanel = sol;
		_stepPanel = step;
		
		this.setLayout(new BorderLayout());
		_computeBar = new JPanel(new BorderLayout());
		_pages = new JPanel(new GridLayout(3, 1));
		_ops = new JPanel(new WrapLayout(FlowLayout.LEFT));
		
		//compute bar
		JPanel buttonPanel = new JPanel();
		JButton computeButton = new JButton("Compute");
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ClearButtonListener(this));
		computeButton.addActionListener(new SolButtonListener(this));
		_computeBar.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.add(clearButton);
		buttonPanel.add(computeButton);
		JPanel scrollPanel = new JPanel(new BorderLayout());
		scrollPanel.setPreferredSize(new Dimension(100,55));
		_bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JScrollPane scrollBar = new JScrollPane(_bar);
		scrollBar.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
		scrollPanel.add(scrollBar, BorderLayout.CENTER);
		_computeBar.add(scrollPanel, BorderLayout.CENTER);
		
		//pages
		JButton rank0Button = new JButton("Rank 0");
		JButton rank1Button = new JButton("Rank 1");
		JButton rank2Button = new JButton("Rank 2");
		_pages.add(rank0Button);
		_pages.add(rank1Button);
		_pages.add(rank2Button);
		
		//operations
		for(Op o : Op.values()){
			_ops.add(new OpBlock(o, o.getIcon(o), this));
		}
		//Bracket
		_ops.add(new BracketBlock(true, this));
		_ops.add(new BracketBlock(false, this));
		
		_computeBar.setBorder(BorderFactory.createLineBorder(Color.black));
		_pages.setBorder(BorderFactory.createLineBorder(Color.black));
		_ops.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.add(_computeBar, BorderLayout.SOUTH);
		//this.add(_pages, BorderLayout.WEST);
		this.add(_ops, BorderLayout.CENTER);
	}
	
	public void addToBar(Numerical n, String s){
		_numericals.put(_id, n);
		_bar.add(new BarObject(n, s, _id, this));
		_id++;
		this.revalidate();
		this.repaint();
	}
	
	public void removeFromBar(int id, Component c){
		_numericals.remove(id);
		_bar.remove(c);
		this.revalidate();
		this.repaint();
	}
	
	public void clearBar(){
		_numericals.clear();
		_bar.removeAll();
		this.repaint();
	}
	
	public void compute(){
		List<Numerical> l = new ArrayList<>();
		for(Numerical n : _numericals.values()){
			System.out.println(n.getName());
			l.add(n);
		}
		try{
			ParseNode result = Parser.parse(l);
			if(result == null){
				String s = "\\text{No solution found}";
				_stepPanel.setTex(s);
				_solPanel.setTex(s);
				return;
			}
			Countable answer = result.getSolution().getAnswer();
			_solPanel.setTex(answer.toLatex());
			
			//DFS for each child .getsolution().getsteps();
			
			List<String> ls = result.getSolution().getLatex();
			
			StringBuilder sb = new StringBuilder();
			_stepPanel.setTex(traverseTree(result, sb).toString());
		} catch (IllegalArgumentException e){
			System.out.println(e.getMessage());
			_solPanel.setTex("\\text{" + e.getMessage() + "}");
			_stepPanel.setTex("\\text{" + e.getMessage() + "}");
		}
	}
	
	public StringBuilder traverseTree(ParseNode n, StringBuilder sb){
		if(n.getLeft() != null){
			String toAdd = traverseTree(n.getLeft(), new StringBuilder()).toString();
			sb.append(toAdd);
		}
		if(n.getRight() != null){
			String toAdd = traverseTree(n.getLeft(), new StringBuilder()).toString();
			sb.append(toAdd);
		}
		List<String> list = n.getSolution().getLatex();
		for(String s : list){
			sb.append(s);
			sb.append("\\\\");
		}
		return sb;
	}
	
	private class BarObject extends JPanel implements MouseListener{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 3647938517413519896L;
		private Compute _c;
		private Integer _id;
		public BarObject(Numerical n, String s, int id, Compute c){
			this.setLayout(new BorderLayout());
			JLabel label = new JLabel(s);
			label.setForeground(Color.white);
			label.setHorizontalAlignment(JLabel.CENTER);
			this.setPreferredSize(new Dimension(40,30));
			this.setBackground(Color.blue);
			this.add(label, BorderLayout.CENTER);
			this.setToolTipText(s);
			_c = c;
			_id = id;
			this.addMouseListener(this);
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			_c.removeFromBar(_id, this);
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	private class SolButtonListener implements ActionListener{
		
		Compute _c;
		
		public SolButtonListener(Compute c){
			_c = c;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			_c.compute();
		}
		
	}
	
	private class ClearButtonListener implements ActionListener{
		
		Compute _c;
		
		public ClearButtonListener(Compute c){
			_c = c;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			_c.clearBar();
		}
		
	}

}
