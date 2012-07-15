public abstract class Movable extends Drawable
{
    
    int dx, ddx;
    int dy, ddy;

    int dxMax;
    int dyMax;

    public Movable(int x, int y, int dxMax, int dyMax, GameContent game)
    {
        super(x, y, game);
        this.dxMax = dxMax;
        this.dyMax = dyMax;
        stop();
    }
    
    public abstract void control();
    
    public void update()
    {
        x += dx;
        dx += ddx;
        if (dx > dxMax) dx = dxMax;
        else if (dx < -dxMax) dx = -dxMax;

        y += dy;
        dy += ddy;
        if (dy > dyMax) dy = dyMax;
        else if (dy < -dyMax) dy = -dyMax;
    }

    public void stop()
    {
        dx = ddx = dy = ddy = 0;
    }
    
}
