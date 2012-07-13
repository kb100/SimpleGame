import java.awt.Graphics;

public abstract class Drawable
{
	int x, y;

	public Drawable(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void unconditionalShift(int dx, int dy)
	{
		x += dx;
		y += dy;
	}

	public abstract void draw(Graphics g);
}
