import java.awt.Graphics;

public abstract class Source<T extends Movable> extends Movable
{

    public Source(double x, double y, GameContent game)
    {
        super(x, y, 0, 0, Double.MAX_VALUE, Double.MAX_VALUE, game);
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