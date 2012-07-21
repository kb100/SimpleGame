import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener
{

    Timer timer;
    GameContent game;
    GameController controller;
    GameController controller2;
    byte[] save;

    public GamePanel()
    {
        this.setSize(GameContent.GAME_WIDTH, GameContent.GAME_HEIGHT);
        this.setBackground(Color.BLACK);

        save = null;

        game = new GameContent(this);

        controller = new GameController();
        this.addKeyListener(controller);
        game.player.controller = controller;

        controller2 = new GameController(KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_0, KeyEvent.VK_9, KeyEvent.VK_SHIFT, KeyEvent.VK_8);
        this.addKeyListener(controller2);
        game.player2.controller = controller2;

        timer = new Timer(25, this);
        timer.start();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (Drawable drawMe : game.drawables)
        {
            if (drawMe.isOnScreen())
            {
                drawMe.draw(g);
            }
        }

    }

    public void actionPerformed(ActionEvent e)
    {
        game.run();
        repaint();

    }

    public void toggleTimerSpeed()
    {
        if (timer.getDelay() == 25) timer.setDelay(500);
        else timer.setDelay(25);
    }

    public synchronized void loadSavedState()
    {
        try
        {
            // FileInputStream fileIn = new FileInputStream("GameContent.ser");
            if (save == null) return;

            ByteArrayInputStream stream = new ByteArrayInputStream(save);
            ObjectInputStream in = new ObjectInputStream(stream);
            game = (GameContent) in.readObject();
            game.panel = this;
            game.player.controller = controller;
            game.player2.controller = controller2;
            in.close();
            stream.close();
        }
        catch (IOException i)
        {
            // i.printStackTrace();
            game = new GameContent(this);
            System.out.println("io");
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("class not found");
            c.printStackTrace();
            game = new GameContent(this);
        }
    }

    public synchronized void setSaveState()
    {
        save = game.getSaveState();
    }

}
