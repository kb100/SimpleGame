import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.HashSet;

public class CollisionHandler
{
    static HashMap< Class< ? >, HashSet< Class< ? >>> intersections = new HashMap< Class< ? >, HashSet< Class< ? >>>();

    public static void registerCollisions( Class< ? > clazz, Class< ? > clazz2 )
    {
        HashSet< Class< ? >> bucket = intersections.get( clazz );
        if( bucket == null )
        {
            bucket = new HashSet< Class< ? >>();
            intersections.put( clazz, bucket );
        }
        bucket.add( clazz2 );

        bucket = intersections.get( clazz2 );
        if( bucket == null )
        {
            bucket = new HashSet< Class< ? >>();
            intersections.put( clazz2, bucket );
        }
        bucket.add( clazz );
    }

    public static boolean isValidCollision( Object object, Object object2 )
    {
        if( object == object2 )
            return false;

        HashSet< Class< ? >> bucket = intersections.get( object.getClass() );
        if( bucket == null || !bucket.contains( object2.getClass() ) )
            return false;

        return true;
    }

    public static void handleCollision( Drawable drawable1, Drawable drawable2 )
    {
        if( !isValidCollision( drawable1, drawable2 ) )
            return;

        Class< ? > clazz1 = drawable1.getClass();
        Class< ? > clazz2 = drawable2.getClass();

        if( clazz1 == LocalPlayer.class && drawable2 instanceof SolidRectangle )
        {
            handleCollision( (LocalPlayer) drawable1, (SolidRectangle) drawable2 );
        }
        else if( drawable1 instanceof SolidRectangle && clazz2 == LocalPlayer.class )
        {
            handleCollision( (LocalPlayer) drawable2, (SolidRectangle) drawable1 );
        }
        else if( clazz1 == LandMine.class && drawable2 instanceof SolidRectangle )
        {
            handleCollision( (LandMine) drawable1, (SolidRectangle) drawable2 );
        }
        else if( drawable1 instanceof SolidRectangle && clazz2 == LandMine.class )
        {
            handleCollision( (LandMine) drawable2, (SolidRectangle) drawable1 );
        }
        else if( clazz1 == LocalPlayer.class && clazz2 == LocalPlayer.class )
        {
            handleCollision( (LocalPlayer) drawable1, (LocalPlayer) drawable2 );
        }
        else
        {
            throw new IllegalArgumentException( "Unhandled class collision pair: (" + clazz1 + ", " + clazz2 + ")" );
        }
    }

    public static void handleCollision( LandMine mine, SolidRectangle rectangle2 )
    {
        Area area = new Area( mine.toRectangle() );
        area.intersect( new Area( rectangle2.toRectangle() ) );
        if( !area.isEmpty() )
        {
            int sign;
            Rectangle intersection = area.getBounds();
            if( intersection.width <= intersection.height )
            {
                // do a horizontal shift
                sign = (intersection.getCenterX() - mine.toRectangle().getCenterX()) < 0 ? 1 : -1;
                mine.unconditionalShift( (intersection.width) * sign, 0 );
                mine.dx = 0;
            }
            if( intersection.width >= intersection.height )
            {
                // do a vertical shift
                sign = (intersection.getCenterY() - mine.toRectangle().getCenterY()) < 0 ? 1 : -1;
                mine.unconditionalShift( 0, (intersection.height) * sign );
                mine.stop();
            }

        }
    }

    public static void handleCollision( LocalPlayer player, SolidRectangle rectangle )
    {
        Area area = new Area( player.toRectangle() );
        area.intersect( new Area( rectangle.toRectangle() ) );
        if( !area.isEmpty() )
        {
            if( rectangle instanceof VanishingSolidRectangle )
            {
                rectangle.color = new Color( rectangle.color.getRed(), rectangle.color.getGreen(), rectangle.color.getBlue(), rectangle.color.getAlpha() / 4 );
                if( rectangle.color.getAlpha() == 0 )
                {
                    rectangle.remove();
                    return;
                }
            }
            else if( rectangle instanceof Snowflake || rectangle instanceof ExplosionParticle )
            {
                rectangle.remove();
                return;
            }

            int sign;
            Rectangle intersection = area.getBounds();
            if( intersection.width <= intersection.height )
            {
                // do a horizontal shift
                sign = (intersection.getCenterX() - player.toRectangle().getCenterX()) < 0 ? 1 : -1;
                player.unconditionalShift( (intersection.width) * sign, 0 );
                player.dx = 0;
                // if(player.dy>0) player.dy--;
                // else if(player.dy < 0) player.dy++;
            }
            if( intersection.width >= intersection.height )
            {
                // do a vertical shift
                sign = (intersection.getCenterY() - player.toRectangle().getCenterY()) < 0 ? 1 : -1;
                player.unconditionalShift( 0, (intersection.height) * sign );
                player.land();
            }

            if( rectangle instanceof LandMine )
            {
                ((LandMine) rectangle).tick();
            }

        }

    }

    public static void handleCollision( LocalPlayer player, LocalPlayer player2 )
    {
        Area area = new Area( player.toRectangle() );
        area.intersect( new Area( player2.toRectangle() ) );
        if( !area.isEmpty() )
        {
            int sign;
            Rectangle intersection = area.getBounds();
            if( intersection.width <= intersection.height )
            {
                // do a horizontal shift
                double diff = (intersection.getCenterX() - player.toRectangle().getCenterX());
                if( diff != 0 )
                {
                    sign = diff < 0 ? 1 : -1;
                    player.unconditionalShift( Math.max( (intersection.width) / 2, 1 ) * sign, 0 );
                    player2.unconditionalShift( Math.max( (intersection.width) / 2, 1 ) * sign * -1, 0 );
                }

            }
            if( intersection.width >= intersection.height )
            {
                // do a vertical shift
                sign = (intersection.getCenterY() - player.toRectangle().getCenterY()) < 0 ? 1 : -1;
                if( sign < 0 )
                {
                    player.unconditionalShift( 0, (intersection.height) * sign );
                    player.land();
                }
                sign = (intersection.getCenterY() - player2.toRectangle().getCenterY()) < 0 ? 1 : -1;
                if( sign < 0 )
                {
                    player2.unconditionalShift( 0, (intersection.height) * sign );
                    player2.land();
                }
            }

        }

    }
}
