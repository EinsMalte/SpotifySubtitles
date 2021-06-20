

void updateToken() {
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
