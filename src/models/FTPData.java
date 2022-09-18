package models;

public class FTPData {
    private String ftpURL; 
    private int port = 22;
    private String user;
    private String password;
    
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public void setFtpURL(String ftpURL) {
        this.ftpURL = ftpURL;
    }
    public String getFtpURL() {
        return ftpURL;
    }
}
