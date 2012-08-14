import java.util.ArrayDeque;

public class FPSInfo
{
    private static ArrayDeque<Long> times = new ArrayDeque<Long>();
    static
    {
        long currentTime = System.nanoTime();
        for(long pastTime = currentTime - 20 * 1000; pastTime <= currentTime; pastTime += 1000)
            times.add(pastTime);
    }
    private static final int FRAMES = 20;
    private static double lastFPS = 0d;
    private static long nanosSinceLastUpdate = 0L;

    private static void updateTimes()
    {
        long lastNanoTime = times.getLast();
        long nextNanoTime = System.nanoTime();
        nanosSinceLastUpdate = nextNanoTime - lastNanoTime;
        if(nanosSinceLastUpdate <= 0)
            throw new IllegalStateException("nanosSinceLastUpdate: " + nanosSinceLastUpdate);
        times.addLast(nextNanoTime);
        if(times.size() > FRAMES)
            times.poll();
    }

    // Call exactly once per frame
    public static void frameTick()
    {
        updateTimes();
        long nanosSpread = times.getLast() - times.getFirst();
        lastFPS = (FRAMES * 1_000_000_000d / nanosSpread);
    }

    public static double getLastFPS()
    {
        return lastFPS;
    }

    public static long getNanosSinceLastUpdate()
    {
        return nanosSinceLastUpdate;
    }
}
