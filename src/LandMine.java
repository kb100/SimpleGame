import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

public class LandMine extends SolidRectangle
{

    int timeUntilExplode;

    public LandMine(int x, int y, GameContent game)
    {
        super(x, y, 20, 10, Color.GRAY, game);
        timeUntilExplode = 20;
        dyMax = 18;
    }

    public void control()
    {
        ddy = 1;
        if (timeUntilExplode < 20) timeUntilExplode++;
        if (timeUntilExplode == 20) color = Color.GRAY;
        else color = Color.RED;
    }

    public void tick()
    {
        timeUntilExplode -= 5;
        if (timeUntilExplode <= 0) explode();
    }

    public void explode()
    {
        for (int i = x; i < x + width; i++)
        {
            for (int j = y; j < y + height; j++)
            {
                //for (int k = 0; k < 10; k++)
                {
                    ExplosionParticle particle = new ExplosionParticle(i, j, game);
                    game.addMovable(particle);
                }
            }
        }
        
       List<LocalPlayer> players = game.findPlayersInArea(new Rectangle(x-10,y-10,width+20,width+20));
       for(LocalPlayer player : players)
       {
           player.dx = 2*((player.x+player.width/2)-(x+width/2));
           player.dy = 2*((player.y+player.height/2)-(y+height/2));
           player.disable(50);
       }
        
        remove();
    }

}
