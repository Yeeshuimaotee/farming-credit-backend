package com.example.farmingcreditbackend.service;

import com.example.farmingcreditbackend.dto.CaptchaResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 */
@Service
@Slf4j
public class CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${system.captcha.expire}")
    private int expire;

    @Value("${system.captcha.width}")
    private int width;

    @Value("${system.captcha.height}")
    private int height;

    @Value("${system.captcha.length}")
    private int length;

    @Value("${system.captcha.chars}")
    private String chars;

    @Value("${system.captcha.font-size}")
    private int fontSize;

    private static final String CAPTCHA_KEY = "CAPTCHA:";

    /**
     * 生成验证码
     */
    public CaptchaResponseDTO generateCaptcha() {
        // 生成随机验证码
        String captchaCode = RandomStringUtils.random(length, chars).toLowerCase();
        String captchaKey = UUID.randomUUID().toString();

        // 生成验证码图片
        String captchaImage = generateCaptchaImage(captchaCode);

        // 存储到Redis
        redisTemplate.opsForValue().set(
                CAPTCHA_KEY + captchaKey,
                captchaCode,
                expire,
                TimeUnit.SECONDS
        );

        return CaptchaResponseDTO.builder()
                .captchaCode(captchaKey)
                .captchaImage(captchaImage)
                .expireTime(System.currentTimeMillis() + expire * 1000)
                .build();
    }

    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String captchaKey, String inputCode) {
        if (StringUtils.isEmpty(captchaKey) || StringUtils.isEmpty(inputCode)) {
            return false;
        }

        String redisKey = CAPTCHA_KEY + captchaKey;
        String storedCode = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            return false;
        }

        // 验证成功后删除验证码
        redisTemplate.delete(redisKey);

        return storedCode.equalsIgnoreCase(inputCode);
    }

    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String code) {
        try {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // 设置抗锯齿
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 设置背景
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // 设置边框
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(0, 0, width - 1, height - 1);

            // 添加干扰点
            Random random = new Random();
            for (int i = 0; i < 50; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                g.setColor(new Color(random.nextInt(220), random.nextInt(220), random.nextInt(220)));
                g.drawOval(x, y, 1, 1);
            }

            // 添加干扰线
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(random.nextInt(220), random.nextInt(220), random.nextInt(220)));
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                int x2 = random.nextInt(width);
                int y2 = random.nextInt(height);
                g.drawLine(x1, y1, x2, y2);
            }

            // 绘制验证码
            g.setFont(new Font("Arial", Font.BOLD, fontSize));
            for (int i = 0; i < code.length(); i++) {
                // 随机颜色
                g.setColor(new Color(
                        random.nextInt(150) + 50,
                        random.nextInt(150) + 50,
                        random.nextInt(150) + 50
                ));

                // 随机角度
                double theta = (random.nextDouble() - 0.5) * Math.PI * 0.3;

                // 绘制字符
                g.rotate(theta, 20 + i * 25, 30);
                g.drawString(String.valueOf(code.charAt(i)), 20 + i * 25, 30);
                g.rotate(-theta, 20 + i * 25, 30);
            }

            g.dispose();

            // 转换为Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            throw new RuntimeException("生成验证码失败");
        }
    }
}