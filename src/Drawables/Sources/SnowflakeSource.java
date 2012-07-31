public class SnowflakeSource extends Source< Snowflake >
{

    public SnowflakeSource( int x, int y, GameContent game )
    {
        super( x, y, game );
    }

    public void produceMovable()
    {
        for(int i = 0; i < 100; ++i)
        game.addMovable( new Snowflake( x, y, game ) );
    }

}
