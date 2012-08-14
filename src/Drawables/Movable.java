public abstract class Movable extends Drawable
{

    double dx, ddx;
    double dy, ddy;

    double dxMax;
    double dyMax;

    public Movable(double x, double y, double width, double height, double dxMax, double dyMax, GameContent game)
    {
        super(x, y, width, height, game);
        this.dxMax = dxMax;
        this.dyMax = dyMax;
        stop();
    }

    public abstract void control();

    public final void update()
    {
        x += dx;
        dx += ddx;
        if(dx > dxMax)
            dx = dxMax;
        else if(dx < -dxMax)
            dx = -dxMax;

        y += dy;
        dy += ddy;
        if(dy > dyMax)
            dy = dyMax;
        else if(dy < -dyMax)
            dy = -dyMax;
    }

    public void stop()
    {
        dx = ddx = dy = ddy = 0;
    }

}
