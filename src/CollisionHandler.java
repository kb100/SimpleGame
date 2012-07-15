import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;

public class CollisionHandler
{

    public static void handleCollision(LocalPlayer player, SolidRectangle rectangle)
    {
        Area area = new Area(player.toRectangle());
        area.intersect(new Area(rectangle.toRectangle()));
        if (!area.isEmpty())
        {
            if (rectangle instanceof VanishingSolidRectangle)
            {
                rectangle.color = new Color(rectangle.color.getRed(), rectangle.color.getGreen(), rectangle.color.getBlue(), rectangle.color.getAlpha() / 4);
                if (rectangle.color.getAlpha() == 0)
                {
                    rectangle.remove();
                }

                if (rectangle instanceof Snowflake)
                {
                    rectangle.remove();
                    return;
                }
            }

            int sign;
            Rectangle intersection = area.getBounds();
            if (intersection.width <= intersection.height)
            {
                // do a horizontal shift
                sign = (intersection.getCenterX() - player.toRectangle().getCenterX()) < 0 ? 1 : -1;
                player.unconditionalShift((intersection.width) * sign, 0);
                player.dx = 0;
            }
            if (intersection.width >= intersection.height)
            {
                // do a vertical shift
                sign = (intersection.getCenterY() - player.toRectangle().getCenterY()) < 0 ? 1 : -1;
                player.unconditionalShift(0, (intersection.height) * sign);
                player.land();
            }

        }

    }

    public static void handleCollision(LocalPlayer player, LocalPlayer player2)
    {
        Area area = new Area(player.toRectangle());
        area.intersect(new Area(player2.toRectangle()));
        if (!area.isEmpty())
        {
            int sign;
            Rectangle intersection = area.getBounds();
            if (intersection.width <= intersection.height)
            {
                // do a horizontal shift
                double diff = (intersection.getCenterX() - player.toRectangle().getCenterX());
                if(diff != 0)
                {
                    sign = diff < 0 ? 1 : -1;
                    player.unconditionalShift(Math.max((intersection.width)/2,1) * sign, 0);
                    player2.unconditionalShift(Math.max((intersection.width)/2,1) * sign*-1, 0);
                }
                
            }
            if (intersection.width >= intersection.height)
            {
                // do a vertical shift
                sign = (intersection.getCenterY() - player.toRectangle().getCenterY()) < 0 ? 1 : -1;
                if (sign < 0)
                {
                    player.unconditionalShift(0, (intersection.height) * sign);
                    player.land();
                }
                sign  = (intersection.getCenterY() - player2.toRectangle().getCenterY()) < 0 ? 1 : -1;
                if(sign < 0)
                {
                    player2.unconditionalShift(0, (intersection.height) * sign);
                    player2.land();
                }
            }

        }

    }
}
