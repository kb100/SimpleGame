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
			if (intersection.width < intersection.height && intersection.width > 0)
			{
				//do a horizontal shift
				player.unconditionalShift((intersection.width+1) * (player.dx < 0 ? 1 : -1), 0);
			}
			else if (intersection.width > intersection.height && intersection.height > 0)
			{
				//do a vertical shift
				player.unconditionalShift(0, (intersection.height) * (player.dy < 0 ? 1 : -1));
			}
			player.land();
		}

	}
}
