import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class SolidRectangle extends Movable
{
    Color color;

    public SolidRectangle(int x, int y, int width, int height, int dxMax, int dyMax, Color color, GameContent game)
    {
        super(x, y, width, height, dxMax, dyMax, game);
        this.color = color;
    }

    public SolidRectangle(int x, int y, int width, int height, Color color, GameContent game)
    {
        super(x, y, width, height, 0, 0, game);
        this.color = color;
    }

    public Rectangle toRectangle()
    {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g)
    {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public void control()
    {
        // By default solid rectangles are fixed
    }

}
