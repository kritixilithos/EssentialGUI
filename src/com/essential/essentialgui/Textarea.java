package com.essential.essentialgui;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;

public class Textarea {
	private PApplet p;
	private ArrayList<ArrayList> posX;
	private ArrayList<String> typed = new ArrayList<>();
	private int textIndex = 0;
	private int current_line = 0;

	private int startX, startY,
					endW, endH;
	private int textX, textY;
	private int fontSize, background, textColor;
	private int cursorX;

	private int spaceWidth = 0;

	private boolean newKey = false;
	private boolean backspaced = false;
	private boolean entered = false;

	private boolean started = false;

	public Textarea(PApplet parent, int _startX, int _startY, int _width, int _height) {
		posX = new ArrayList<>();
		posX.add(new ArrayList<>());

		p = parent;

		startX = _startX;
		startY = _startY;
		endW = _width;
		endH = _height;

		fontSize = 12;
		p.textSize(fontSize);

		background = p.color(255, 255, 255);
		textColor = p.color(0, 0, 0);

		textX = startX + 2;
		textY = startY + fontSize;

		cursorX = textX;

		typed.add("");
	}

	public void setFontSize(int _fontSize) {
		fontSize = _fontSize;
	}

	public void setBackgroundColor(int _background) {
		background = _background;
	}

	public void setTextColor(int _textColor) {
		textColor = _textColor;
	}

	public void updateAndDisplay() {
		calculateSpaceWidth();

		//Textarea box
		p.fill(background);
		p.noStroke();
		p.rect(startX, startY, endW, endH);

		for (int i=0; i<current_line+1; i++) {
			p.fill(0);
			//p.println(typed.get(i));
			//TODO: this should be `typed.get(i)`, but it borks when the current_line is set to i
			p.text(typed.get(current_line).toString(), textX, textY + i * fontSize);
		}

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
		p.rect(typed.get(current_line).length() == textIndex ? cursorX + 2 : cursorX,
				startY + 2 + current_line * fontSize, 1, fontSize - 1);
	}

	public void keyUpdate(char key) {
		if (p.key == PConstants.CODED) {
			if (p.keyCode == PConstants.LEFT) {
				textIndex--;
				if (textIndex < 0)
					textIndex = 0;
			} else if (p.keyCode == PConstants.RIGHT) {
				textIndex++;
				if (textIndex > typed.get(current_line).length())
					textIndex = typed.get(current_line).length();
			} else if (p.keyCode == PConstants.UP) {
				if (current_line > 0) {
					current_line -= 1;

					if (textIndex > typed.get(current_line).length())
						textIndex = typed.get(current_line).length() - 1;
				}
			} else if (p.keyCode == PConstants.DOWN) {
				if (current_line < typed.size() - 1) {
					current_line += 1;

					if (textIndex > typed.get(current_line).length())
						textIndex = typed.get(current_line).length();
				}
			}
			newKey = true;
		} else {
			if (p.keyCode == 8) {
				if (textIndex > 0 && textIndex <= typed.get(current_line).length()) {
					textIndex--;
					insertCharIntoTyped();
					posX.get(current_line).remove(textIndex);
					backspaced = true;
				}
			} else if (p.keyCode == 10) {
				if (current_line == typed.size() - 1) {
					typed.add("");
					posX.add(new ArrayList<>());
					textIndex = 0;
				} else {
					//TODO: check this
					for (int i = typed.size() - 1; i > textIndex; i++) {
						typed.set(
								i, typed.get(i - 1)
						);
						posX.set(
								i, posX.get(i - 1)
						);
					}
					typed.set(textIndex, "");
					//TODO: for posX
				}
				current_line ++;
				cursorX = textX;
				textIndex = 0;
				entered = true;
			} else {
				insertCharIntoTyped(key);
				textIndex++;
			}
			newKey = true;
		}
	}

	private void calculateCursorX() {
		int oldX = textX + cumSum(posX.get(current_line), posX.get(current_line).size());
		cursorX = textX;
		for (int j = startY + current_line*fontSize+2; j <= startY + current_line*fontSize + fontSize; j++) {
			for (int i = textX; i < endW + startX; i++) {
				if (p.get(i,j) != background && i>cursorX) {
					cursorX = i;
				}
			}
		}
		for (int i = typed.get(current_line).length() - 1; i >= 0; i--) {
			if (typed.get(current_line).charAt(i) == ' ') {
				cursorX += spaceWidth;
			} else {
				break;
			}
		}

		int newX = cursorX;

		int change = newX - oldX;

		if (newKey && change > 0 && !backspaced) {
			posX.get(current_line).add(textIndex-1, change);
		}

		backspaced = false;
		entered = false;

		if (posX.get(current_line).size() > 0) {
			System.out.println(textIndex + " " + posX);
			cursorX = textX + cumSum(posX.get(current_line), textIndex);
		}
	}

	private void insertCharIntoTyped(char c) {
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

	private int cumSum(ArrayList<Integer> list, int finalIndex) {
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
