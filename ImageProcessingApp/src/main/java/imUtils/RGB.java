package imUtils;

public class RGB {

	public int r, g, b;
	final int THRESHOLD = 5;
	
	public RGB(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public boolean isShaded() {
		return r < THRESHOLD && g < THRESHOLD && b < THRESHOLD;
	}
}
