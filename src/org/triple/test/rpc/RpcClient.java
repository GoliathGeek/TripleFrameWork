package org.triple.test.rpc;

public class RpcClient {
	public static void main(String[] args){
		
		String[] keyWords = {"found","catch","catched","know","teach","readyEat","taste"};
		
		RpcTestService rpcTestService = new RpcTestService();
		
		for(String key : keyWords){
			System.out.println(rpcTestService.getWords(key));
		}
		
	}
}
