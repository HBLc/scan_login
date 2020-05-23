package com.becypress.berlin.controller;

import com.becypress.berlin.common.PoolCache;
import com.becypress.berlin.common.ScanCounter;
import com.becypress.berlin.common.ScanPool;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description: ScanController
 *
 * @author hbl
 * @date 2020/5/23 0023 10:00
 */
@Slf4j
@Controller
@EnableAutoConfiguration
public class ScanController
{
    @GetMapping(value = "/index")
    public String index(HttpServletRequest request)
    {
        log.info("生成UUID 成功");

        request.setAttribute("uuid", UUID.randomUUID());

        return "pages/index";
    }

    @GetMapping(value = "/qrcode/{uuid}")
    @ResponseBody
    public void createQRCode(@PathVariable String uuid, HttpServletResponse response)
    {
        // 二维码跳转内容，此处使用了内网穿透代理本机服务
        String qrCodeContent = "http://222.186.174.121:41408/login/" + uuid;
        int width = 300;
        int height = 300;
        String format = "png";
        // 将 UUID 放入缓存
        ScanPool scanPool = new ScanPool();
        PoolCache.cacheMap.put(uuid, scanPool);
        log.info("UUID 放入缓存成功");
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        try
        {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, format, response.getOutputStream());
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        log.info("根据 UUID 生成二维码成功");
    }

    @PostMapping(value = "/pool")
    @ResponseBody
    public String pool(String uuid)
    {
        log.info("检测[" + uuid + "]是否登录");
        ScanPool scanPool = PoolCache.cacheMap.get(uuid);

        if (scanPool == null)
        {
            // 若 UUID 不存在，则为超时被清理
            return "TIMEOUT";
        }

        // 使用计时器，固定时间后不再等待扫码结果，防止页面访问超时
        new Thread(new ScanCounter(scanPool)).start();

        boolean scanStatus = scanPool.getScanStatus();

        if (scanStatus)
        {
            return "success";
        }
        else
        {
            return "fail";
        }
    }

    @GetMapping(value = "/login/{uuid}")
    @ResponseBody
    public String login(@PathVariable String uuid)
    {
        ScanPool scanPool = PoolCache.cacheMap.get(uuid);

        // 二维码不存在，说明超时已被清理
        if (scanPool == null)
        {
            return "TIMEOUT, scan fail";
        }

        // 设置扫码状态为成功
        scanPool.scanSuccess();

        log.info("扫码完成，登录成功");

        return "扫码完成，登录成功";
    }
}
