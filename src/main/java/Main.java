package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.json.JsonObject;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.http.response.JsonResponse;

public class Main {
	
	int page;
	String language;

	public Main() {
		// TODO Auto-generated constructor stub
		page = 10;
		language = "java";
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		Main main = new Main();
		if (args != null && args.length > 1) {
			main.language = args[0];
		}
		main.findRepo();
	}

	private void findRepo() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
        final Github github = new RtGithub();
        JsonResponse resp = github.entry()
                .uri().path("/search/repositories")
                .queryParam("q", "language:" + language).queryParam("per_page", 100).back()
                .fetch()
                .as(JsonResponse.class);
            List<JsonObject> items = resp.json().readObject()
                .getJsonArray("items")
                .getValuesAs(JsonObject.class);
            for (final JsonObject item : items) {
                System.out.println(
                    String.format(
                        "repository found: %s",
                        item.get("full_name").toString()
                    )
                    + " clone url:" + item.get("clone_url").toString()
                );
                clone(item.get("clone_url").toString(), item.get("name").toString());
            }
        for (int i = 1; i < page; i++) {
            resp = github.entry()
                    .uri().path("/search/repositories")
                    .queryParam("q", "language:java").queryParam("page", i+1).queryParam("per_page", 100).back()
                    .fetch()
                    .as(JsonResponse.class);
                items = resp.json().readObject()
                    .getJsonArray("items")
                    .getValuesAs(JsonObject.class);
                for (final JsonObject item : items) {
                    System.out.println(
                        String.format(
                            "repository found: %s",
                            item.get("full_name").toString()
                        )
                        + " clone url:" + item.get("clone_url").toString()
                    );
                    clone(item.get("clone_url").toString(), item.get("name").toString());
                }
			
		}

	}

	private void clone(String url, String name) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String[] cmd = { "/bin/sh", "-c", "mkdir codehouse ; git clone " + url + " codehouse/" + name };
		Process ps = Runtime.getRuntime().exec(cmd);  
        
        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));  
        StringBuffer sb = new StringBuffer();  
        String line;  
        while ((line = br.readLine()) != null) {  
            sb.append(line).append("\n");  
        }  
        String result = sb.toString();  
        System.out.println(result);  
        
        BufferedReader brError = new BufferedReader(new InputStreamReader(ps.getErrorStream(), "gb2312"));  
        String errline = null;  
        while ((errline = brError.readLine()) != null) {  
             System.out.println(errline);  
        }  
        
        int res = ps.waitFor();  
        
        if (res != 0) {
        	System.err.println("git clone error: " + res);
			return;
		}
	}

}
