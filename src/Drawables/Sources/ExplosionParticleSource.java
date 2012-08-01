public class ExplosionParticleSource extends Source<ExplosionParticle>
{

    public ExplosionParticleSource(int x, int y, GameContent game)
    {
        super(x, y, game);
    }

    public void produceMovable()
    {
        game.addMovable(new ExplosionParticle(x, y, game));
    }

}
