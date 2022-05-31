package com.kqinfo.universal.log.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Operator implements Serializable {

    private static final long serialVersionUID = 8548440618495384061L;

    private String userId;

    private String username;
}
