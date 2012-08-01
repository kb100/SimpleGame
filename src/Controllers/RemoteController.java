public class RemoteController implements Controller
{
    ControllerState bufferedState;

    public RemoteController()
    {
        // start a thread which continuously goes to internet to get
        // controllerState;
    }

    public ControllerState getControllerState()
    {
        ControllerState state;

        while(bufferedState == null)
        {
            try
            {
                wait();
            }
            catch(InterruptedException e)
            {
                System.out.println("interrupt");
            }
        }
        state = bufferedState;
        bufferedState = null;
        return state;

    }

}
