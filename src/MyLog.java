import java.io.*;
import java.sql.*;
public class MyLog implements loger {
    private static FileWriter Logs;
    public void printStory(String strLogM) throws IOException {
        Logs = new FileWriter("log.txt", true);
        Logs.write("MESS: " + strLogM + "\n");
        Logs.flush();
    }

    public void printInfo(String strLogI) throws IOException {
        Logs = new FileWriter("log.txt", true);
        Logs.write("INFO: " + strLogI + "\n");
        Logs.flush();
    }
}
