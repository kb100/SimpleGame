import java.awt.Color;
import java.util.ArrayDeque;

public class Snowflake extends SolidRectangle
{
    private int age;
    private static final int MAX_AGE=5;
    private static int randIndex = 0;
    private static int[] randomInts = new int[2048];
    static
    {
        for(int i = 0; i < randomInts.length; ++i)
            randomInts[i] = (int)(Math.abs(GameContent.rand.nextInt()));

        CollisionHandler.registerCollisions(Snowflake.class, Snowflake.class);
        CollisionHandler.registerCollisions(Snowflake.class, SolidRectangle.class);
    }

    private static Color[] colors = { Color.WHITE, new Color(250, 250, 250), new Color(240, 240, 255), new Color(220, 220, 220), new Color(230, 230, 250) };

    private Snowflake(int x, int y, GameContent game)
    {
        super(x, y, randomSnowflakeSize(), randomSnowflakeSize(), randomSnowflakeColor(), game);
        this.dxMax = 5;
        this.dyMax = 5;
        ddy = 1;
        ddx = 0;
        age = 0;
        dx += (randInt(7) - 4);
        dy += (randInt(8) - 4);
    }

    public void control()
    {
        if(dy > 3 || dy < -3)
        {
            dx += randInt(3) - 1;
            dy += randInt(4) - 2;
        }
        if(randInt(32) == 0)
            decay();

    }

    public void decay()
    {
        age++;
        color = randomSnowflakeColor();
        if(age == MAX_AGE)
        {
            this.remove();
        }
    }

    public void remove()
    {
        super.remove();
        Snowflake.Mempool.returnSnowflake(this);
    }

    private static int randomSnowflakeSize()
    {
        return 1 + randInt(3);
    }

    public static Color randomSnowflakeColor()
    {
        return colors[randInt(colors.length)];
    }

    private static int randInt(int max)
    {
        int rand = randomInts[randIndex++] % max;
        randIndex %= randomInts.length;
        return rand;
    }

    public static class Mempool
    {
        private static ArrayDeque<Snowflake> available = new ArrayDeque<Snowflake>(1024);
        private static int snowflakeCount = 1024;

        public static Snowflake checkoutSnowflake(int x, int y, GameContent game)
        {
            if(available.isEmpty())
                doublePoolSize();

            Snowflake snowflake = available.pollLast();
            snowflake.x = x;
            snowflake.y = y;
            snowflake.age = 0;
            snowflake.game = game;
            snowflake.dx = (randInt(7) - 4);
            snowflake.dy = (randInt(8) - 4);

            return snowflake;
        }

        private static void doublePoolSize()
        {
            for(int i = 0; i < snowflakeCount; ++i)
                available.addLast(new Snowflake(0, 0, null));

            snowflakeCount *= 2;
        }

        public static void returnSnowflake(Snowflake snowflake)
        {
            available.addLast(snowflake);
        }
    }
}
