import java.awt.Container;
import javax.swing.JApplet;

public class SimpleGame extends JApplet
{
    public void init()
    {
        this.setSize(500, 500);
        GameContent panel = new GameContent();
        Container contentPane = this.getContentPane();
        panel.setFocusable(true);
        contentPane.setLayout(null);
        contentPane.add(panel);
    }
}
