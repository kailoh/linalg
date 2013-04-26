package frontend.containers;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import frontend.general.Constants;
import frontend.graphicsengine.Container;
import frontend.shapes.Coord;
import frontend.shapes.Text;

public class Solution extends Container {
	
	private Text _text;
	TeXIcon _ti;
	Coord _location;
	
	public Solution(Coord location, Coord size) {
		super(location,size);
		//_text = new Text(Constants.TEXT_FONTSTYLE, Constants.CONSTRUCT_INSTRUCTIONS_TEXT_STYLE, Constants.CONSTRUCT_INSTRUCTIONS_TEXT_SIZE, "The computed solution is here.", Constants.CONSTRUCT_INSTRUCTIONS_TEXT_COLOR, location.plus(Constants.CONSTRUCT_INSTRUCTIONS_TEXT_OFFSET));
		TeXFormula formula = new TeXFormula("\\text{Solutions will be displayed here}");
		_ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		_location = location;
	}
	
	@Override
	public void setSize(Coord size) {
		super.setSize(size);
	}
	
	@Override
	public void setLocation(Coord location) {
		super.setLocation(location);
//		_text.setLocation(location.plus(Constants.CONSTRUCT_INSTRUCTIONS_TEXT_OFFSET));
	}
	
	public void setTex(String tex){
		TeXFormula formula = new TeXFormula(tex);
		_ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
	}

	@Override
	public void onDraw(Graphics2D g) {
//		_text.onDraw(g);		
		_ti.paintIcon(new JLabel(), g, _location.x, _location.y);
	}
	@Override
	
	public void onMouseClicked(int clickCount, Coord c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDown(int keycode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUp(int keycode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRepeated(int keycode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTyped(int keycode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMousePressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseReleased() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseDragged(Coord location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseMoved(Coord location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheelForward(Coord location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseWheelBackward(Coord location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragStart(Coord location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragEnd(Coord location) {
		// TODO Auto-generated method stub
		
	}

}