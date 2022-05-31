package com.kqinfo.universal.log.test;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Data
public class Order implements Serializable {

    private static final long serialVersionUID = 224639844131348155L;

    private String id;
}
