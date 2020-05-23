package com.becypress.berlin.common;

/**
 * Description: ScanCounter
 *
 * @author hbl
 * @date 2020/5/23 0023 11:06
 */
public class ScanCounter implements Runnable
{
    /**
     * 指定超时时间，超过27秒扔未扫码则刷新二维码
     */
    public static final Long TIMEOUT = 27000L;

    private final ScanPool scanPool;

    public ScanCounter(ScanPool scanPool)
    {
        this.scanPool = scanPool;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(TIMEOUT);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        this.notifyPool(scanPool);
    }

    public synchronized void notifyPool(ScanPool scanPool)
    {
        scanPool.notifyPool();
    }
}
