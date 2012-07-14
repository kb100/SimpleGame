public class CollisionDetector
{
    public static boolean areColliding(Player player, SolidRectangle rectangle)
    {
        return rectangle.toRectangle().intersects(player.toRectangle());
    }
}
