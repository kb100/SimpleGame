import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;

public abstract class Drawable implements Serializable
{
    int x, y;
    int width, height;
    GameContent game;

    public Drawable( int x, int y, int width, int height, GameContent game )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.game = game;
    }

    public void unconditionalShift( int dx, int dy )
    {
        x += dx;
        y += dy;
    }

    public Rectangle getBoundingRectangle()
    {
        return new Rectangle( x, y, width, height );
    }

    public void remove()
    {
        game.remove( this );
    }

    public abstract void draw( Graphics g );

    public boolean isOnScreen()
    {
        return x + width >= 0 && x < GameContent.GAME_WIDTH && y + height >= 0 && y < GameContent.GAME_HEIGHT;
    }
}
