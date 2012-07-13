import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController implements KeyListener
{
	boolean left;
	boolean right;
	boolean up;
	boolean down;
	boolean start;
	boolean select;
	boolean jump;

	public GameController()
	{
		left = right = up = down = start = select = jump = false;
	}

	public synchronized void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_A) left = true;
		else if (code == KeyEvent.VK_D) right = true;
		else if (code == KeyEvent.VK_W) up = true;
		else if (code == KeyEvent.VK_S) down = true;
		else if (code == KeyEvent.VK_SPACE) jump = true;
	}

	public synchronized void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_A) left = false;
		else if (code == KeyEvent.VK_D) right = false;
		else if (code == KeyEvent.VK_W) up = false;
		else if (code == KeyEvent.VK_S) down = false;
		else if (code == KeyEvent.VK_SPACE) jump = false;
	}

	public void keyTyped(KeyEvent e)
	{
	}
}
