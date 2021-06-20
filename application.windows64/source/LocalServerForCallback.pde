import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.Executors;


String[] website;


ThreadPoolExecutor threadPoolExecutor;


HttpServer server;

void connectToServer() {
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
