package kr.hhplus.be.server.core.library;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;


public class SpringELParser {

	private SpringELParser() {}

	public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		ExpressionParser parser = new SpelExpressionParser();
		return parser.parseExpression(key).getValue(context, Object.class);
	}
}
