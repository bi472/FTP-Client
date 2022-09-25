package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import clients.DatabaseClient;
import models.FTPData;
import repository.DatabaseClientRepository;

public class App {

    private static FTPData ftpData = new FTPData();
    private static final String DATABASE_NAME = "database.json";

    public static boolean validateHostname(String hostname) throws IOException{
        try
        {
            Socket socket = new Socket(hostname, 21);
            socket.close();
        }
        catch (java.net.UnknownHostException exception){
            System.out.println("Введите IP-адрес или имя хостинга.");
            return false;
        }
        catch (java.net.ConnectException exception){
            System.out.println("Превышено время ожидания ответа хоста.");
            return false;
        }
        return true;
    }

    private enum FTPCommands {
        OPTS,
        USER,
        PASS,
        HELP,
        RETR,
        NLST,
        PASV,
        STOR,
        CWD,
        DELE,
        PWD,
        LIST
    }

    private static class SocketClient {
        final String EOL = "\r\n";
        private final Socket socket;
        private final BufferedWriter serverSender;
        private final BufferedReader serverReceiver;
        private final String hostname;
        private int port;

        public SocketClient(String host, int port) throws IOException {
            this.hostname = host;
            this.port = port;
            this.socket = new Socket(this.hostname, this.port);
            serverSender = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            serverReceiver = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        }

        private void sendCommandToServer(FTPCommands command, String data) throws IOException {
            this.serverSender.write(command.name() + " " + data + this.EOL);
            this.serverSender.flush();
        }

        private void sendCommandToServer(FTPCommands command) throws IOException {
            this.serverSender.write(command.name() + this.EOL);
            this.serverSender.flush();
        }

        private String getServerLine() throws IOException {
            return serverReceiver.readLine();
        }

