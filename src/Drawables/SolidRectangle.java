import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class SolidRectangle extends Movable
{
    Color color;

    public SolidRectangle(double x, double y, double width, double height, double dxMax, double dyMax, Color color, GameContent game)
    {
        super(x, y, width, height, dxMax, dyMax, game);
        this.color = color;
    }

    public SolidRectangle(double x, double y, double width, double height, Color color, GameContent game)
    {
        super(x, y, width, height, 0, 0, game);
        this.color = color;
    }

    public Rectangle2D.Double toRectangle()
    {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public void draw(Graphics g)
    {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
    }

    public void control()
    {
        // By default solid rectangles are fixed
    }

}
