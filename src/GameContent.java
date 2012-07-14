import java.awt.Color;
import java.awt.Graphics;
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
    final int GAME_WIDTH = 500;
    final int GAME_HEIGHT = 500;
    final int PLAYER_HEIGHT = 50;
    final int PLAYER_WIDTH = 25;
    final int NUM_RECTANGLES = 150;

    static Random rand = new Random();

    public Color randomColor()
    {
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    Timer timer;
    Player player;
    SolidRectangle floor;

    // TODO: Keep sorted by order should be painted back to front
    ArrayList<Drawable> drawables;
    Queue<Drawable> removeQueue;

    public GameContent()
    {
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setBackground(Color.BLACK);

        drawables = new ArrayList<Drawable>();
        removeQueue = new LinkedList<Drawable>();

        floor = new SolidRectangle(20, 460, 460, 20, Color.GRAY, this);
        drawables.add(floor);

        for (int i = 0; i < NUM_RECTANGLES; i++)
        {
            drawables.add(new SolidRectangle(rand.nextInt(GAME_WIDTH), rand.nextInt(GAME_HEIGHT), 10 + rand.nextInt(50), 10 + rand.nextInt(50), randomColor(), this));
        }

        GameController controller = new GameController();
        this.addKeyListener(controller);
        player = new Player((GAME_WIDTH - PLAYER_WIDTH) / 2, (GAME_HEIGHT - PLAYER_HEIGHT) / 2, PLAYER_WIDTH, PLAYER_HEIGHT, controller, this);
        drawables.add(player);

        timer = new Timer(25, this);
        timer.start();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (Drawable drawMe : drawables)
        {
            drawMe.draw(g);
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

        drawables.removeAll(removeQueue);

        player.control();
        player.update();

        boolean freeFall = true;

        for (Drawable drawable : drawables)
        {
            if (drawable instanceof SolidRectangle)
            {
                if (CollisionDetector.areColliding(player, (SolidRectangle) drawable))
                {
                    CollisionHandler.handleCollision(player, (SolidRectangle) drawable);
                    freeFall = false;
                }
            }
        }

        if (freeFall)
        {
            // gravity
            player.ddy = 1;
        }

        adjustFrameIfNecessary();
        repaint();
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

}
