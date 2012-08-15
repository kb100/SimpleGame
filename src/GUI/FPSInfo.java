import java.util.ArrayDeque;

public class FPSInfo
{
    private static final int FRAMES = 20;
    private static ArrayDeque<Long> times = new ArrayDeque<Long>();
    static
    {
        long currentTime = System.nanoTime();
        for(long pastTime = currentTime - (FRAMES-1) * 1000; pastTime <= currentTime; pastTime += 1000)
            times.add(pastTime);
    }
    private static double lastFPS = 0d;
    private static long nanosSinceLastUpdate = 0L;
    public static final double NANOSECONDS_PER_SECOND = 1_000_000_000d;
    public static final double SECONDS_PER_NANOSECOND = 1/NANOSECONDS_PER_SECOND;

    private static void updateTimes()
    {
        long lastNanoTime = times.getLast();
        long nextNanoTime = System.nanoTime();
        nanosSinceLastUpdate = Math.min(30_000_000,nextNanoTime - lastNanoTime);
        if(nanosSinceLastUpdate <= 0)
            throw new IllegalStateException("nanosSinceLastUpdate: " + nanosSinceLastUpdate);
        times.addLast(nextNanoTime);
        if(times.size() > FRAMES)
            times.removeFirst();
    }

    // Call exactly once per frame
    public static void frameTick()
    {
        updateTimes();
        long nanosSpread = times.getLast() - times.getFirst();
        lastFPS = ((FRAMES * NANOSECONDS_PER_SECOND) / nanosSpread);
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
