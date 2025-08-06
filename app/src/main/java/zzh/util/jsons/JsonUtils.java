package zzh.util.jsons;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonUtils {
  public static String loadJSONFromAsset(Context context, String fileName) {
    StringBuilder stringBuilder = new StringBuilder();
    try {
      AssetManager assetManager = context.getAssets();
      InputStream inputStream = assetManager.open(fileName);
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      String line;
      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
      reader.close();
      inputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
      return "无法读取 JSON 文件内容";
    }
    return stringBuilder.toString();
  }
}
