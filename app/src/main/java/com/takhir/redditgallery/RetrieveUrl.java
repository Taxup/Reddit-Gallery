package com.takhir.redditgallery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.takhir.redditgallery.MainActivity.urlsList;

public class RetrieveUrl {

    static void retrieveImageUrlsAndAddToArrayList(String[] entries) {
        String domain = "https://i.redd.it/";

        for (String entry : entries) {

            Matcher matcher = Pattern.compile(domain).matcher(entry);

            if (matcher.find()) {
                String result = entry.substring(matcher.end()).trim();

                result = result.substring(0, 17);

                urlsList.add(domain + result);
            }
        }
    }

    static void retrieveGifUrlsAndAddToArrayList(String[] entries) {
        String domain = "https://www.redgifs.com/watch/";

        for (String entry : entries) {

            Matcher matcher = Pattern.compile(domain).matcher(entry);

            if (matcher.find()) {
                String result = entry.substring(matcher.end()).trim();

                result = result.split("\"")[0];
                System.out.println(result);

                if (!result.isEmpty()) {
                    urlsList.add(domain + result);
                }
            }
        }
    }
}
