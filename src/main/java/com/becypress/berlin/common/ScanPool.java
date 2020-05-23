package com.becypress.berlin.common;

import lombok.Data;

/**
 * Description: ScanPool
 *
 * @author hbl
 * @date 2020/5/23 0023 10:20
 */
@Data
public class ScanPool
{
    /**
     * 创建时间
     */
    private Long createTime = System.currentTimeMillis();

    /**
     * 登录状态
     */
    private boolean scanFlag = false;

    public synchronized boolean getScanStatus()
    {
        try
        {
            if (!isScanFlag())
            {
                // 若未扫码，则线程等待
                this.wait();
            }
            if (isScanFlag())
            {
                return Boolean.TRUE;
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void scanSuccess()
    {
        // 扫码成功后，设置扫码状态为true
        this.setScanFlag(true);
        this.notifyAll();
    }

    public synchronized void notifyPool()
    {
        this.notifyAll();
    }
}
