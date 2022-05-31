package com.kqinfo.universal.log.parser;

import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);
    private final ExpressionParser parser = new SpelExpressionParser();

    public Expression getExpression(AnnotatedElementKey methodKey, String expression){
        return super.getExpression(expressionCache, methodKey, expression);
    }

    public Expression getTemplateExpression(String expression){
        TemplateParserContext templateParserContext = new TemplateParserContext("{", "}");
        return parser.parseExpression(expression, templateParserContext);
    }
}
