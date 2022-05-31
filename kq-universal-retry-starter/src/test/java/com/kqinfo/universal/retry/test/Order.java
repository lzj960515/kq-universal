package com.kqinfo.universal.retry.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order implements Serializable {

    private Integer id;

    private LocalDateTime createTime;

    private Goods goods;
}
