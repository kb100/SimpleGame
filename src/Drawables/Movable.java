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
        long nanosSinceLastUpdate = FPSInfo.getNanosSinceLastUpdate();
        x += dx * nanosSinceLastUpdate;
        dx += ddx * nanosSinceLastUpdate;
        if(dx > dxMax)
            dx = dxMax;
        else if(dx < -dxMax)
            dx = -dxMax;

        y += dy * nanosSinceLastUpdate;
        dy += ddy * nanosSinceLastUpdate;
        if(dy > dyMax)
            dy = dyMax;
        else if(dy < -dyMax)
            dy = -dyMax;
    }

    public void stop()
    {
        dx = ddx = dy = ddy = 0;
    }

    public void slow(double xSlow, double ySlow)
    {
        slowX(xSlow);
        slowY(ySlow);
    }

    public void slowX(double xSlow)
    {
        if(dx > 0)
        {
            dx -= xSlow;
            dx = Math.max(dx, 0);
        }
        else
        {
            dx += xSlow;
            dx = Math.min(dx, 0);
        }
    }

    public void slowY(double ySlow)
    {
        if(dy > 0)
        {
            dy -= ySlow;
            dy = Math.max(dy, 0);
        }
        else
        {
            dy += ySlow;
            dy = Math.min(dy, 0);
        }
    }

}
