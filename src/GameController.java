import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameController implements KeyListener
{
	boolean left;
	int keyLeft;

	boolean right;
	int keyRight;

	boolean up;
	int keyUp;

	boolean down;
	int keyDown;

	boolean start;
	int keyStart;

	boolean select;
	int keySelect;

	boolean jump;
	int keyJump;

	public GameController()
	{
		left = right = up = down = start = select = jump = false;
		configureButtons(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_SPACE);
	}

	public GameController(int keyLeft, int keyRight, int keyUp, int keyDown, int keyStart, int keySelect, int keyJump)
	{
		left = right = up = down = start = select = jump = false;
		configureButtons(keyLeft, keyRight, keyUp, keyDown, keyStart, keySelect, keyJump);
	}

	public void configureButtons(int keyLeft, int keyRight, int keyUp, int keyDown, int keyStart, int keySelect, int keyJump)
	{
		this.keyLeft = keyLeft;
		this.keyRight = keyRight;
		this.keyUp = keyUp;
		this.keyDown = keyDown;
		this.keyStart = keyStart;
		this.keySelect = keySelect;
		this.keyJump = keyJump;
	}

	public synchronized void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code == keyLeft) left = true;
		else if (code == keyRight) right = true;
		else if (code == keyUp) up = true;
		else if (code == keyDown) down = true;
		else if (code == keyStart) start = true;
		else if (code == keySelect) select = true;
		else if (code == keyJump) jump = true;
	}

	public synchronized void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();
		if (code == keyLeft) left = false;
		else if (code == keyRight) right = false;
		else if (code == keyUp) up = false;
		else if (code == keyDown) down = false;
		else if (code == keyStart) start = false;
		else if (code == keySelect) select = false;
		else if (code == keyJump) jump = false;
	}

	public void keyTyped(KeyEvent e)
	{
	}
}
