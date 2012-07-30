public class FPSInfo
{
    private static long lastTime = System.nanoTime();

    private static long getNanosSinceLastUpdate()
    {
        long currentTime = System.nanoTime();
        long difference = currentTime - lastTime;
        lastTime = currentTime;
        return difference;
    }

    public static double getFPS()
    {
        long nanos = getNanosSinceLastUpdate();
        return 1_000_000_000d / nanos;
    }
}
