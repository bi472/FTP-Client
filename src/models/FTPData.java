package models;

public class FTPData {
    private String ftpURL; 
    private int port = 22;
    
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    public void setFtpURL(String ftpURL) {
        this.ftpURL = ftpURL;
    }
    public String getFtpURL() {
        return ftpURL;
    }
}
