import java.awt.Color;
import java.util.ArrayDeque;

public class Snowflake extends SolidRectangle
{
    private int age;
    private static final int MAX_AGE = 20;
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

    private Snowflake(double x, double y, GameContent game)
    {
        super(x, y, randomSnowflakeSize(), randomSnowflakeSize(), randomSnowflakeColor(), game);
        dyMax = GameContent.TERMINAL_VELOCITY_PPN * .35d;
        dxMax = GameContent.TERMINAL_VELOCITY_PPN * .2d;
        ddy = GameContent.GRAVITY_PPNN;
        ddx = 0;
        age = 0;
        dx = GameContent.TERMINAL_VELOCITY_PPN / 10 * (GameContent.rand.nextDouble() - .5);
        dy = GameContent.TERMINAL_VELOCITY_PPN * (GameContent.rand.nextDouble() - .1);
    }

    public void control()
    {
        if(dy > (dyMax * .75d) || dy < (-dyMax * .75d))
        {
            slowX((GameContent.rand.nextDouble() - .5d) * GameContent.TERMINAL_VELOCITY_PPN / 20d);
            slowY((GameContent.rand.nextDouble() - .5d) * GameContent.TERMINAL_VELOCITY_PPN / 20d);
            dx += (GameContent.rand.nextDouble() - .5d) * GameContent.TERMINAL_VELOCITY_PPN / 20d;
            dy += (GameContent.rand.nextDouble() - .5d) * GameContent.TERMINAL_VELOCITY_PPN / 15d;
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

        public static Snowflake checkoutSnowflake(double x, double y, GameContent game)
        {
            if(available.isEmpty())
                makeMoreSnowflakes();

            Snowflake snowflake = available.pollLast();
            snowflake.x = x;
            snowflake.y = y;
            snowflake.age = 0;
            snowflake.game = game;
            snowflake.dx = GameContent.TERMINAL_VELOCITY_PPN / 2d * (GameContent.rand.nextDouble() - .5d);
            snowflake.dy = GameContent.TERMINAL_VELOCITY_PPN * (GameContent.rand.nextDouble() - .5d);

            return snowflake;
        }

        private static void makeMoreSnowflakes()
        {
            for(int i = 0; i < 1024; ++i)
                available.addLast(new Snowflake(0, 0, null));
        }

        public static void returnSnowflake(Snowflake snowflake)
        {
            available.addLast(snowflake);
        }
    }
}
