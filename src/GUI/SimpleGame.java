import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JApplet;

public class SimpleGame extends JApplet
{
    GamePanel panel;

    public void init()
    {
        this.setSize((int)GameContent.GAME_WIDTH, (int)GameContent.GAME_HEIGHT);
        this.setIgnoreRepaint(true);
        panel = new GamePanel();
        Container contentPane = this.getContentPane();
        panel.setFocusable(true);
        contentPane.setLayout(null);
        contentPane.add(panel);

        this.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent e)
            {
                panel.setLocation((int)((e.getComponent().getWidth() - GameContent.GAME_WIDTH) / 2d), (int)((e.getComponent().getHeight() - GameContent.GAME_HEIGHT) / 2d));
            }

            public void componentShown(ComponentEvent e){}
            public void componentMoved(ComponentEvent e){}
            public void componentHidden(ComponentEvent e){}
        });
    }

}
