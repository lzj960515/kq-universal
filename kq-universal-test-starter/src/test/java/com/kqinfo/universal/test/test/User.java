package com.kqinfo.universal.test.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 2.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String name;

    private Integer age;

    private List<Hobby> hobbies;

    private Order order;

    List<User> children;

    public User(String name, Integer age, List<Hobby> hobbies, Order order) {
        this.name = name;
        this.age = age;
        this.hobbies = hobbies;
        this.order = order;
    }
}
