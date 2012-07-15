import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class SolidRectangle extends Movable
{
    int width;
    int height;
    Color color;

    public SolidRectangle(int x, int y, int width, int height, int dxMax, int dyMax, Color color, GameContent game)
    {
        super(x, y, dxMax, dyMax, game);
        this.width = width;
        this.height = height;
        this.color = color;
    }
    
    public SolidRectangle(int x, int y, int width, int height, Color color, GameContent game)
    {
        super(x, y, 0, 0, game);
        this.width = width;
        this.height = height;
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

    public boolean isOnScreen()
    {
        return x+width >= 0 && x < GameContent.GAME_WIDTH && y+height >= 0 && y < GameContent.GAME_HEIGHT;
    }

    public void control()
    {
        //By default solid rectangles are fixed
    }
  
}
