import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        connectToFTP();
    }   

    private static void connectToFTP() throws IOException {
        String ftpUrl = "ftp://u272443:AhDJUfkiO0KO@ftp272443.ispsite.ru/123.txt";
        
        try{
        URLConnection urlConnection = new URL(ftpUrl).openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        Files.copy(inputStream, new File("downloaded_buz.txt").toPath());
        inputStream.close();
        }
        catch(Exception ex){
            System.out.println(ex.toString());
        }
    }
}
