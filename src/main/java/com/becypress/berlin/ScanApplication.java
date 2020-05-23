package com.becypress.berlin;

import com.becypress.berlin.controller.ScanController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Description: ScanApplication
 *
 * @author hbl
 * @date 2020/5/23 0023 11:30
 */
@SpringBootApplication
public class ScanApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(new Object[]{ScanApplication.class, ScanController.class}, args);
    }
}
