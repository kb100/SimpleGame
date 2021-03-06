import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class GameController implements KeyListener, Controller, Serializable
{
    int keyLeft;
    int keyRight;
    int keyUp;
    int keyDown;
    int keyJump;
    ControllerState state;

    public GameController()
    {
        state = new ControllerState();
        configureButtons(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE);
    }

    public GameController(int keyLeft, int keyRight, int keyUp, int keyDown, int keyJump)
    {
        state = new ControllerState();
        configureButtons(keyLeft, keyRight, keyUp, keyDown, keyJump);
    }

    public synchronized void configureButtons(int keyLeft, int keyRight, int keyUp, int keyDown, int keyJump)
    {
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.keyJump = keyJump;
    }

    public synchronized void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
        if(code == keyLeft)
            state.left = true;
        else if(code == keyRight)
            state.right = true;
        else if(code == keyUp)
            state.up = true;
        else if(code == keyDown)
            state.down = true;
        else if(code == keyJump)
            state.jump = true;
    }

    public synchronized void keyReleased(KeyEvent e)
    {
        int code = e.getKeyCode();
        if(code == keyLeft)
            state.left = false;
        else if(code == keyRight)
            state.right = false;
        else if(code == keyUp)
            state.up = false;
        else if(code == keyDown)
            state.down = false;
        else if(code == keyJump)
            state.jump = false;
    }

    public synchronized ControllerState getControllerState()
    {
        return new ControllerState(state);
    }

    public void keyTyped(KeyEvent e)
    {
    }

}
