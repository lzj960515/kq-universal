package com.kqinfo.universal.redis.test;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.concurrent.TimeUnit;

/**
 * spel test
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public class SpelTest {

    @Test
    public void spelTest(){
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#a");

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("a", "world");
        Object str = expression.getValue(context);
        System.out.println(str);
    }

    @Test
    public void entityTest(){
        SpelParserConfiguration config = new SpelParserConfiguration(true,true);
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#user.name + #user.age");

        EvaluationContext context = new StandardEvaluationContext();
        User user = new User();
        user.setName("zhangsan");
        user.setAge(12);
        context.setVariable("user", user);
        Object str = expression.getValue(context);
        System.out.println(str);

        Expression a = parser.parseExpression("a");
        Object value = a.getValue(user);
        System.out.println(value);
    }

    @Test
    public void unitTest(){
        System.out.println(TimeUnit.MINUTES.toSeconds(1));
    }

}


