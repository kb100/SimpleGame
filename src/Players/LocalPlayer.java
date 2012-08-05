import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

//Does not extend SolidRectangle on purpose
public class LocalPlayer extends Movable
{

    boolean crouching;
    int crouchingHeightChange;
    int disabledTime;

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
        super(x, y, width, height, 8, 18, game);

        this.color = Color.WHITE;

        this.crouching = false;
        crouchingHeightChange = (int)(height * .3);
        disabledTime = 0;

    }

    public void control()
    {
        ControllerState state = controller.getControllerState();
        ddy = 1;
        if(disabledTime == 0)
        {
            if(state.left)
                ddx = -1;
            else if(state.right)
                ddx = 1;
            else
                ddx = 0;

            if(state.down)
            {
                if(!crouching)
                {
                    height -= crouchingHeightChange;
                    y += crouchingHeightChange;
                    crouching = true;
                }
            }
            else
            {
                if(crouching && roomToUncrouch())
                {
                    y -= crouchingHeightChange;
                    height += crouchingHeightChange;
                    crouching = false;
                }
            }

            if(state.jump)
                jump();

        }

        if(disabledTime > 0)
            disabledTime--;
        if(disabledTime == 0)
            color = Color.white;
        if(turning())
            dx /= 2;
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
            if(CollisionHandler.isValidCollision(this, rectangle) && !(rectangle instanceof Snowflake))
                return false;
        }
        return true;
    }

    public void jump()
    {
        dy = -10;
    }

    public void slow(int xSlow, int ySlow)
    {
        if(dx > 0)
            dx -= xSlow;
        else
            dx += xSlow;

        if(dy > 0)
            dy -= ySlow;
        else
            dy += ySlow;
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
        if(disabledTime != 0)
            dx /= 2;
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
