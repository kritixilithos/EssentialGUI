package com.essential.essentialgui;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;

public class Textarea {
	private PApplet p;
	private ArrayList<Integer> posX;
	private ArrayList<String> typed = new ArrayList<>();
	private int textIndex = 0;
	private int current_line = 0;

	private int startX, startY,
					endW, endH;

	private int textX, textY;

	private int fontSize, background;

	private int cursorX;

	private int spaceWidth = 0;

	private boolean newKey = false;
	private boolean backspaced = false;

	private boolean started = false;

	Textarea (PApplet parent, int _startX, int _startY, int _width, int _height, int _fontSize, int _background, int _textColor) {
		p = parent;
		posX = new ArrayList<>();

		startX = _startX;
		startY = _startY;
		endW = _width;
		endH = _height;

		fontSize = _fontSize;
		p.textSize(fontSize);

		background = _background;

		textX = startX + 2;
		textY = startY + fontSize;

		cursorX = textX;

		typed.add("");
	}

	void updateAndDisplay() {
		calculateSpaceWidth();

		//Textarea box
		p.fill(background);
		p.noStroke();
		p.rect(startX, startY, endW, endH);

		p.fill(0);
		p.text(typed.get(current_line), textX, textY);

		if (newKey) {
			calculateCursorX();
			newKey = false;
		}

		//Border
		p.noFill();
		p.stroke(0);
		p.rect(startX, startY, endW, endH);

		//Cursor
		p.fill(0);
		p.rect(typed.get(current_line).length()==textIndex?cursorX+2:cursorX, startY+2, 1, fontSize-1);
	}

	void keyUpdate(char key) {
		if (p.key == PConstants.CODED) {
			if (p.keyCode == PConstants.LEFT) {
				textIndex--;
				if (textIndex < 0) textIndex = 0;
			} else if (p.keyCode == PConstants.RIGHT) {
				textIndex++;
				if (textIndex > typed.get(current_line).length())
					textIndex = typed.get(current_line).length();
			}
			newKey = true;
		} else {
			if (p.keyCode == 8) {
				if (textIndex > 0 && textIndex <= typed.get(current_line).length()) {
					textIndex--;
					insertCharIntoTyped();
					posX.remove(textIndex);
					backspaced = true;
				}
			} else {
				insertCharIntoTyped(key);
				textIndex++;
			}
			newKey = true;
		}
	}


	private void calculateCursorX() {
		int oldX = textX + cumSum(posX, posX.size());
		cursorX = textX;
		for(int j=startY;j<=textY;j++) {
			for(int i=textX;i<endW+startX;i++) {
				if (p.get(i,j) != background && i>cursorX) {
					cursorX=i;
				}
			}
		}
		for (int i=typed.get(current_line).length()-1; i>=0; i--) {
			if (typed.get(current_line).charAt(i) == ' ') {
				cursorX += spaceWidth;
			} else {
				break;
			}
		}

		int newX = cursorX;

		int change = newX - oldX;

		if (newKey && change > 0 && !backspaced) {
			posX.add(textIndex-1, change);
		}

		backspaced = false;

		if (posX.size() > 0) {
			System.out.println(textIndex + " " + posX);
			cursorX = textX + cumSum(posX, textIndex);
		}
	}

	private void insertCharIntoTyped (char c) {
		typed.set(
				current_line,
				typed.get(current_line).substring(0, textIndex) + c +
				typed.get(current_line).substring(textIndex, typed.get(current_line).length())
				);
	}

	private void insertCharIntoTyped() {
		typed.set(
				current_line,
				typed.get(current_line).substring(0, textIndex) +
				typed.get(current_line).substring(textIndex + 1, typed.get(current_line).length())
				);
	}

	private void calculateSpaceWidth() {
		if (!started) {
			p.fill(background);
			p.noStroke();
			p.rect(startX, startY, endW, endH);

			p.fill(0);
			p.text("H f", textX, textY);
			calculateCursorX();
			int withSpace = cursorX;

			p.fill(background);
			p.noStroke();
			p.rect(startX, startY, endW, endH);

			p.fill(0);

			p.text("Hf", textX, textY);
			calculateCursorX();
			int sansSpace = cursorX;

			spaceWidth = withSpace - sansSpace;
			cursorX = textX;
			started = true;
		}
	}

	private int cumSum (ArrayList<Integer> list, int finalIndex) {
		if (list.size() == 0) return 0;

		int sum = 0;
		for (int i = 0; i < finalIndex; i++) {
			if (i <= list.size()) {
				sum += list.get(i);
			}
		}
		return sum;
	}
}
