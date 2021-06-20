import java.net.URL;
import java.net.URL.*;
import java.net.URLConnection;
import java.net.HttpURLConnection.*;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

URL url = null;
HttpURLConnection http;

String token = "AQBpMukXw-s0Q6XglUMuhivANTtKNK9NjAEaqg_hTRJdP_4mqfb5n_YwMAjIH5CTOsTPXFFW7zZck9Zht6A8ZiQHBAsQ1ahD08aMRm6xIW0GE3W49u4bxjBVMjtEqEb_e86SyDbG-CN5GUJYJRJg0LZxTZHmZaVdxhMOH2WaPxXmH0TNghMEjBaoqK5R5ej_FaDRWahRMpfu9CDKs8-4";

void beginHTTP() {
  
  try {
    url = new URL("https://api.spotify.com/v1/me/player/currently-playing?market=ES&additional_types=episode");
  } 
  catch (Exception e) {
  }
}

JSONObject answer;


void requestHTTP() {
  try {

    try {
      http = (HttpURLConnection)url.openConnection();
    } 
    catch (Exception e) {
      println("URL Exception");
    }

    http.setRequestProperty("Accept", "application/json");
    http.setRequestProperty("Content-Type", "application/json");
    http.setRequestProperty("Authorization", "Bearer " + token);
    http.setDoInput(true);

    is = http.getInputStream();

    try {
      InputStreamReader isr = null;
      BufferedReader br = null;
      StringBuilder sb = new StringBuilder();
      String content;
      try {
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        while ((content = br.readLine()) != null) {
          sb.append(content);
        }
      } 
      catch (IOException ioe) {
        System.out.println("IO Exception occurred");
        ioe.printStackTrace();
      } 
      finally {
        isr.close();
        br.close();
      }
      String mystring = sb.toString();


      //println("mystring");


      //  System.out.println(http.getResponseCode() + " " + http.getResponseMessage() + " " + http.getContent());
      if (http.getResponseCode() != 200) last_check = millis()-check_amount;
      answer = parseJSONObject(mystring);
      done = true;
    } 
    catch(Exception e) {
    }
    http.disconnect();
  } 
  catch(Exception e) {
    println("Request ", e);
  }
}
