import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public class GameContent implements Serializable
{

    static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Dimension dim = toolkit.getScreenSize();
    static final int GAME_WIDTH = dim.width - 10;
    static final int GAME_HEIGHT = dim.height - 150;

    static final int PLAYER_HEIGHT = 50;
    static final int PLAYER_WIDTH = 25;
    static final int NUM_PLATFORMS = 1000;

    static Random rand = new Random();

    public static Color randomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    transient GamePanel panel;

    GameContent savedState;
    LocalPlayer player;
    LocalPlayer player2;

    Drawable frameReference;

    // TODO: Keep sorted by order should be painted back to front
    List<LocalPlayer> localPlayers;
    ArrayList<Drawable> drawables;
    THashSet<Movable> movables;
    List<Drawable> addDrawableQueue;
    List<Movable> addMovableQueue;
    List<Drawable> removeQueue;

    QuadTree tree = new QuadTree();

    public GameContent(GamePanel panel)
    {
        this.panel = panel;
        savedState = null;

        localPlayers = new ArrayList<LocalPlayer>();
        drawables = new ArrayList<Drawable>();
        movables = new THashSet<Movable>();
        addDrawableQueue = new ArrayList<Drawable>();
        addMovableQueue = new ArrayList<Movable>();
        removeQueue = new LinkedList<Drawable>();

        addMovable(new SnowflakeSource(GAME_WIDTH/2,0, this));

        SolidRectangle floor = new SolidRectangle(20, GAME_HEIGHT - 40, GAME_WIDTH - 40, 20, Color.GRAY, this);
        addDrawable(floor);

        for(int i = 0; i < NUM_PLATFORMS; i++)
        {

            drawables.add(new SolidRectangle(rand.nextInt(5000) - 2500, rand.nextInt(5000) - 2500, 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
            drawables.add(new VanishingSolidRectangle(rand.nextInt(5000) - 2500, rand.nextInt(5000) - 2500, 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
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

    // TODO: don't use arraylist, too slow
    // dont use THashSet, nonconstant order
    public void addOrRemoveQueuedElements()
    {
        {
            Iterator<Drawable> it;
            Drawable current;
            if(removeQueue.size() > 0)
            {
                it = removeQueue.iterator();
                while(it.hasNext())
                {
                    current = it.next();
                    drawables.remove(current);
                    movables.remove(current);
                    it.remove();
                }
            }

            if(addDrawableQueue.size() > 0)
            {
                it = addDrawableQueue.iterator();
                while(it.hasNext())
                {
                    current = it.next();
                    drawables.add(current);
                    it.remove();
                }
            }
        }

        if(addMovableQueue.size() > 0)
        {
            Iterator<Movable> it = addMovableQueue.iterator();
            Movable current;
            while(it.hasNext())
            {
                current = it.next();
                drawables.add(current);
                movables.add(current);
                it.remove();
            }
        }
    }

    public void adjustFrameIfNecessary()
    {
        int dx = calculateShift(GAME_WIDTH, frameReference.x);
        int dy = calculateShift(GAME_HEIGHT, frameReference.y);

        if(dx != 0 || dy != 0)
        {
            for(Drawable shiftMe : drawables)
            {
                shiftMe.unconditionalShift(dx, dy);
            }
        }
    }

    public void toggleReferenceFrame()
    {
        if(frameReference == player)
            frameReference = player2;
        else frameReference = player;
    }

    List<SolidRectangle> findSolidRectanglesInArea(Rectangle rectangle)
    {
        List<SolidRectangle> rectangles = new ArrayList<SolidRectangle>();
        for(Drawable drawable : drawables)
        {
            if(drawable instanceof SolidRectangle)
            {
                if(rectangle.intersects(((SolidRectangle)drawable).toRectangle()))
                {
                    rectangles.add((SolidRectangle)drawable);
                }
            }
        }
        return rectangles;
    }

    List<LocalPlayer> findPlayersInArea(Rectangle rectangle)
    {
        ArrayList<LocalPlayer> inArea = new ArrayList<LocalPlayer>();
        for(LocalPlayer player : localPlayers)
        {
            if(rectangle.intersects(player.toRectangle()))
            {
                inArea.add(player);
            }
        }
        return inArea;
    }

    private static int calculateShift(int gameDimension, int referenceDimension)
    {
        int shift;
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
