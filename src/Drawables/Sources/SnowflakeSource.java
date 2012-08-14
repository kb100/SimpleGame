public class SnowflakeSource extends Source<Snowflake>
{

    public SnowflakeSource(double x, double y, GameContent game)
    {
        super(x, y, game);
    }

    public void produceMovable()
    {
        game.addMovable(Snowflake.Mempool.checkoutSnowflake(x, y, game));
    }

}