        public void close() {
            try {
                this.serverSender.close();
                this.serverReceiver.close();
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class FTPProtocolClient {
        private final SocketClient client;
        private final BufferedReader keyboard;
        private String username = null;

        public FTPProtocolClient(SocketClient client) {
            this.client = client;
            this.keyboard = new BufferedReader(new InputStreamReader(System.in));
        }

        public void connect() throws IOException {
            this.client.sendCommandToServer(FTPCommands.OPTS, "UTF8 ON");
        }

        public void login() throws IOException {
            String response;
            do {
                response = this.client.getServerLine();
            } while (!response.startsWith("200"));
            boolean successfulConnection;
            do {
                // Get input from user
                System.out.println("Введите имя пользователя:");
                this.username = this.keyboard.readLine();
                this.client.sendCommandToServer(FTPCommands.USER, this.username);
                response = this.client.serverReceiver.readLine();
                System.out.println(response);
                // Send the password
                System.out.println("Введите пароль:");
                String password = this.keyboard.readLine();
                this.client.sendCommandToServer(FTPCommands.PASS, password);
                response = this.client.getServerLine();
                System.out.println(response);

                successfulConnection = response.startsWith("530");
            } while (successfulConnection);
        }

        private SocketClient getPassiveConnection() throws IOException {
            this.client.sendCommandToServer(FTPCommands.PASV);
            String response;
            do{            
                response = this.client.getServerLine();
            }while(!response.startsWith("227"));

            // Get the hostname and port from the server

            String[] spaceSeparated = response.split("(,|\\.|\\)|\\()");

            int passivePort = Integer.parseInt(spaceSeparated[5]) * 256 + Integer.parseInt(spaceSeparated[6]);

            String passiveHostname = spaceSeparated[1];
            for (int i = 2; i < spaceSeparated.length - 2; i++) {
                passiveHostname += "." + spaceSeparated[i];
            }

            // Create the new SocketClient
            return new SocketClient(passiveHostname, passivePort);
        }

        public void get(String arguments) throws IOException {
            try {
                SocketClient dataSocket = getPassiveConnection();

                // send request for the file
                this.client.sendCommandToServer(FTPCommands.RETR, arguments);
                this.client.getServerLine();

                // open file stream
                FileWriter writer = new FileWriter(arguments, false);

                // write remote stream to local stream
                final int MAX_BUFFER_SIZE = 4096;
                char[] buffer = new char[MAX_BUFFER_SIZE];
                int count;

                while (true) {
                    count = dataSocket.serverReceiver.read(buffer, 0, MAX_BUFFER_SIZE);
                    if (count > 0) {
                        writer.write(buffer, 0, count);
                    } else {
                        break;
                    }
                }
                this.client.getServerLine();

                // close sockets
                dataSocket.close();
                writer.close();
            } catch (java.io.FileNotFoundException fileNotFoundException) {
                System.out.println("Такого файла не существует.\n");
            } catch (Exception exception) {
                System.out.println("Ошибка:" + exception.toString());
            }

        }

        public void put(String arguments) throws IOException {
            try {
                SocketClient dataSocket = getPassiveConnection();

                // send request for the file
                this.client.sendCommandToServer(FTPCommands.STOR, arguments);
                this.client.getServerLine();

                // open file stream
                FileReader reader = new FileReader(arguments);

                // write remote stream to local stream
                final int MAX_BUFFER_SIZE = 4096;
                char[] buffer = new char[MAX_BUFFER_SIZE];
                int count;

                while (true) {
                    count = reader.read(buffer, 0, MAX_BUFFER_SIZE);
                    if (count > 0) {
                        dataSocket.serverSender.write(buffer, 0, count);
                    } else {
                        break;
                    }
                }
                // close sockets
                dataSocket.close();
                reader.close();
                this.client.getServerLine();
            } catch (Exception exception) {
                System.out.println("Ошибка:" + exception.toString());
            }
        }

        public void disconnect() {
            System.out.println("Closing connection with: " + this.client.hostname);
            this.client.close();
        }

    }

    private static void showCommands() {
        System.out.println(
                "\nВыберите номер соответсвующей команды: \n" +
                        "1.	Получение списка студентов по имени\n" +
                        "2.	Получение информации о студенте по id\n" +
                        "3.	Добавление студента (id генерируется автоматически)\n" +
                        "4.	Удаление студента по id\n" +
                        "5.	Завершение работы\n");
    }

    private static void pressEnterToContinue() {
        System.out.print("\nPress Enter key to continue ...");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }

    private static void processCommands(FTPProtocolClient protocolClient,
            DatabaseClientRepository dRepository) {
        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        int option = 1;
        while (option != 5) {
            showCommands();
            System.out.print("Введите номер команды: ");
            try {
                option = scanner.nextInt();
                switch (option) {
                    case 1:
                        protocolClient.get(DATABASE_NAME);
                        dRepository.findAllStudents();
                        pressEnterToContinue();
                        break;
                    case 2:
                        protocolClient.get(DATABASE_NAME);
                        System.out.print("Введите id студента: ");
                        dRepository.findStudentById(scanner.nextInt());
                        pressEnterToContinue();
                        break;
                    case 3:
                        protocolClient.get(DATABASE_NAME);
                        System.out.print("Введите имя студента: ");
                        dRepository.addStudent(scanner2.nextLine());
                        protocolClient.put(DATABASE_NAME);
                        pressEnterToContinue();
                        break;
                    case 4:
                        protocolClient.get(DATABASE_NAME);
                        System.out.print("Введите id студента: ");
                        dRepository.deleteStudent(scanner.nextInt());
                        protocolClient.put(DATABASE_NAME);
                        pressEnterToContinue();
                        break;
                    case 5:
                        break;
                    default:
                        System.out.print("Введите цифру команды (1-5).");
                        pressEnterToContinue();
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
                break;
            }
        }
        scanner.close();
        scanner2.close();
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String ftpURL;
        do{
        System.out.println("Enter myftp server-name:");
        ftpURL = scanner.nextLine();
        }
        while (!validateHostname(ftpURL));
        ftpData.setFtpURL(ftpURL);
        ftpData.setPort(21);

        SocketClient socketClient = new SocketClient(ftpData.getFtpURL(), ftpData.getPort());
        FTPProtocolClient protocolClient = new FTPProtocolClient(socketClient);

        protocolClient.connect();
        protocolClient.login();

        DatabaseClientRepository dRepository = new DatabaseClient();

        processCommands(protocolClient, dRepository);

        protocolClient.disconnect();
        scanner.close();
    }
}
