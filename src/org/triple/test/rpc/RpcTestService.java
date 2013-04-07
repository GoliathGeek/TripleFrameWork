package org.triple.test.rpc;

import java.util.HashMap;
import java.util.Map;

import org.triple.common.annotation.TripleService;

@TripleService
public class RpcTestService {
	
	private static Map<String,String> wordsMap = new HashMap<String,String>();
	
	static{
		wordsMap.put("found", "看！这里有一只落单的theonefx");
		wordsMap.put("catch", "不要惊动他，我们从后面绕过去");
		wordsMap.put("catched", "嘿！我抓到了！蹦哒的还挺给力");
		wordsMap.put("know", "你知道么，这家伙的蛋白质含量是牛肉的8倍");
		wordsMap.put("teach", "在天朝生存，这种高蛋白是必不可少了，他能提供你一天需要的卡路里");
		wordsMap.put("readyEat", "我要开始吃了，像这样扭掉头，把肠子挤出来，饿...");
		wordsMap.put("taste", "这味道有点像鸡肉");
	}
	
	public String getWords(String key){
		return wordsMap.get(key);
	}
}
