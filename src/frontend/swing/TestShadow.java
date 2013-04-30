package frontend.swing;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

/*
 * Note: Shadow on Jpanel http://www.curious-creature.org/2007/08/01/rounded-corners-and-shadow-for-dialogs-extreme-gui-makeover/
 */
public class TestShadow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("hey");
		frame.setMinimumSize(new Dimension(500,500));
		frame.setUndecorated(true);
		frame.setBackground(new Color(1.0f,1.0f,1.0f,0.5f));
		frame.setVisible(true);
	}

}