import processing.core.PApplet;
import java.util.ArrayList;

public class Textarea {
	PApplet p;
	ArrayList<Integer> posX;
	int textIndex = 0;
	String typed = "";

	int startX;
	int startY;
	int endW;
	int endH;

	int textX;
	int textY;

	int cursorX;

	int spaceWidth = 0;

	boolean newKey = false;
	boolean backspaced = false;

	boolean started = false;

	Textarea (PApplet parent, int _startX, int _startY, int _width, int _height) {
		p = parent;
		p.textSize(16);
		posX = new ArrayList<Integer>();

		startX = _startX;
		startY = _startY;
		endW = _width;
		endH = _height;

		textX = startX + 2;
		textY = startY + 16;

		cursorX = textX;
	}

	void updateAndDisplay() {
		calculateSpaceWidth();

		p.background(255);
		p.fill(0);
		p.text(typed, textX, textY);

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
		p.rect(cursorX+2, startY+2, 1, 15);
	}

	void keyUpdate(char key) {
		if (p.key == p.CODED) {
			if (p.keyCode == p.LEFT) {
				textIndex--;
				if (textIndex < 0) textIndex = 0;
			} else if (p.keyCode == p.RIGHT) {
				textIndex++;
				if (textIndex > typed.length()) textIndex = typed.length();
			}
			newKey = true;
		} else {
			if (p.keyCode == 8) {
				if (textIndex > 0 && textIndex <= typed.length()) {
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


	void calculateCursorX() {
		int oldX = textX + cumSum(posX, posX.size());
		cursorX = textX;
		for(int j=startY;j<=textY;j++) {
			for(int i=textX;i<endW+startX;i++) {
				if (p.get(i,j) != p.color(255) && i>cursorX) {
					cursorX=i;
				}
			}
		}
		for (int i=typed.length()-1; i>=0; i--) {
			if (typed.charAt(i) == ' ') {
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

	void insertCharIntoTyped (char c) {
		typed = typed.substring(0, textIndex) + c + typed.substring(textIndex, typed.length());
	}

	void insertCharIntoTyped () {
		typed = typed.substring(0, textIndex) + typed.substring(textIndex + 1, typed.length());
	}

	void calculateSpaceWidth() {
		p.background(255);

		if (!started) {
			p.fill(0);
			p.text("H f", textX, textY);
			calculateCursorX();
			int withSpace = cursorX;

			p.background(255);

			p.text("Hf", textX, textY);
			calculateCursorX();
			int sansSpace = cursorX;

			spaceWidth = withSpace - sansSpace;
			cursorX = textX;
			started = true;
		}
	}

	int cumSum (ArrayList<Integer> list, int finalIndex) {
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
