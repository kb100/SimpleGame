import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GameContent extends JPanel implements ActionListener
{

    static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Dimension dim = toolkit.getScreenSize();
    static final int GAME_WIDTH = dim.width - 10;
    static final int GAME_HEIGHT = dim.height - 150;

    static final int PLAYER_HEIGHT = 50;
    static final int PLAYER_WIDTH = 25;
    static final int NUM_PLATFORMS = 30;
    static final int FLAKES_PER_FRAME = 1;

    static Random rand = new Random();

    public static Color randomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    Timer timer;
    LocalPlayer player;
    LocalPlayer player2;

    Drawable frameReference;

    SolidRectangle floor;
    SnowflakeSource snowflakeSource;

    // TODO: Keep sorted by order should be painted back to front
    List<Drawable> drawables;
    List<Movable> movables;
    List<Drawable> addDrawableQueue;
    List<Movable> addMovableQueue;
    List<Drawable> removeQueue;

    public GameContent()
    {
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setBackground(Color.BLACK);

        drawables = new ArrayList<Drawable>();
        movables = new ArrayList<Movable>();
        addDrawableQueue = new ArrayList<Drawable>();
        addMovableQueue = new ArrayList<Movable>();
        removeQueue = new LinkedList<Drawable>();

        snowflakeSource = new SnowflakeSource(GAME_WIDTH / 2, 0, this);
        drawables.add(snowflakeSource);

        floor = new SolidRectangle(20, GAME_HEIGHT - 40, GAME_WIDTH - 40, 20, Color.GRAY, this);
        drawables.add(floor);

        for (int i = 0; i < NUM_PLATFORMS; i++)
        {

             drawables.add(new SolidRectangle(rand.nextInt(GAME_WIDTH),
             rand.nextInt(GAME_HEIGHT), 30 + rand.nextInt(50), 20 +
             rand.nextInt(40), randomColor(), this));
            drawables.add(new VanishingSolidRectangle(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
        }

        GameController controller = new GameController();
        this.addKeyListener(controller);
        player = new LocalPlayer((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, controller, this);
        drawables.add(player);
        movables.add(player);
        frameReference = player;

        GameController controller2 = new GameController(KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_0, KeyEvent.VK_9, KeyEvent.VK_SHIFT, KeyEvent.VK_8);
        this.addKeyListener(controller2);
        player2 = new LocalPlayer((GAME_WIDTH - PLAYER_WIDTH) / 2 + 40, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, controller2, this);
        drawables.add(player2);
        movables.add(player2);

        timer = new Timer(25, this);
        timer.start();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        for (Drawable drawMe : drawables)
        {
            if (drawMe.isOnScreen())
            {
                drawMe.draw(g);
            }
        }
    }

    // Always a timer event
    public void actionPerformed(ActionEvent e)
    {

        // System.out.println("drawables: " + drawables.size() + " movables: " +
        // movables.size() + " addDrawableQueue: " + addDrawableQueue.size() +
        // " addMovableQueue: " + addMovableQueue.size() + " removeQueue: " +
        // removeQueue.size());
        for (int i = 0; i < FLAKES_PER_FRAME; i++)
        {
            snowflakeSource.produceMovable();
        }
        addOrRemoveQueuedElements();

        for (Movable movable : movables)
        {
            movable.control();
            movable.update();
        }

        
        //Check collisions

        for (Drawable drawable : drawables)
        {

            if (drawable instanceof SolidRectangle)
            {
                if (drawable != player && CollisionDetector.areColliding(player, (SolidRectangle) drawable))
                {
                    CollisionHandler.handleCollision(player, (SolidRectangle) drawable);
                }

                if (drawable != player2 && CollisionDetector.areColliding(player2, (SolidRectangle) drawable))
                {
                    CollisionHandler.handleCollision(player2, (SolidRectangle) drawable);
                }
            }
        }

        if (CollisionDetector.areColliding(player, player2))
        {
            CollisionHandler.handleCollision(player, player2);
        }

        // gravity
        player.ddy = 1;
        player2.ddy = 1;

        adjustFrameIfNecessary();
        repaint();
    }

    public void addOrRemoveQueuedElements()
    {
        if (removeQueue.size() > 0)
        {
            drawables.removeAll(removeQueue);
            movables.removeAll(removeQueue);
            removeQueue.clear();
        }

        if (addDrawableQueue.size() > 0)
        {
            drawables.addAll(addDrawableQueue);
            addDrawableQueue.clear();
        }

        if (addMovableQueue.size() > 0)
        {
            drawables.addAll(addMovableQueue);
            movables.addAll(addMovableQueue);
            addMovableQueue.clear();
        }
    }

    public void adjustFrameIfNecessary()
    {
        int dx = calculateShift(GAME_WIDTH, frameReference.x);
        int dy = calculateShift(GAME_HEIGHT, frameReference.y);

        if (dx != 0 || dy != 0)
        {
            for (Drawable shiftMe : drawables)
            {
                shiftMe.unconditionalShift(dx, dy);
            }
        }
    }

    public void toggleReferenceFrame()
    {
        frameReference = (frameReference == player ? player2 : player);
    }

    public void toggleTimerSpeed()
    {
        if (timer.getDelay() == 25) timer.setDelay(500);
        else timer.setDelay(25);
    }

    List<SolidRectangle> solidRectanglesInArea(Rectangle rectangle)
    {
        List<SolidRectangle> rectangles = new ArrayList<SolidRectangle>();
        for (Drawable drawable : drawables)
        {
            if (drawable instanceof SolidRectangle && !(drawable instanceof Snowflake))
            {
                if (rectangle.intersects(((SolidRectangle) drawable).toRectangle()))
                {
                    rectangles.add((SolidRectangle) drawable);
                }
            }
        }
        return rectangles;

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

}
