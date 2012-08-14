import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public abstract class Drawable implements Serializable
{
    double x, y;
    double width, height;
    GameContent game;

    public Drawable(double x, double y, double width, double height, GameContent game)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.game = game;
    }

    public void unconditionalShift(double dx, double dy)
    {
        x += dx;
        y += dy;
    }

    public Rectangle2D.Double getBoundingRectangle()
    {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public void remove()
    {
        game.remove(this);
    }

    public abstract void draw(Graphics g);

    public boolean isOnScreen()
    {
        return x + width >= 0 && x < GameContent.GAME_WIDTH && y + height >= 0 && y < GameContent.GAME_HEIGHT;
    }
}
