import java.awt.Color;
import java.awt.Graphics;

public class Snowflake extends SolidRectangle
{
    int age;

    public Snowflake( int x, int y, GameContent game )
    {
        super( x, y, randomSnowflakeSize(), randomSnowflakeSize(), randomSnowflakeColor(), game );
        this.dxMax = 5;
        this.dyMax = 5;
        ddy = 1;
        ddx = 0;
        age = 0;
    }

    public void control()
    {
        dx += GameContent.rand.nextInt( 3 ) - 1;
        dy += GameContent.rand.nextInt( 4 ) - 2;
        if( GameContent.rand.nextInt( 50 ) == 0 )
            decay();

    }

    public void decay()
    {
        age++;
        color = new Color( color.getRed() - 5, color.getGreen() - 5, color.getBlue() - 5 );
        if( age == 5 )
        {
            remove();
        }
    }

    public static int randomSnowflakeSize()
    {
        return 1 + GameContent.rand.nextInt( 3 );
    }

    public static Color randomSnowflakeColor()
    {
        int n = GameContent.rand.nextInt( 56 ) + 200;
        return new Color( n, n, n );
    }

}
