import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class GameController implements KeyListener, Controller, Serializable
{
    int keyLeft;
    int keyRight;
    int keyUp;
    int keyDown;
    int keyStart;
    int keySelect;
    int keyJump;
    int keyExit;
    ControllerState state;

    public GameController()
    {
        state = new ControllerState();
        configureButtons(KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_SPACE, KeyEvent.VK_ESCAPE);
    }

    public GameController(int keyLeft, int keyRight, int keyUp, int keyDown, int keyStart, int keySelect, int keyJump, int keyExit)
    {
        state = new ControllerState();
        configureButtons(keyLeft, keyRight, keyUp, keyDown, keyStart, keySelect, keyJump, keyExit);
    }

    public synchronized void configureButtons(int keyLeft, int keyRight, int keyUp, int keyDown, int keyStart, int keySelect, int keyJump, int keyExit)
    {
        this.keyLeft = keyLeft;
        this.keyRight = keyRight;
        this.keyUp = keyUp;
        this.keyDown = keyDown;
        this.keyStart = keyStart;
        this.keySelect = keySelect;
        this.keyJump = keyJump;
        this.keyExit = keyExit;
    }

    public synchronized void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();
        if (code == keyLeft) state.left = true;
        else if (code == keyRight) state.right = true;
        else if (code == keyUp) state.up = true;
        else if (code == keyDown) state.down = true;
        else if (code == keyJump) state.jump = true;
        else if (code == keyStart) state.start = true;
        else if (code == keySelect) state.select = true;
        else if (code == keyExit) state.exit = true;
    }

    public synchronized void keyReleased(KeyEvent e)
    {
        int code = e.getKeyCode();
        if (code == keyLeft) state.left = false;
        else if (code == keyRight) state.right = false;
        else if (code == keyUp) state.up = false;
        else if (code == keyDown) state.down = false;
        else if (code == keyJump) state.jump = false;
    }

    public synchronized void hasBeenHandled(int code)
    {
        if(code == keyStart) state.start = false;
        else if(code == keySelect) state.select = false;
        else if (code == keyExit) state.exit = false;
    }
    
    public synchronized ControllerState getControllerState()
    {
        return new ControllerState(state);
    }

    public void keyTyped(KeyEvent e)
    {
    }

}
