package com.example.multiculture_supporter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView1;
    String sourceString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView) ;
        editText = (EditText) findViewById(R.id.editText);
    }

    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                sourceString = editText.getText().toString();
                new GoogleTranslatorTask().execute(sourceString);
                break;
        }
    }


    // 번역 AsyncTask
    class GoogleTranslatorTask extends AsyncTask<String, Integer, String> {
           String URL = "https://www.googleapis.com/language/translate/v2?key=";
           String KEY = "AIzaSyAgJVUHXYJYOhctVpZgUUImw6tpbvJqs3M";
           String TARGET = "&target=ko";
           String SOURCE = "&source=en";
           String QUERY = "&q=";

        String englishString = "";
        String koreaString;

        @Override
        protected String doInBackground(String... editText) {
            englishString = editText[0];
            StringBuilder result = new StringBuilder();
            try{
                String encodedText = URLEncoder.encode(englishString, "UTF-8");
                java.net.URL url = new URL(URL+ KEY + SOURCE + TARGET + QUERY + encodedText);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                InputStream stream;
                if(conn.getResponseCode() == 200){
                    stream = conn.getInputStream();
                }else{
                    stream = conn.getErrorStream();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;

                while((line = reader.readLine()) != null){
                    result.append(line);
                }

                JsonParser parser = new JsonParser();

                JsonElement element = parser.parse(result.toString());

                if(element.isJsonObject()){
                    JsonObject obj = element.getAsJsonObject();
                    if(obj.get("error") == null){
                        koreaString = obj.get("data").getAsJsonObject().get("translations").getAsJsonArray().get(0).getAsJsonObject().get("translatedText").getAsString();
                    }
                }
                if(conn.getResponseCode() != 200){
                    Log.e("GoogleTranslatorTask", result.toString());
                }
            }catch(IOException | JsonSyntaxException ex){
                Log.e("GoogleTranslatorTask", ex.getMessage());
            }
            return koreaString;
        }

        @Override
        protected void onPreExecute() { super.onPreExecute(); }

        @Override
        protected void onProgressUpdate(Integer ...progress) { // 파일 다운로드 퍼센티지 표시 작업

        }

        @Override
        protected void onPostExecute(String result) {
            textView1.setText(result);
        }


    }
}


