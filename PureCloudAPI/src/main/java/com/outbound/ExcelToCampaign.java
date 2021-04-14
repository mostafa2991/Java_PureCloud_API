/**package com.outbound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL; 
//import org.json.JSONObject;
/**
 * @author Created by M_ElHagez on Apr 11, 2021 
 
public class ExcelToCampaign {

	public void readExcel() throws IOException{
//		read excel file 
		FileInputStream file = new FileInputStream("test.xlsx");
//		get a campaign name with the same file name 
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		XSSFRow header_row = sheet.getRow(0);
		for (Cell cell : header_row) {
			String headerValue = cell.getStringCellValue();
			System.out.println(headerValue);
	}
		file.close();
		workbook.close();
	}
	
	public static void call_me() throws Exception {
	     String url = "http://api.ipinfodb.com/v3/ip-city/?key=d64fcfdfacc213c7ddf4ef911dfe97b55e4696be3532bf8302876c09ebd06b&ip=74.125.45.100&format=json";
	     URL obj = new URL(url);
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();
	     System.out.println("\nSending 'GET' request to URL : " + url);
	     System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(
	             new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     }
	     in.close();
	     //print in String
	     System.out.println(response.toString());
	
	}
	public static void main(String[] args)  {
		try {
			ExcelToCampaign.call_me();
	        } catch (Exception e) {
	         e.printStackTrace();
	       }
		
	}
	
}
*/
