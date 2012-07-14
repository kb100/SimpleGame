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
			Rectangle intersection = area.getBounds();
			if (intersection.width < intersection.height)
			{
				// do a horizontal shift
				int sign = (intersection.getCenterX()-player.toRectangle().getCenterX()) < 0? 1:-1;
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
