import javax.imageio.IIOException;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class DB {

    public LinkedList<String> story = new LinkedList<>();
    MyLog myLog = new MyLog();
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASS = "root";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/chatdb";
    private Connection connectionDB;


    public DB() {
        try {
            this.connectionDB = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASS);
            System.out.println("БД подключена");
            myLog.printInfo("БД подключена");
        } catch (SQLException sqlException) {
            System.out.println("БД SQL Exception: " + sqlException);
        } catch (IOException exception){
            System.out.println("БД myLog Exception: " + exception);
        }
    }

    public void addDB(String message) {
        try {
            String[] procmes = message.split("\\|");
            String sqlAdd = "INSERT INTO chatstory (nikuser, timemes, usmessage) VALUES (?, ?, ?);";
            PreparedStatement preparedStatementAdd = connectionDB.prepareStatement(sqlAdd);
            preparedStatementAdd.setString(1, procmes[0]);
            preparedStatementAdd.setString(2, procmes[1]);
            preparedStatementAdd.setString(3, procmes[2]);
            preparedStatementAdd.executeUpdate();
        } catch (SQLException eadd) {
            System.out.println("addDB SQLException: " + eadd);
        }
    }

    public void printChatStory(){
        try {
            Statement statement = connectionDB.createStatement();
            String sqlSelect = "SELECT * FROM chatstory;";
            ResultSet resultSet = statement.executeQuery(sqlSelect);
            while (resultSet.next()){
                story.add(resultSet.getString("nikuser") + "|" + resultSet.getString("timemes") + "|" + resultSet.getString("usmessage"));
            }

        } catch (SQLException epcs) {
            System.out.println("printChatStory SQLException: " + epcs);
        }
    }
}
