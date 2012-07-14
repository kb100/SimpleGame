import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;

public class CollisionHandler
{

    public static void handleCollision(Player player, SolidRectangle rectangle)
    {
        Area area = new Area(player.toRectangle());
        area.intersect(new Area(rectangle.toRectangle()));
        if (!area.isEmpty())
        {
            rectangle.color = new Color(rectangle.color.getRed(), rectangle.color.getGreen(), rectangle.color.getBlue(), rectangle.color.getAlpha() / 2);
            if (rectangle.color.getAlpha() == 0)
            {
                rectangle.remove();
            }
            Rectangle intersection = area.getBounds();
            if (intersection.width < intersection.height)
            {
                // do a horizontal shift
                int sign = (intersection.getCenterX() - player.toRectangle().getCenterX()) < 0 ? 1 : -1;
                player.unconditionalShift((intersection.width) * sign, 0);
                player.dx = 0;
            }
            else if (intersection.width > intersection.height)
            {
                // do a vertical shift
                player.unconditionalShift(0, (intersection.height) * (player.dy < 0 ? 1 : -1));
                player.land();
            }

        }

    }
}
