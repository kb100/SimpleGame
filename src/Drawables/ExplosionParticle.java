import java.awt.Color;

public class ExplosionParticle extends SolidRectangle
{
    int age;

    public ExplosionParticle( int x, int y, GameContent game )
    {
        super( x, y, randomSize(), randomSize(), randomFireColor(), game );
        this.dxMax = 50;
        this.dyMax = 50;
        ddy = 1;
        ddx = 0;
        age = 0;
        double theta = Math.random() * Math.PI;
        dy = (int) (-20 * Math.sin( theta ));
        dx = (int) (20 * Math.cos( theta ));
    }

    public void control()
    {
        dx += GameContent.rand.nextInt( 3 ) - 1;
        dy += GameContent.rand.nextInt( 4 ) - 2;
        if( GameContent.rand.nextInt( 25 ) == 0 )
            decay();

    }

    public void decay()
    {
        age++;
        if( age == 10 )
        {
            remove();
        }
    }

    public static int randomSize()
    {
        return 1 + GameContent.rand.nextInt( 5 );
    }

    public static Color randomFireColor()
    {
        int n = GameContent.rand.nextInt( 56 );
        return new Color( 255, n, n );
    }

}
