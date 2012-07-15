import java.awt.Rectangle;

public class CollisionDetector
{
    public static boolean areColliding(LocalPlayer player, SolidRectangle rectangle)
    {
        return rectangle.toRectangle().intersects(player.toRectangle());
    }
    
    public static boolean areColliding(Rectangle rectangle, SolidRectangle solidRectangle)
    {
        return rectangle.intersects(solidRectangle.toRectangle());
    }
    
    public static boolean areColliding(LocalPlayer player, LocalPlayer player2)
    {
        return player.toRectangle().intersects(player2.toRectangle());
    }
}
