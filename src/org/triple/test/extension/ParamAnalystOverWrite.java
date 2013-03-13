package org.triple.test.extension;

import org.triple.common.extension.AdaptiveAnalyst;

public class ParamAnalystOverWrite implements AdaptiveAnalyst {

	@Override
	public String getExtensionKey(Object arg) {
		Param param = (Param) arg;
		if ("b".equals(param.getStrInfo())) {
			return "ifacewrapa";
		} else if ("a".equals(param.getStrInfo())) {
			return "ifacewrapb";
		}
		return "ifacewrapdefault";
	}

}
