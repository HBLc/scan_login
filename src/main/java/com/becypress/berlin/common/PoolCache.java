package com.becypress.berlin.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: PoolCache
 *
 * @author hbl
 * @date 2020/5/23 0023 10:20
 */
@Data
@Slf4j
public class PoolCache
{
    private PoolCache()
    {
    }

    /**
     * 缓存超时时间 80秒
     */
    private static Long timeOutSecond = 80L;

    /**
     * 每一分钟清理一次缓存
     */
    private static Long cleanIntervalSecond = 60L;

    public static Map<String, ScanPool> cacheMap = new ConcurrentHashMap<String, ScanPool>();

    static
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        // 每一分钟清理一次缓存
                        Thread.sleep(cleanIntervalSecond * 1000);
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                    clean();
                }
            }

            public void clean()
            {
                log.info("清理缓存...");

                if (!cacheMap.keySet().isEmpty())
                {
                    Iterator<String> iterator = cacheMap.keySet().iterator();
                    while (iterator.hasNext())
                    {
                        String key = iterator.next();
                        ScanPool pool = cacheMap.get(key);
                        if (System.currentTimeMillis() - pool.getCreateTime() > timeOutSecond * 1000)
                        {
                            // 若当前时间 - 二维码创建时间 > 80秒，则移除(失效)该二维码
                            cacheMap.remove(key);
                        }
                    }
                }
            }
        }).start();
    }
}
