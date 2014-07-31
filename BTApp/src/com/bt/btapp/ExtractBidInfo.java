package com.bt.btapp;

public class ExtractBidInfo {
	private String Response = null;
	private String productName = null;
	private String logisticsInfo = null;
	private String uidInfo = null;
	ExtractBidInfo(String Response)
	{
		this.Response = Response;
	}
	public String getProductName()
	{
		return productName;
	}
	public String getLogisticsInfo()
	{
		return logisticsInfo;
	}
	public String getUidInfo()
	{
		return uidInfo;
	}
	public void handle()
	{
		String []token1 = Response.split(";");
		String []token2 = token1[0].split(",");
		if(token1.length==2)
		{
			logisticsInfo = token1[1];
		}
		uidInfo = "epc: "+token2[1]+"\n" +"product time: "+token2[2] +"\n"
					+"recall: "+token2[3]+"\n"+"product name: "+token2[4]+"\n"
					+"expire time: "+token2[5]+"\n"+"company name: "+token2[6]+"\n"
					+"company address: "+token2[7]+"\n";
		productName = token2[4];
	}
}
