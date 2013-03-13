package org.triple.test.extension;

import org.triple.common.extension.AdaptiveAnalyst;

public class ParamAnalyst implements AdaptiveAnalyst {

	@Override
	public String getExtensionKey(Object arg) {
		Param param = (Param) arg;
		if ("a".equals(param.getStrInfo())) {
			return "ifacewrapa";
		} else if ("b".equals(param.getStrInfo())) {
			return "ifacewrapb";
		}
		return "ifacewrapdefault";
	}

}
