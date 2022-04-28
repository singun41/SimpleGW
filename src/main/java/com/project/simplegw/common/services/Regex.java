package com.project.simplegw.common.services;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static boolean isUrl(String urlStr) {
		// 한글 인코딩된 URL의 경우 이 정규식을 통과하지 못한다.
        Pattern p = Pattern.compile("^(?:https?:\\/\\/)?(?:www\\.)?[a-zA-Z0-9./]+$");
        Matcher m = p.matcher(urlStr);
        if  (m.matches()) return true;
        URL u = null;
        try {
            u = new URL(urlStr);
        } catch (MalformedURLException e) {
            return false;
        }
        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
