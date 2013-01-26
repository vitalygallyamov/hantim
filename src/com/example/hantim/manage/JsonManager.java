package com.example.hantim.manage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class JsonManager {
	
	private String url;
	
	public JsonManager(String url){
		this.url = url;
	}
	
	public String getJsonResult(){
		
		StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
	        int statusCode = statusLine.getStatusCode();
	        
	        if (statusCode == 200) {
            
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(
	        			response.getEntity().getContent(), "UTF-8"),8);
	            String line;
	            while((line = reader.readLine()) != null){
	            	builder.append(line).append("\n");;
	            }
	            
	        }else {
	        	return "";
	        }
	        
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return builder.toString();
	}
}
