package com.kqinfo.universal.imagecode.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.awt.image.BufferedImage;

/**
 * @author Zijian Liao
 * @since 1.5.0
 */
@Data
@Accessors(chain = true)
public class ImageCode {

    private BufferedImage bufferedImage;

    private String code;
}
