import java.io.Serializable;

public class ControllerState implements Serializable
{
    boolean left;
    boolean right;
    boolean up;
    boolean down;
    boolean start;
    boolean select;
    boolean jump;
    boolean exit;

    public ControllerState()
    {
        left = right = up = down = start = select = jump = false;
    }

    public ControllerState( ControllerState other )
    {
        left = other.left;
        right = other.right;
        up = other.up;
        down = other.down;
        start = other.start;
        select = other.select;
        jump = other.jump;
        exit = other.exit;
    }
}
