//Music Thingy
import ddf.minim.*;


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

void setup() {
  fullScreen();

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

void draw() {
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
            if (lyrics.getJSONObject(i).getInt("seconds") < pm*0.001-1) {
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




void dispose() {
  println("Good bye");
  //http.disconnect();
}


void keyPressed() {
  if (key == 'a') showQR = !showQR;
}
