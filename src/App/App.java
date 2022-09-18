package App;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Scanner;

import Clients.FtpClient;

import java.io.*;
import json.JsonObject;
import models.FTPData;
import repository.FtpClientRepository;

public class App {

    private static FTPData ftpData = new FTPData();

    private static void inputFtpData(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать!\nВведите ссылку на FTP-сервер в виде ftp-server.ru:");
        // ftpData.setFtpURL(scanner.nextLine());
        ftpData.setFtpURL("ftp272793.ispsite.ru");
        System.out.println("Хотите подключиться в активном режиме?[Y/N]:");
        // if (scanner.nextLine() == "Y")
        ftpData.setPort(20);
        System.out.println("Введите имя пользователя:");
        // ftpData.setUser(scanner.nextLine());
        ftpData.setUser("u272793");
        System.out.println("Введите пароль:");
        // ftpData.setPassword(scanner.nextLine());
        ftpData.setPassword("Fgo7kux1EfbF");
    }

    public static void main(String[] args) throws Exception {
        inputFtpData();
    }
}
