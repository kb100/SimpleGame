import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Player extends Drawable
{
	int dx, ddx;
	int dy, ddy;

	int dxMax;
	int dyMax;

	int width, height;

	Color color;
	
	public Player(int x, int y, int width, int height)
	{
		super(x,y);
		this.width = width;
		this.height = height;

		dxMax = 10;
		dyMax = 20;
		
		color = Color.WHITE;
		
		stop();
	}

	public void update()
	{
		x += dx;
		dx += ddx;
		if (dx > dxMax) dx = dxMax;
		else if (dx < -dxMax) dx = -dxMax;

		if (turning()) dx /= 2;

		y += dy;
		dy += ddy;
		if (dy > dyMax) dy = dyMax;
		else if (dy < -dyMax) dy = -dyMax;
	}

	public void unconditionalShift(int dx, int dy)
	{
		x += dx;
		y += dy;
	}

	public boolean turning()
	{
		return dx * ddx <= 0;
	}

	public void fall()
	{
		ddy = 3;
	}

	public void jump()
	{
		dy = -7;
	}

	public void land()
	{
		dy = ddy = 0;
	}

	public void stop()
	{
		dx = ddx = dy = ddy = 0;
	}

	public void draw(Graphics g)
	{
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	public Rectangle toRectangle()
	{
		return new Rectangle(x, y, width, height);
	}
}
