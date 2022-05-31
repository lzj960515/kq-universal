package com.kqinfo.universal.workflow.parser;

import cn.hutool.json.JSONObject;
import com.kqinfo.universal.workflow.model.EndNode;
import com.kqinfo.universal.workflow.model.WorkNode;
import org.springframework.stereotype.Component;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class EndNodeParser extends NodeParser {

	@Override
	protected WorkNode newNode() {
		return new EndNode();
	}

	@Override
	protected void parse(JSONObject nodeJson, WorkNode workNode) {

	}

}
