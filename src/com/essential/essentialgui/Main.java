package com.essential.essentialgui;

import processing.core.PApplet;

public class Main extends PApplet {
	private Textarea textarea;

	public static void main (String[] args) {
		PApplet.main("com.essential.essentialgui.Main");
	}

	public void settings() {
		size(600, 400);
	}

	public void setup() {
		background(255);
		textarea = new Textarea(this, 100, 100, 400, 200, 16, color(255), color(0));
	}

	public void draw() {
		background(0, 255, 255);
		textarea.updateAndDisplay();
	}

	public void keyPressed() {
		textarea.keyUpdate(key);
	}
}
