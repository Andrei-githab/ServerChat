import java.io.*;
import java.sql.*;
public class MyLog implements loger {
    private static FileWriter Logs;
    public void printStory(String strLogM) throws Exception {
        strLogM = "MESS: " + strLogM + "\n";
        Logs = new FileWriter("log.txt", true);
        Logs.write(strLogM);
        Logs.flush();
    }

    public void printInfo(String strLogI) throws  IOException {
        strLogI = "INFO: " + strLogI + "\n";
        Logs = new FileWriter("log.txt", true);
        Logs.write(strLogI);
        Logs.flush();
    }
}
