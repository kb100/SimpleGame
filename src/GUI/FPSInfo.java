import java.util.LinkedList;

public class FPSInfo
{
    private static LinkedList<Long> times = new LinkedList<Long>();
    private static final int FRAMES = 20;

    private static long getNanosSinceLastUpdate()
    {
        times.add(System.nanoTime());
        if(times.size() > FRAMES)
            times.poll();
        return times.getLast() - times.getFirst();
    }

    public static double getFPS()
    {
        long nanos = getNanosSinceLastUpdate();
        return FRAMES * 1_000_000_000d / nanos;
    }
}
