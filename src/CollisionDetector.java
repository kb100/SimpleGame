import java.awt.Rectangle;

public class CollisionDetector
{
    public static boolean areColliding(Player player, SolidRectangle rectangle)
    {
        return rectangle.toRectangle().intersects(player.toRectangle());
    }
    
    public static boolean areColliding(Rectangle rectangle, SolidRectangle solidRectangle)
    {
        return rectangle.intersects(solidRectangle.toRectangle());
    }
}
