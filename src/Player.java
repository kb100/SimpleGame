import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

//Does not extend SolidRectangle on purpose
public class Player extends Movable
{
    

    int width, height;

    Color color;

    GameController controller;

    public Player(int x, int y, int width, int height, GameController controller, GameContent game)
    {
        super(x, y, 8 , 18, game);
        this.width = width;
        this.height = height;

        color = Color.WHITE;

        this.controller = controller;

        stop();
    }

    public void control()
    {
        if (controller.left) ddx = -1;
        else if (controller.right) ddx = 1;
        else ddx = 0;

        if (controller.jump) jump();
    }

    public void update()
    {
        super.update();
        if (turning()) dx /= 2;
    }

    public boolean turning()
    {
        return dx * ddx <= 0;
    }

    public void jump()
    {
        dy = -10;
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
