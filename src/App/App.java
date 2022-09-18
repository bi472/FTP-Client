package App;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Scanner;
import java.io.*;
import json.JsonObject;
import models.FTPData;

public class App {

    private static FTPData ftpData = new FTPData();
    private static final String FTP_TAG = "ftp://";
    private static final String DATABASE_NAME = "database.json";

    private static void inputFtpData(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать!\nВведите ссылку на FTP-сервер в виде ftp-server.ru:");
        ftpData.setFtpURL(scanner.nextLine());
        System.out.println("Хотите подключиться в активном режиме?[Y/N]:");
        if (scanner.nextLine() == "Y")
            ftpData.setPort(20);
        System.out.println("Введите имя пользователя:");
        ftpData.setUser(scanner.nextLine());
        System.out.println("Введите пароль:");
        ftpData.setPassword(scanner.nextLine());
    }

    public static void main(String[] args) throws Exception {
        inputFtpData();
    }
}
