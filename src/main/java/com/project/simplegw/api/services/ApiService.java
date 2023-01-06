package com.project.simplegw.api.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiService {

    public ApiService() {
        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    String call(String url) {
        log.info("url: {}", url);
        
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                conn.disconnect();
                return null;
            }

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String response = null;
                StringBuilder result = new StringBuilder();

                while((response = bufferedReader.readLine()) != null) {
                    result.append(response);
                }
                return result.toString();

            } catch(Exception e) {
                e.printStackTrace();
                log.warn("Read response exception.");
                return null;
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("call() method exception.");
            return null;
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- same package ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
