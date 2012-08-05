import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
    FPSInfo fps;
    byte[] save;
    boolean drawQuadTree;
    boolean drawFPSInfo;

    public GamePanel()
    {
        this.setSize(GameContent.GAME_WIDTH, GameContent.GAME_HEIGHT);
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        save = null;

        game = new GameContent(this);
        drawQuadTree = false;
        drawFPSInfo = true;

        controller = new GameController();
        this.addKeyListener(controller);
        game.player.controller = controller;

        controller2 = new GameController(KeyEvent.VK_J, KeyEvent.VK_L, KeyEvent.VK_I, KeyEvent.VK_K, KeyEvent.VK_0, KeyEvent.VK_9, KeyEvent.VK_SHIFT, KeyEvent.VK_8);
        this.addKeyListener(controller2);
        game.player2.controller = controller2;

        timer = new Timer(25, this);
        timer.start();

//        this.addKeyListener(new KeyListener()
//        {
//            public void keyTyped(KeyEvent e)
//            {
//            }
//
//            public void keyReleased(KeyEvent e)
//            {
//            }
//
//            public void keyPressed(KeyEvent e)
//            {
//                int code = e.getKeyCode();
//                if(code == KeyEvent.VK_1) // load/save
//                {
//                    if((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)
//                        setSaveState();
//                    else
//                        loadSavedState();
//                }
//                else if(code == KeyEvent.VK_BACK_SPACE) // toggle reference frame
//                {
//                    game.toggleReferenceFrame();
//                }
//                else if(code == KeyEvent.VK_2)
//                {
//                    toggleTimerSpeed();
//                }
//                else if(code == KeyEvent.VK_3)
//                {
//                    drawQuadTree = !drawQuadTree;
//                }
//                else if(code == KeyEvent.VK_4)
//                {
//                    drawFPSInfo = !drawFPSInfo;
//                }
//                else if(code == KeyEvent.VK_ESCAPE)
//                {
//                    System.exit(0);
//                }
//            }
//        });
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for(Drawable drawMe : game.drawables)
        {
            if(drawMe.isOnScreen())
                drawMe.draw(g);
        }

        if(drawFPSInfo)
        {

            g.setColor(Color.green);
            g.drawString("FPS: " + FPSInfo.getFPS(), 10, 15);
            g.drawString("drawables: " + game.drawables.size(), 10, 30);
            g.drawString("movables: " + game.movables.size(), 10, 45);
            g.drawString("removeQueue: " + game.removeQueue.size(), 10, 60);
        }

        if(drawQuadTree)
            game.tree.draw(g);

    }

    public void actionPerformed(ActionEvent e)
    {
        game.run();
        repaint();
    }

    public void toggleTimerSpeed()
    {
        if(timer.getDelay() == 25)
            timer.setDelay(500);
        else
            timer.setDelay(25);
    }

    public synchronized void loadSavedState()
    {
        try
        {
            // FileInputStream fileIn = new FileInputStream("GameContent.ser");
            if(save == null)
                return;

            ByteArrayInputStream stream = new ByteArrayInputStream(save);
            ObjectInputStream in = new ObjectInputStream(stream);
            game = (GameContent)in.readObject();
            game.panel = this;
            game.player.controller = controller;
            game.player2.controller = controller2;
            in.close();
            stream.close();
        }
        catch(IOException i)
        {
            // i.printStackTrace();
            game = new GameContent(this);
            System.out.println("io");
        }
        catch(ClassNotFoundException c)
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
