import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

//Does not extend SolidRectangle on purpose
public class LocalPlayer extends Movable
{
    
    int width, height;
    boolean crouching;
    int crouchingHeightChange;
    int disabledTime;
    ExplosionParticleSource source;

    Color color;

    GameController controller;

    static
    {
        CollisionHandler.registerCollisions(LocalPlayer.class, LocalPlayer.class);
        CollisionHandler.registerCollisions(LocalPlayer.class, SolidRectangle.class);
        CollisionHandler.registerCollisions(LocalPlayer.class, VanishingSolidRectangle.class);
        CollisionHandler.registerCollisions(LocalPlayer.class, Snowflake.class);
        CollisionHandler.registerCollisions(LocalPlayer.class, LandMine.class);
    }
    
    public LocalPlayer(int x, int y, int width, int height, GameContent game)
    {
        super(x, y, 8, 18, game);
        this.width = width;
        this.height = height;
        
        source = new ExplosionParticleSource(x, y, game);

        this.color = Color.WHITE;

        this.crouching = false;
        crouchingHeightChange = (int) (height * .3);
        disabledTime = 0;

    }

    public void control()
    {
        ControllerState state = controller.getControllerState();
        if (state.exit)
        {
            controller.hasBeenHandled(controller.keyExit);
            game.panel.loadSavedState();
            // game.saveState();
            // System.exit(0);
        }

        if (state.select)
        {
            game.toggleReferenceFrame();
            controller.hasBeenHandled(controller.keySelect);

        }

        // Slow motion for debugging purposes
        if (state.start)
        {
            // game.panel.toggleTimerSpeed();
            controller.hasBeenHandled(controller.keyStart);
            game.panel.setSaveState();
        }

        if (disabledTime > 0) return;

        if (state.left) ddx = -1;
        else if (state.right) ddx = 1;
        else ddx = 0;

        if (state.down)
        {
            if (!crouching)
            {
                height -= crouchingHeightChange;
                y += crouchingHeightChange;
                crouching = true;
            }
        }
        else
        {
            if (crouching && roomToUncrouch())
            {
                y -= crouchingHeightChange;
                height += crouchingHeightChange;
                crouching = false;
            }
        }

        if (state.jump) jump();
    }

    public void update()
    {
        super.update();
        if (disabledTime > 0) disabledTime--;
        if(disabledTime == 0) color = Color.white;
        if (turning()) dx /= 2;
        
        source.x = x+width/2;
        source.y = y - 5;
        source.produceMovable();
    }

    public boolean turning()
    {
        return (dx * ddx <= 0) && (disabledTime == 0);
    }

    public boolean roomToUncrouch()
    {
        List<SolidRectangle> rectangles = game.findSolidRectanglesInArea(new Rectangle(x, y - crouchingHeightChange, width, height));
        for(SolidRectangle rectangle : rectangles)
        {
            if(CollisionHandler.isValidCollision(this, rectangle));
        }
        return true; 
    }

    public void jump()
    {
        dy = -10;
    }

    public void disable(int time)
    {
        disabledTime += time;
        ddx = 0;
        ddy = 0;
        color = Color.LIGHT_GRAY;
    }

    public void land()
    {
        dy = ddy = 0;
    }

    public void draw(Graphics g)
    {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public Rectangle toRectangle()
    {
        return new Rectangle(x, y, width, height);
    }

}
