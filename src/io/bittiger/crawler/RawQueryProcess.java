package io.bittiger.crawler;

import io.bittiger.ad.Ad;

import java.io.*;
import java.util.List;

/**
 * Created by wenbi on 2017/5/7.
 */
public class RawQueryProcess {

    public void generateSubQuery(String rawQueryPath, String outputFilePath) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFilePath, true);
        BufferedWriter out = new BufferedWriter(fileWriter);
        try (BufferedReader br = new BufferedReader(new FileReader(rawQueryPath))) {
            String line;
            while ((line = br.readLine().trim()) != null) {
                if (line.isEmpty()) {
                    out.write('\n');
                    continue;
                }
                //System.out.println(line);
                String[] fields = line.split(",\\s+");
                String query = fields[0].trim();
                String[] queryStr = query.split("\\s+");
                if (queryStr.length >= 3) {
                    for (int i = 2;i < queryStr.length; i++) {
                        StringBuffer sb = new StringBuffer();
                        for (int j = 0;j <= queryStr.length - i;j++) {
                            sb.append(queryStr[j] + " ");
                        }
                        //写入output文件中
                        out.write(sb.toString().trim());
                        out.write('\n');
                    }
                }
            }
            out.close();
        }
    }
}
