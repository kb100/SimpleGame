import java.awt.Graphics;

public abstract class Source<T extends Movable> extends Movable
{

    public Source(int x, int y, GameContent game)
    {
        super(x, y, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE, game);
    }

    public void draw(Graphics g)
    {
        // Invisible
    }

    public abstract void produceMovable();

    public void control()
    {
        produceMovable();
    }
}