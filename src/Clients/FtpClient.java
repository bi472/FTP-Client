package clients;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import models.FTPData;
import repository.FtpClientRepository;

public class FtpClient implements FtpClientRepository {

    private static final String FTP_TAG = "ftp://";
    private static final String DATABASE_NAME = "database.json";
    private FTPData data;
    private String ftpUrl;

    public FtpClient(FTPData data) {
        this.data = data;
    }

    @Override
    public void putDatabase() {
        ftpUrl = FTP_TAG + data.getUser() + ":" + data.getPassword() + "@" + data.getFtpURL() + "/" + DATABASE_NAME;
        try {
            URLConnection urlConnection = new URL(this.ftpUrl).openConnection();
            OutputStream oStream = urlConnection.getOutputStream();
            Files.copy(new File(DATABASE_NAME).toPath(), oStream);
            System.out.println("Файл Json успешно скачан с FTP-сервера.");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void saveDatabase() {
        ftpUrl = FTP_TAG + data.getUser() + ":" + data.getPassword() + "@" + data.getFtpURL() + "/" + DATABASE_NAME;
        try {
            URLConnection urlConnection = new URL(this.ftpUrl).openConnection();
            InputStream iStream = urlConnection.getInputStream();
            Files.copy(iStream, new File(DATABASE_NAME).toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Файл Json успешно сохранён на FTP-сервер.");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

}
