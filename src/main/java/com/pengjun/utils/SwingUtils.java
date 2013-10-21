package com.pengjun.utils;

import java.awt.Toolkit;

import javax.swing.JFrame;

public class SwingUtils {

	public static void setFrameCenter(JFrame frame) {

		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getWidth();
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize()
				.getHeight();
		frame.setLocation((screenWidth - frame.getWidth()) / 2,
				(screenHeight - frame.getHeight()) / 2);

	}

	public static JFrame createFrame(int width, int height) {

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setVisible(true);

		return frame;
	}
}
