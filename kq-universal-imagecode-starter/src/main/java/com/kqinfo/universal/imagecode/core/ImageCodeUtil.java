package com.kqinfo.universal.imagecode.core;

import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.RandomUtil;
import com.kqinfo.universal.imagecode.properties.ImageCodeProperties;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

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

    private static final String RANDOM_CODE = "ABCDEFGHJKMNPQRSTWXYZ123456789";

    public ImageCodeUtil(ImageCodeProperties imageCodeProperties) {
        ImageCodeUtil.width = imageCodeProperties.getWidth();
        ImageCodeUtil.height = imageCodeProperties.getHeight();
        ImageCodeUtil.codeCount = imageCodeProperties.getCodeCount();
    }

    public static ImageCode createImage() {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = GraphicsUtil.createGraphics(image, Color.WHITE);
        String code = RandomUtil.randomString(RANDOM_CODE, codeCount);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, (int) (height * 0.75));
        GraphicsUtil.drawStringColourful(g, code, font, width, height);
        // 扭曲
        shear(g, width, height, Color.WHITE);
        // 画干扰线
        // y2和y1应该在不同的部分，如y1在上部分，那么y2必须在下部分
        // 位置标志，0上半部分 1下半部分
        int indexFlag = RandomUtil.randomInt(2);
        // 20
        int halfHeight = height >> 1;
        // 0~20 + 20*(0|1) = (0~20| 20~40)
        int y1 = RandomUtil.randomInt(halfHeight) + halfHeight * indexFlag;
        int y2 = RandomUtil.randomInt(halfHeight) + halfHeight * (1 - indexFlag);
        drawInterfere(g, 0, y1, width, y2, 3, ImgUtil.randomColor());

        return new ImageCode()
                .setBufferedImage(image)
                .setCode(code.toLowerCase());
    }

    public static void sendImage(BufferedImage bufferedImage, OutputStream outputStream) {
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

    /**
     * 扭曲
     *
     * @param g     {@link Graphics}
     * @param w1    w1
     * @param h1    h1
     * @param color 颜色
     */
    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    /**
     * X坐标扭曲
     *
     * @param g     {@link Graphics}
     * @param w1    宽
     * @param h1    高
     * @param color 颜色
     */
    private static void shearX(Graphics g, int w1, int h1, Color color) {
        int period;
        if (w1 > 20) {
            // period不在0~5，0~5倾斜度不高
            period = RandomUtil.randomInt(w1 - 10) + 5;
        } else {
            period = RandomUtil.randomInt(w1);
        }


        int frames = 1;
        int phase = RandomUtil.randomInt(2);
        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            g.setColor(color);
            g.drawLine((int) d, i, 0, i);
            g.drawLine((int) d + w1, i, w1, i);
        }
    }

    /**
     * Y坐标扭曲
     *
     * @param g     {@link Graphics}
     * @param w1    宽
     * @param h1    高
     * @param color 颜色
     */
    private static void shearY(Graphics g, int w1, int h1, Color color) {
        int period;
        if (w1 > 30) {
            // period不在0~8，0~8倾斜度不高
            period = RandomUtil.randomInt((h1 - 16) >> 1) + 8;
        } else {
            period = RandomUtil.randomInt(h1 >> 1);
        }

        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            g.setColor(color);
            // 擦除原位置的痕迹
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + h1, i, h1);
        }

    }

    /**
     * 干扰线
     *
     * @param g         {@link Graphics}
     * @param x1        x1
     * @param y1        y1
     * @param x2        x2
     * @param y2        y2
     * @param thickness 粗细
     * @param c         颜色
     */
    @SuppressWarnings("SameParameterValue")
    private static void drawInterfere(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {

        // The thick line is in fact a filled polygon
        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;
        // line length
        double lineLength = Math.sqrt(dX * dX + (double) (dY * dY));

        double scale = (double) (thickness) / (2 * lineLength);

        // The x and y increments from an endpoint needed to create a
        // rectangle...
        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        // Now we can compute the corner points...
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }

}
