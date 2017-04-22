import processing.core.PApplet;
import com.essential.essentialgui.*;

public class Main extends PApplet {
	private Textarea textarea;

	public static void main (String[] args) {
		PApplet.main("Main");
	}

	public void settings() {
		size(600, 400);
	}

	public void setup() {
		background(255);
		textarea = new Textarea(this, 100, 100, 400, 200);
	}

	public void draw() {
		background(0, 255, 255);
		textarea.updateAndDisplay();
	}

	public void keyPressed() {
		textarea.keyUpdate(key);
	}
}
