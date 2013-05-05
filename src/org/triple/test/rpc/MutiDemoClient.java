package org.triple.test.rpc;


public class MutiDemoClient {

	public static void main(String[] args) {

		String[] paths = {
				"triple://127.0.0.1:20890?weight=100&retrytime=2",
				"triple://127.0.0.1:20891?weight=100&retrytime=2",
				"triple://127.0.0.1:20892?weight=100&retrytime=2",
				"triple://127.0.0.1:20893?weight=100&retrytime=2"
		};

		DemoService remoteService = new MutiConsumer<DemoService>(paths).getService(DemoService.class);

		String[] keyWords = { "found", "catch", "catched", "know", "teach", "readyEat", "taste" };
		for (String key : keyWords) {
			System.out.println(remoteService.getWords(key));
		}

	}
}
