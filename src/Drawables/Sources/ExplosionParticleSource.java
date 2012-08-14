public class ExplosionParticleSource extends Source<ExplosionParticle>
{

    public ExplosionParticleSource(double x, double y, GameContent game)
    {
        super(x, y, game);
    }

    public void produceMovable()
    {
        game.addMovable(new ExplosionParticle(x, y, game));
    }

}
