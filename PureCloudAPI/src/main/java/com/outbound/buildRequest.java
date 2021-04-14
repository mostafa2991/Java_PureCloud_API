package com.outbound;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Created by M_ElHagez on Apr 11, 2021
 */
public class buildRequest {
//	upload the csv file
	private static final String HTTPS_UPLOAD_CSV_FILE = "https://apps.mypurecloud.ie/uploads/v2/contactlist";
//	create the contact list
	private static final String HTTPS_CREATE_CONTACTLIST = "https://api.mypurecloud.ie/api/v2/outbound/contactlists";
	// use to get the token
	private static final String PASSWORD = "YfsbBEADsRPcHViRJIXl_WuKo7ZCyMamwNwMvgzN2xY";
	private static final String USER_NAME = "6e75d068-f42e-4826-a02a-88ddd0fd9b03";
	private static final String HTTPS_TOKEN = "https://login.mypurecloud.ie/oauth/token";
//	use to check for files
	private static final String CSV_PATH = "E:/Converters/CSV Files";
	private static String token;

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
					System.out.println("no Csv files in the directory");
				} else {
					return files;
				}
			}
		} finally {
//			add code
		}
		return null;
	}

	private static void uploadFiles() {
		try {
			File[] files = CheckCSVFiles();
			for (File file : files) {
				String fileNameCSV = file.getName();
				if (fileNameCSV.contains(".csv")) {
//					if we have files ....> starting to upload 
//					1st get tocken
			        getToken();
//					2nd create contact list with the same name 
     			    String contactlistId = createContactlist( token, file);
//				    System.out.println(contactlistId);
//     			    assign the contact list to a campaign
     			    
//				    3rd upload the csv file
					uploadCsvFile(token,contactlistId);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished ......");
	}

	private static String getToken() {
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

	private static String getOutboundCampaigns(String tocken) throws IOException {
		String campaignName = null;
		// Sending get request
		URL url = new URL("https://api.mypurecloud.ie/api/v2/outbound/campaigns");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestProperty("Authorization", "Bearer " + tocken);
		// e.g. bearer token=
		// eyJhbGciOiXXXzUxMiJ9.eyJzdWIiOiPyc2hhcm1hQHBsdW1zbGljZS5jb206OjE6OjkwIiwiZXhwIjoxNTM3MzQyNTIxLCJpYXQiOjE1MzY3Mzc3MjF9.O33zP2l_0eDNfcqSQz29jUGJC-_THYsXllrmkFnk85dNRbAw66dyEKBP5dVcFUuNTA8zhA83kk3Y41_qZYx43T

		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output;

		StringBuffer response = new StringBuffer();
		while ((output = in.readLine()) != null) {
			response.append(output);
		}
		in.close();
		// printing result from response
		System.out.println("Response:-" + response.toString());
		return campaignName;
	}

	@SuppressWarnings("unchecked")
	private static JSONObject createJsonBodyToCreateContactList(File file) {
		try {
//			read csv file and get file name and header
			String fileName = file.getName().replace(".csv", "");
//			start with json
			JSONObject parent = new JSONObject();
			JSONArray columnNames = new JSONArray();
			JSONArray phoneColumns = new JSONArray();
			JSONObject columnName = new JSONObject();
			parent.put("name", fileName);
//			String fileName2 = "hrdf.csv";
//			CSVReader reader = new CSVReader(new FileReader(fileName2 ));
			CSVReader reader = new CSVReader(new FileReader(file));

			// if the first line is the header
			String[] header = reader.readNext();
			for (int i = 0; i < header.length; i++) {
				
				columnNames.add(header[i].trim());
				System.out.println(header[i]);
			}
			parent.put("columnNames", columnNames);
			columnName.put("columnName", "Phone");
			columnName.put("type", "Cell");
			phoneColumns.add(columnName);
			parent.put("phoneColumns", phoneColumns);
			System.out.println(parent);
			return parent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	private static String createContactlist(String token, File file) {
		String contactListId = null;
		StringBuilder response = null;
		try {
			URL url = new URL(HTTPS_CREATE_CONTACTLIST);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestMethod("POST");
//		        create json body
			JSONObject contactListBody = createJsonBodyToCreateContactList(file);
//			attach the body to the request
			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = contactListBody.toString().getBytes("utf-8");
//				System.out.println(input[0]+input[1]);
				os.write(input, 0, input.length);
			}
//           get the response
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
				response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
//				get contact list id from response
				JSONObject object = (JSONObject) JSONValue.parse(response.toString());
				contactListId = (String) object.get("id");
//				System.out.println(response.toString());
//				System.out.println(contactListId);
				return contactListId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contactListId;

	}

	public static void uploadCsvFile(String token ,String contactlistId) {
		try {
			System.out.println("start upload the csv file");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost uploadFile = new HttpPost(HTTPS_UPLOAD_CSV_FILE);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("id", contactlistId, ContentType.TEXT_PLAIN);
			builder.addTextBody("fileType", "contactlist", ContentType.TEXT_PLAIN);
			// This attaches the file to the POST:
			File f = new File("hrdf.csv");
			builder.addBinaryBody("file", new FileInputStream(f), ContentType.APPLICATION_OCTET_STREAM, f.getName());
			uploadFile.setHeader("Authorization",
					"Basic pz6bb2y8ujkNvoUrrsGjxOqZGSyxgbMZ34EMEgkzkQtojbgTe1Jf5V-SRwLdsuxIAFV8_T7JQSxOcyVNboKvIw");
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			CloseableHttpResponse response = httpClient.execute(uploadFile);
     		HttpEntity responseEntity = response.getEntity();
			System.out.println("the file has been successfully uploaded");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
