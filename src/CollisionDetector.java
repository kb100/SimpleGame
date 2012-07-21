import java.awt.Rectangle;

public class CollisionDetector
{
    public static boolean areColliding(LocalPlayer player, SolidRectangle rectangle)
    {
        return (player.x < rectangle.x+rectangle.width && player.x+player.width > rectangle.x &&
                player.y < rectangle.y+rectangle.height && player.y+player.height > rectangle.y);
    }
    
    public static boolean areColliding(Rectangle rectangle, SolidRectangle solid)
    {
        return (solid.x < solid.x+solid.width && solid.x+solid.width > solid.x &&
                solid.y < solid.y+solid.height && solid.y+solid.height > solid.y);
    }
    
    public static boolean areColliding(SolidRectangle rectangle1, SolidRectangle rectangle2)
    {
        return (rectangle1.x < rectangle2.x+rectangle2.width && rectangle1.x+rectangle1.width > rectangle2.x &&
                rectangle1.y < rectangle2.y+rectangle2.height && rectangle1.y+rectangle1.height > rectangle2.y);
    }
    
    public static boolean areColliding(LocalPlayer player, LocalPlayer player2)
    {
        return (player.x < player2.x+player2.width && player.x+player.width > player2.x &&
                player.y < player2.y+player2.height && player.y+player.height > player2.y);
    }
}
