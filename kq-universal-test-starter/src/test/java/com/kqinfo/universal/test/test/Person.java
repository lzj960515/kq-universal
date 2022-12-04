package com.kqinfo.universal.test.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;
}
