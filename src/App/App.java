package app;
import java.util.Scanner;

import clients.DatabaseClient;
import models.FTPData;
import repository.DatabaseClientRepository;

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
        DatabaseClientRepository dRepository = new DatabaseClient();
        dRepository.addStudent("Boris");
    }
}
