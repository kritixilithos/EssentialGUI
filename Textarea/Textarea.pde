char[]text;
ArrayList<Integer> posX;
int textIndex = 0;
String typed = "";

int startX = 100;
int startY = 100;
int endW   = 400;
int endH   = 200;

int textX  = startX+2;
int textY  = startY+16;

int cursorX = textX;

int spaceWidth = 0;

boolean newKey = false;
boolean backspaced = false;

boolean started = false;

void setup() {
	size(600,400);
	textSize(16);
	text = new char[99];
	posX = new ArrayList<Integer>();
}

void draw() {
	calculateSpaceWidth();

	background(255);
	fill(0);
	text(typed, textX, textY);

	if (newKey) {
		calculateCursorX();
		newKey = false;
	}

	//Border
	noFill();
	stroke(0);
	rect(startX, startY, endW, endH);

	//Cursor
	fill(0);
	rect(cursorX+2, startY+2, 1, 15);
}

void keyPressed() {
	if (key == CODED) {
		if (keyCode == LEFT) {
			textIndex--;
			if (textIndex < 0) textIndex = 0;
		} else if (keyCode == RIGHT) {
			textIndex++;
			if (textIndex > typed.length()) textIndex = typed.length();
		}
		newKey = true;
	} else {
		if (keyCode == 8) {
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
			if (get(i,j) != color(255) && i>cursorX) {
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
		println(textIndex, posX);
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
	background(255);

	if (!started) {
		fill(0);
		text("H f", textX, textY);
		calculateCursorX();
		int withSpace = cursorX;

		background(255);

		text("Hf", textX, textY);
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
