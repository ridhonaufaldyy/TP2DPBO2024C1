import java.sql.*;

public class Database {

    private Connection connection;
    private Statement statement;

    // constructor
    public Database(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dpbo2", "root", "");
            statement = connection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // digunakan untuk SElECT
    public ResultSet selectQuery(String sql)
    {
        try{
            statement.executeQuery(sql);
            return statement.getResultSet();
        } catch (SQLException e){
            throw new RuntimeException (e);
        }
    }
    // gunakan untuk insert,update, dan delete
    public int upToNow (String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // getter
    public Statement getStatement()
    {
        return statement;
    }
}
