import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
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
import com.sun.net.httpserver.HttpExchange; 
import java.util.StringJoiner; 
import java.net.URLEncoder; 
import java.nio.charset.StandardCharsets; 
import java.net.InetSocketAddress; 
import com.sun.net.httpserver.*; 
import java.util.concurrent.ThreadPoolExecutor; 
import java.util.concurrent.Executors; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SpotifySubtitles extends PApplet {

//Music Thingy



Minim minim;
AudioPlayer player;
AudioMetaData meta;

String song_name = "Goosebumps";


IntList times = new IntList();

PImage cover_image;

String title = "";
String author = "";

int duration = 0;

JSONArray lyrics;

public void setup() {
  

  connectToServer();



  /*
  JSONObject item = answer.getJSONObject("item");
   
   String u = item.getString("name");
   
   String new_u = "";
   for (int i = 0; i < u.length(); i++) {
   if (u.charAt(i) != ' ') {
   new_u += u.charAt(i);
   } else {
   if (u.charAt(i) == ' ')
   new_u += "%20";
   }
   }
   
   //lyrics = loadJSONArray("https://api.textyl.co/api/lyrics?q=" + new_u);
   //println(lyrics);
   */

  qr = loadImage("qrcode.png");
}

int last_check = -10000;
int pm = 0;
int last_pm = 0;

int check_amount = 700;

int last_refreshed_token = 0;
int refresh_token_amount = 60000;

boolean done = true;

InputStream is;


boolean showQR = false;

boolean hasToken = false;

PImage qr;

boolean playing = true;

public void draw() {
  background(0);

  try {

    if (hasToken) {
      if (!done) {
      }

      if (playing)
        pm += millis()-last_pm;
      last_pm = millis();
      if (millis()-last_check > check_amount || pm > duration-500) {
        done = false;

        last_check = millis();
        requestHTTP();
        //println(answer);
        if (answer != null) {
          JSONObject item = answer.getJSONObject("item");

          String u = item.getString("name");

          String new_u = "";
          for (int i = 0; i < u.length(); i++) {
            if (u.charAt(i) != ' ') {
              new_u += u.charAt(i);
            } else {
              new_u += "%20";
            }
          }

          try {
            if (title != item.getString("name")) {
              String[] s = loadStrings("https://api.textyl.co/api/lyrics?q=" + new_u);
              //println(s[0]);
              if (s.length > 1 || s[0].length() > 100)
                lyrics = loadJSONArray("https://api.textyl.co/api/lyrics?q=" + new_u);
              else lyrics = null;
            }
          } 
          catch (NullPointerException p) {
            lyrics = null;
            println(p);
            println("no lyrics lmao");
          }

          cover_image = loadImage(item.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url"), "png");
          pm = answer.getInt("progress_ms");

          duration = item.getInt("duration_ms");
          title = item.getString("name");
          author = item.getJSONArray("artists").getJSONObject(0).getString("name");
          playing = answer.getBoolean("is_playing");
        }
      }

      if (answer != null) {

        fill(204, 104, 0);
        rect(0, 0, width, 500);
        fill(255);
        textAlign(LEFT, TOP);
        int ts = 120;
        textSize(ts);
        while (textWidth(title) > width-700) {
          textSize(ts);
          ts--;
        }

        text(title, 500, 50);
        float title_width = textWidth(title);
        textSize(25);
        textAlign(LEFT, BOTTOM);
        text(author, 500+title_width+20, 50+ts);

        String l = "";

        //println(answer);
        if (lyrics != null) {
          for (int i = lyrics.size()-1; i >= 0; i--) {
            if (lyrics.getJSONObject(i).getInt("seconds") < pm*0.001f-1) {
              l = lyrics.getJSONObject(i).getString("lyrics");
              break;
            }
          }
        } else l = "Kein Text vorhanden D:";
        textAlign(LEFT, TOP);
        textSize(60);
        text(l, 500, 200, width-600, 200);

        //text(pm,500,400);


        textAlign(RIGHT, TOP);
        text(nf(hour(), 2) + ":" + nf(minute(), 2), width, 0);
      }
      if (cover_image != null)
        image(cover_image, 50, 50, 400, 400);

      if (showQR) {
        textSize(40);
        textAlign(CENTER, CENTER);
        text("Musik-Wünsche:", 250, 25);  
        image(qr, 50, 50, 400, 400);
      }

      noCursor();

      if (millis()-last_refreshed_token > refresh_token_amount) {
        updateToken();
        last_refreshed_token = millis();
      }
    }
  } 
  catch (Exception e) {
    fill(204, 104, 0);
    rect(0, 0, width, 500);
    fill(255);
    textSize(15);
    textAlign(LEFT,BOTTOM);
    text("Fehler: " + e, 0, height);
  }
}




public void dispose() {
  println("Good bye");
  //http.disconnect();
}


public void keyPressed() {
  if (key == 'a') showQR = !showQR;
}











URL url = null;
HttpURLConnection http;

String token = "AQBpMukXw-s0Q6XglUMuhivANTtKNK9NjAEaqg_hTRJdP_4mqfb5n_YwMAjIH5CTOsTPXFFW7zZck9Zht6A8ZiQHBAsQ1ahD08aMRm6xIW0GE3W49u4bxjBVMjtEqEb_e86SyDbG-CN5GUJYJRJg0LZxTZHmZaVdxhMOH2WaPxXmH0TNghMEjBaoqK5R5ej_FaDRWahRMpfu9CDKs8-4";

public void beginHTTP() {
  
  try {
    url = new URL("https://api.spotify.com/v1/me/player/currently-playing?market=ES&additional_types=episode");
  } 
  catch (Exception e) {
  }
}

JSONObject answer;


public void requestHTTP() {
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









private class MyHttpHandler implements HttpHandler {    

  @Override    
    public void handle(HttpExchange httpExchange) throws IOException {

    String requestParamValue=null; 

    if ("GET".equals(httpExchange.getRequestMethod())) { 

      requestParamValue = handleGetRequest(httpExchange);
    } else if ("POST".equals(httpExchange)) { 

      //requestParamValue = handlePostRequest(httpExchange);
    }  

    handleResponse(httpExchange, requestParamValue);
  }

  private String handleGetRequest(HttpExchange httpExchange) {

    return httpExchange.

      getRequestURI()

      .toString()

      .split("\\?")[1]

      .split("=")[1];
  }

  private void handleResponse(HttpExchange httpExchange, String requestParamValue)  throws  IOException {

    OutputStream outputStream = httpExchange.getResponseBody();

    StringBuilder htmlBuilder = new StringBuilder();


    htmlBuilder.append("<html>").

      append("<body>").

      append("<h1>").

      append("Hello ")

      .append(requestParamValue)

      .append("</h1>")

      .append("</body>")

      .append("</html>");

    // encode HTML content 



    String htmlResponse = requestParamValue.substring(0, requestParamValue.indexOf('&'));

    token = requestParamValue.substring(0, requestParamValue.indexOf('&'));

    println("token: " + token);

    hasToken = true;

    if (true) {

      URL url = new URL("https://accounts.spotify.com/api/token");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      http.setRequestProperty("Authorization", "Basic ZTljNjkwMGNlY2RiNDU1OWFhODFkNDIwZGMyMTY1NzU6ZjM3MGYzMTFmYWU5NDJmZDgzNWY1ZTRjNzllZmRhZmQ=");
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String data = "grant_type=authorization_code&code=" + token + "&redirect_uri=http:%2F%2Flocalhost:8888%2Fcallback";

      byte[] out = data.getBytes(StandardCharsets.UTF_8);

      OutputStream stream = http.getOutputStream();
      stream.write(out);

      System.out.println("Second: " + http.getResponseCode() + " " + http.getResponseMessage());

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


        println("Yee" + mystring);

        if (http.getResponseCode() != 200) last_check = millis()-check_amount;
        JSONObject tt = parseJSONObject(mystring);
        
        token = tt.getString("access_token");
        
        //println(token);
        
        last_refreshed_token = millis();
      } 
      catch(Exception e) {
        println("Failed token: " + e);
        http.disconnect();
      }
    }










    // this line is a must



    httpExchange.sendResponseHeaders(200, htmlResponse.length());



    outputStream.write(htmlResponse.getBytes());



    outputStream.flush();



    outputStream.close();

    
    beginHTTP();
    requestHTTP();
  }
}









String[] website;


ThreadPoolExecutor threadPoolExecutor;


HttpServer server;

public void connectToServer() {
  try {
    
    threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
    
    server = HttpServer.create(new InetSocketAddress("localhost", 8888), 0);

    server.createContext("/callback", new  MyHttpHandler());



    server.setExecutor(threadPoolExecutor);



    server.start();



    println(" Server started on port 8001");
  } 
  catch(Exception e) {
  }
  link("https://accounts.spotify.com/de/authorize?client_id=e9c6900cecdb4559aa81d420dc216575&response_type=code&redirect_uri=http:%2F%2Flocalhost:8888%2Fcallback&scope=user-read-currently-playing&state=34fFs29kd09");
}


public void updateToken() {
  try {
    if (true) {

      URL url = new URL("https://accounts.spotify.com/api/token");
      HttpURLConnection http = (HttpURLConnection)url.openConnection();
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      http.setRequestProperty("Authorization", "Basic ZTljNjkwMGNlY2RiNDU1OWFhODFkNDIwZGMyMTY1NzU6ZjM3MGYzMTFmYWU5NDJmZDgzNWY1ZTRjNzllZmRhZmQ=");
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String data = "grant_type=authorization_code&code=" + token + "&redirect_uri=http:%2F%2Flocalhost:8888%2Fcallback";

      byte[] out = data.getBytes(StandardCharsets.UTF_8);

      OutputStream stream = http.getOutputStream();
      stream.write(out);

      System.out.println("Second: " + http.getResponseCode() + " " + http.getResponseMessage());

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


        println("Yee" + mystring);

        if (http.getResponseCode() != 200) last_check = millis()-check_amount;
        JSONObject tt = parseJSONObject(mystring);

        token = tt.getString("access_token");

        //println(token);

        last_refreshed_token = millis();
      } 
      catch(Exception e) {
        println("Failed token: " + e);
        http.disconnect();
      }
    }
    println("updated token");
  } 
  catch (Exception e) {
    println("Not updated Token: " + e);
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SpotifySubtitles" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
