
public class SnowflakeSource extends Source<Snowflake>
{

    public SnowflakeSource(int x, int y, GameContent game)
    {
        super(x, y, game);
    }

    public void produceMovable()
    {
        game.addMovable(new Snowflake(x, y, game));
    }

}
