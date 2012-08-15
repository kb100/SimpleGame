import java.awt.Color;

public class ExplosionParticle extends SolidRectangle
{
    int age;
    
    static
    {
        CollisionHandler.registerCollisions(ExplosionParticle.class, SolidRectangle.class);
    }

    public ExplosionParticle(double x, double y, GameContent game)
    {
        super(x, y, randomSize(), randomSize(), randomFireColor(), game);
        this.dxMax = GameContent.TERMINAL_VELOCITY_PPN;
        this.dyMax = GameContent.TERMINAL_VELOCITY_PPN;
        ddy = GameContent.GRAVITY_PPNN;
        ddx = 0;
        age = 0;
        double theta = Math.random() * (2*Math.PI);
        dy = (dyMax * Math.sin(theta));
        dx = (dxMax * Math.cos(theta));
    }

    public void control()
    {
//        dx += GameContent.rand.nextInt(3) - 1;
//        dy += GameContent.rand.nextInt(4) - 2;
        if(GameContent.rand.nextInt(25) == 0)
            decay();

    }

    public void decay()
    {
        age++;
        if(age == 10)
        {
            remove();
        }
    }

    public static int randomSize()
    {
        return 1 + GameContent.rand.nextInt(5);
    }

    public static Color randomFireColor()
    {
        int n = GameContent.rand.nextInt(56);
        return new Color(255, n, n);
    }

}
