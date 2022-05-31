package com.kqinfo.universal.imagecode.core;

import com.kqinfo.universal.imagecode.properties.ImageCodeProperties;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author Zijian Liao
 * @since 1.5.0
 */
@Slf4j
public class ImageCodeUtil {

    /**
     * 图片的宽度
     */
    private static int width;
    /**
     * 图片的高度
     */
    private static int height;
    /**
     * 验证码字符个数
     */
    private static int codeCount;
    /**
     * 验证码干扰线数
     */
    private static int lineCount;

    public ImageCodeUtil(ImageCodeProperties imageCodeProperties){
        ImageCodeUtil.width = imageCodeProperties.getWidth();
        ImageCodeUtil.height = imageCodeProperties.getHeight();
        ImageCodeUtil.codeCount = imageCodeProperties.getCodeCount();
        ImageCodeUtil.lineCount = imageCodeProperties.getLineCount();
    }

    private static final Random RANDOM = new Random();


    public static ImageCode createImage(){
        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        drawLine(g);
        String code = drawCode(g);
        return new ImageCode()
                .setBufferedImage(buffImg)
                .setCode(code);
    }

    public static void sendImage(BufferedImage bufferedImage, OutputStream outputStream){
        try {
            ImageIO.write(bufferedImage, "png", outputStream);
        } catch (IOException ignored) {
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private static void drawLine(Graphics2D g){
        // 生成随机数
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体
        Font font = new Font("Arial",Font.PLAIN, height - 2);
        g.setFont(font);
        for (int i = 0; i < lineCount; i++) {
            int xs = RANDOM.nextInt(width);
            int ys = RANDOM.nextInt(height);
            int xe = xs+RANDOM.nextInt(width/8);
            int ye = ys+RANDOM.nextInt(height/8);
            int red = RANDOM.nextInt(255);
            int green = RANDOM.nextInt(255);
            int blue = RANDOM.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }
    }

    private static String drawCode(Graphics2D g){
        // 每个字符的宽度
        int x = width / (codeCount +2);
        // randomCode记录随机产生的验证码
        StringBuilder randomCode = new StringBuilder();
        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(RANDOM.nextInt(10));
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            int red = RANDOM.nextInt(255);
            int green = RANDOM.nextInt(255);
            int blue = RANDOM.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (i + 1) * x, height - 4);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        return randomCode.toString();
    }

}
