package com.outbound;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Created by M_ElHagez on Apr 11, 2021
 */
public class uploadFiles extends TimerTask {
//	upload the csv file
	private static final String HTTPS_UPLOAD_CSV_FILE = "https://apps.mypurecloud.ie/uploads/v2/contactlist";
	// use to get the token
	private static final String PASSWORD = "YfsbBEADsRPcHViRJIXl_WuKo7ZCyMamwNwMvgzN2xY";
	private static final String USER_NAME = "6e75d068-f42e-4826-a02a-88ddd0fd9b03";
	private static final String HTTPS_TOKEN = "https://login.mypurecloud.ie/oauth/token";
//	use to check for files
	private static final String CSV_PATH = "E:/Converters/CSV Files";
	private static Map<String, String> contactListId = new HashMap<>();

//	static block to initalize the map 
	static {
		contactListId.put("MahmoudTask", "31553e48-53b2-47d6-85bf-765c9d3ccf14");
		contactListId.put("mostafa", "3d0a82b8-c680-4a16-8bf8-7aab73609c17");
	}

	private static File[] CheckCSVFiles() {
		File csvDir = null;
		try {
			System.out.println("Start Checking for CSV Files....");
			csvDir = new File(CSV_PATH);
			if (csvDir.exists() == false) {
				System.out.println("check the directory it's not exist.");
			} else {
				File[] files = csvDir.listFiles();
				if (files.length == 0) {
					System.out.println("no files in the directory");
				} else {
					return files;
				}
			}
		} finally {
//			add code
		}
		return null;
	}

	private static String getToken() {
		String token = null;
		try {
//			1- use http client
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(HTTPS_TOKEN);
//		    set header 
			httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
//	      set body
			httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
//		    set user & password
			UsernamePasswordCredentials creds = new UsernamePasswordCredentials(USER_NAME, PASSWORD);
			httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));
//		get the response	
			CloseableHttpResponse response = client.execute(httpPost);
			String responseString = new BasicResponseHandler().handleResponse(response);
//			System.out.println(responseString);
			JSONObject object = (JSONObject) JSONValue.parse(responseString);
			token = (String) object.get("access_token");
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	private static void uploadCSVFilesOnly(File[] files, String token) {
		try {
			for (File file : files) {
				String fileNameCSV = file.getName();
				if (fileNameCSV.contains(".csv")) {
//					upload the file
//					get contact list id
//					System.out.println(fileNameCSV);
					String contactListID = contactListId.get(fileNameCSV.replace(".csv", ""));
//					System.out.println("*******"+contactListID);
					uploadFile(token, contactListID, file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished ......");
	}

	public static void uploadFile(String token, String contactlistId, File file) {
		try {
			System.out.println("start upload the csv file");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(HTTPS_UPLOAD_CSV_FILE);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("id", contactlistId, ContentType.TEXT_PLAIN);
			builder.addTextBody("fileType", "contactlist", ContentType.TEXT_PLAIN);
			// This attaches the file to the POST:
//			File f = new File("hrdf.csv");
			builder.addBinaryBody("file", new FileInputStream(file), ContentType.APPLICATION_OCTET_STREAM,
					file.getName());
			uploadFile.setHeader("Authorization",
					"Basic pz6bb2y8ujkNvoUrrsGjxOqZGSyxgbMZ34EMEgkzkQtojbgTe1Jf5V-SRwLdsuxIAFV8_T7JQSxOcyVNboKvIw");
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			System.out.println(responseEntity);
			System.out.println("the file has been successfully uploaded");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {
		try {
//			 check files in the directory
			File[] files = CheckCSVFiles();
//			 get tocken 
			String token = getToken();
			uploadCSVFilesOnly(files, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		uploadFiles uploadfiles = new uploadFiles();
		 Timer timer = new Timer();
		 timer.scheduleAtFixedRate(uploadfiles, 0, 5*60*1000);

	}
}
