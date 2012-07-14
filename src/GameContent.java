import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GameContent extends JPanel implements ActionListener
{

    static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    static final Dimension dim = toolkit.getScreenSize();
    static final int GAME_WIDTH = dim.width - 10;
    static final int GAME_HEIGHT = dim.height - 150;

    final int PLAYER_HEIGHT = 50;
    final int PLAYER_WIDTH = 25;
    final int NUM_PLATFORMS = 100;

    static Random rand = new Random();

    public static Color randomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    Timer timer;
    Player player;
    SolidRectangle floor;
    SnowflakeSource snowflakeSource;

    // TODO: Keep sorted by order should be painted back to front
    ArrayList<Drawable> drawables;
    ArrayList<Movable> movables;
    ArrayList<Drawable> addDrawableQueue;
    ArrayList<Movable> addMovableQueue;
    Queue<Drawable> removeQueue;

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

            drawables.add(new SolidRectangle(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
            drawables.add(new VanishingSolidRectangle(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 30 + rand.nextInt(50), 20 + rand.nextInt(40), randomColor(), this));
        }

        GameController controller = new GameController();
        this.addKeyListener(controller);
        player = new Player((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, controller, this);
        drawables.add(player);
        movables.add(player);

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
        // Slow motion for debugging purposes
        if (player.controller.start)
        {
            timer.setDelay(500);
        }
        else if (timer.getDelay() == 500)
        {
            timer.setDelay(25);
        }

        if (player.controller.exit)
        {
            System.exit(0);
        }

        for (int i = 0; i < 10; i++)
        {
            snowflakeSource.produceMovable();
        }

        addOrRemoveQueuedElements();

        for (Movable movable : movables)
        {
            movable.control();
            movable.update();
        }

        for (Drawable drawable : drawables)
        {
            if (drawable instanceof SolidRectangle)
            {
                if (CollisionDetector.areColliding(player, (SolidRectangle) drawable))
                {
                    CollisionHandler.handleCollision(player, (SolidRectangle) drawable);
                    // freeFall = false;
                }
            }
        }

        // gravity
        player.ddy = 1;

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
        int dx = calculateShift(GAME_WIDTH, player.x);
        int dy = calculateShift(GAME_HEIGHT, player.y);

        if (dx != 0 || dy != 0)
        {
            for (Drawable shiftMe : drawables)
            {
                shiftMe.unconditionalShift(dx, dy);
            }
        }

    }

    private static int calculateShift(int gameDimension, int playerDimension)
    {
        int shift;
        return ((shift = (gameDimension - gameDimension / 3) - playerDimension) < 0 || (shift = gameDimension / 3 - playerDimension) > 0) ? shift : 0;
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
