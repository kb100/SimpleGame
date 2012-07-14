import java.awt.Graphics;


public abstract class Source<T extends Movable> extends Drawable
{

    public Source(int x, int y, GameContent game)
    {
        super(x, y, game);
    }



    public void draw(Graphics g)
    {
        // Invisible
    }

    public abstract void produceMovable();
}