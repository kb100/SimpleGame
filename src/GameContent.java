import gnu.trove.set.hash.THashSet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

public class GameContent implements Serializable, Runnable
{

    static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Dimension dim = toolkit.getScreenSize();
    static final double GAME_WIDTH = dim.width - 10;
    static final double GAME_HEIGHT = dim.height - 320;

    static final double PLAYER_HEIGHT = 50;
    static final double PLAYER_WIDTH = 25;
    static final int NUM_PLATFORMS = 500;

    static final double GRAVITY_PPSS = 1400d;
    static final double GRAVITY_PPNN = GRAVITY_PPSS * FPSInfo.SECONDS_PER_NANOSECOND * FPSInfo.SECONDS_PER_NANOSECOND;
    static final double TERMINAL_VELOCITY_PPS = 600d;
    static final double TERMINAL_VELOCITY_PPN = TERMINAL_VELOCITY_PPS * FPSInfo.SECONDS_PER_NANOSECOND;
    static final double FRICTION_PPN = 500_000_000d * FPSInfo.SECONDS_PER_NANOSECOND;

    static Random rand = new Random();
    static
    {
        rand.setSeed(1 << 5);
    }

    public static Color randomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    transient GamePanel panel;
    transient GameContent savedState;

    LocalPlayer player;
    LocalPlayer player2;

    Drawable frameReference;

    // TODO: Keep sorted by order should be painted back to front
    ArrayDeque<LocalPlayer> localPlayers;
    LinkedHashSet<Drawable> drawables;
    THashSet<Movable> movables;
    ArrayDeque<Drawable> addDrawableQueue;
    ArrayDeque<Movable> addMovableQueue;
    ArrayDeque<Drawable> removeQueue;

    QuadTree tree = new QuadTree();

    public GameContent(GamePanel panel)
    {
        this.panel = panel;
        savedState = null;

        localPlayers = new ArrayDeque<LocalPlayer>();
        drawables = new LinkedHashSet<Drawable>();
        movables = new THashSet<Movable>();
        addDrawableQueue = new ArrayDeque<Drawable>();
        addMovableQueue = new ArrayDeque<Movable>();
        removeQueue = new ArrayDeque<Drawable>();

        for(double i = 3 * GAME_WIDTH / 16; i <= 9 * GAME_WIDTH / 16; i += 16)
            addMovable(new SnowflakeSource(i, 0, this));

        SolidRectangle floor = new SolidRectangle(20, GAME_HEIGHT - 40, GAME_WIDTH - 40, 20, Color.GRAY, this);
        addDrawable(floor);

        SolidRectangle wall = new SolidRectangle(GAME_HEIGHT - 40, 20, 20, GAME_HEIGHT - 40, Color.gray, this);
        addDrawable(wall);

        for(int i = 0; i < NUM_PLATFORMS; i++)
        {
            addDrawable(new SolidRectangle(rand.nextInt(5000) - 2500, rand.nextInt(5000) - 2500, 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
            addDrawable(new VanishingSolidRectangle(rand.nextInt(5000) - 2500, rand.nextInt(5000) - 2500, 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
        }

        LandMine mine = new LandMine(700, 50, this);
        addMovable(mine);

        player = new LocalPlayer((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        addMovable(player);
        localPlayers.add(player);
        frameReference = player;

        player2 = new LocalPlayer((GAME_WIDTH - PLAYER_WIDTH) / 2 + 40, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, this);
        addMovable(player2);
        localPlayers.add(player2);

    }

    public void run()
    {
        addOrRemoveQueuedElements();
        // System.out.println("ddyInPixelsPerSecond: " + player.ddy * FPSInfo.NANOSECONDS_PER_SECOND);

        for(Movable movable : movables)
        {
            movable.control();
            movable.update();
        }

        tree = new QuadTree(tree);
        for(Drawable drawable : drawables)
        {
            tree.add(drawable);
        }

        int size;
        Drawable drawable1, drawable2;
        for(ArrayList<Drawable> possibleCollisions : tree.leaves)
        {
            size = possibleCollisions.size();
            for(int i = 0; i < size; ++i)
            {
                for(int j = i + 1; j < size; ++j)
                {
                    drawable1 = possibleCollisions.get(i);
                    drawable2 = possibleCollisions.get(j);

                    if(CollisionDetector.areColliding(drawable1, drawable2))
                    {
                        CollisionHandler.handleCollision(drawable1, drawable2);
                    }
                }
            }
        }

        adjustFrameIfNecessary();
    }

    public void addOrRemoveQueuedElements()
    {
        Drawable currentDrawable;
        while((currentDrawable = removeQueue.pollLast()) != null)
        {
            drawables.remove(currentDrawable);
            movables.remove(currentDrawable);
        }

        while((currentDrawable = addDrawableQueue.pollLast()) != null)
        {
            drawables.add(currentDrawable);
        }

        Movable currentMovable;
        while((currentMovable = addMovableQueue.pollLast()) != null)
        {
            drawables.add(currentMovable);
            movables.add(currentMovable);
        }
    }

    public void adjustFrameIfNecessary()
    {
        final Drawable reference = frameReference; // switching frame reference is user-driven
        final double dx = calculateShift(GAME_WIDTH, reference.x);
        final double dy = calculateShift(GAME_HEIGHT, reference.y);

        if(dx != 0 || dy != 0)
        {
            for(Drawable shiftMe : drawables)
                shiftMe.unconditionalShift(dx, dy);
        }
    }

    public void toggleReferenceFrame()
    {
        if(frameReference == player)
            frameReference = player2;
        else
            frameReference = player;
    }

    List<SolidRectangle> findSolidRectanglesInArea(Rectangle2D.Double rectangle)
    {
        List<SolidRectangle> rectangles = new ArrayList<SolidRectangle>();
        for(Drawable drawable : drawables)
        {
            if(drawable instanceof SolidRectangle)
            {
                if(rectangle.intersects(((SolidRectangle)drawable).toRectangle()))
                    rectangles.add((SolidRectangle)drawable);
            }
        }
        return rectangles;
    }

    List<LocalPlayer> findPlayersInArea(Rectangle2D.Double rectangle)
    {
        ArrayList<LocalPlayer> inArea = new ArrayList<LocalPlayer>();
        for(LocalPlayer player : localPlayers)
        {
            if(rectangle.intersects(player.toRectangle()))
                inArea.add(player);
        }
        return inArea;
    }

    private static double calculateShift(double gameDimension, double referenceDimension)
    {
        double shift;
        return ((shift = (gameDimension - gameDimension / 3) - referenceDimension) < 0 || (shift = gameDimension / 3 - referenceDimension) > 0) ? shift : 0;
    }

    public synchronized void remove(Drawable drawable)
    {
        removeQueue.add(drawable);
    }

    public synchronized void addDrawable(Drawable drawable)
    {
        addDrawableQueue.add(drawable);
    }

    public synchronized void addMovable(Movable movable)
    {
        addMovableQueue.add(movable);
    }

    public synchronized byte[] getSaveState()
    {
        try
        {
            byte[] save;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // FileOutputStream fileOut = new
            // FileOutputStream("GameContent.ser");
            ObjectOutputStream out = new ObjectOutputStream(stream);
            out.writeObject(this);
            save = stream.toByteArray();
            out.close();
            stream.close();
            return save;
        }
        catch(IOException i)
        {
            i.printStackTrace();
            return null;
        }
    }

}
