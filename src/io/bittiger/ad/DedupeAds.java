package io.bittiger.ad;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wenbi on 2017/5/7.
 */
public class DedupeAds {

    public static String cleanData (String input) throws Exception{
        String StringOfTokens;
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        char[] addStopWords = {'.', ',', '"', '\'', '?', ':', ';', '!', '(', ')', '[', ']', '{', '}', '&', '/', '-', '+', '*', '|'};
        stopWords.add(addStopWords);
        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_40, new StringReader(input.trim()));
        tokenStream = new StopFilter(Version.LUCENE_40, tokenStream, stopWords);
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term + " ");
        }
        return sb.toString().trim();
    }

    public static void main (String[] args) throws IOException {
        String input_file = args[1];
        String output_file = args[2];
        HashMap<String, Integer> url_set = new HashMap<String, Integer>();
        int id = 2000;
        FileWriter fileWriter = new FileWriter(output_file, true);
        BufferedWriter out = new BufferedWriter(fileWriter);
        try (BufferedReader br = new BufferedReader(new FileReader(input_file))) {
            String line;
            while ((line = br.readLine()) != null) {//process each line
                JSONObject obj = new JSONObject(line);
                String detail_url = obj.getString("detail_url");
                if (obj.getString("detail_url") != null && obj.getString("title") != null && url_set.get(obj.getString("detail_url")) != null) {
                    obj.put("query", cleanData(obj.getString("query")));
                    obj.put("adId", id);
                    if (obj.getDouble("price") == 0.0) {
                        obj.put("price", ThreadLocalRandom.current().nextInt(30, 481));
                    }
                    obj.put("keyWords", cleanData(obj.getString("title")));
                    id++;
                    out.write(obj.toString());
                    out.write('\n');
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.close();
    }
}
