import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class SolidRectangle extends Drawable
{
	int width;
	int height;
	Color color;
	
	public SolidRectangle(int x, int y, int width, int height, Color color)
	{
		super(x,y);
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public Rectangle toRectangle()
	{
		return new Rectangle(x,y,width,height);
	}
	
	public void draw(Graphics g)
	{
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

}
