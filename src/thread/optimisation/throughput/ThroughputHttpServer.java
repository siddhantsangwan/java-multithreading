package thread.optimisation.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThroughputHttpServer {
    private static final String INPUT_FILE = "./resources/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 8;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Path.of(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        // create a server listening on port 8000
        // backlog is the queue size, 0 cause we want our own thread pool queue
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {
        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            if(!keyValue[0].toLowerCase().equals("word")) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }
            Long count = countWord(keyValue[1]);
            // byte[] response = Long.toString(count).getBytes();
            byte[] response = count.toString().getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private Long countWord(String word) {
            Long count = 0l;
            int index = 0;
            while(index >= 0) {
                index = text.indexOf(word, index);
                if(index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }
}
