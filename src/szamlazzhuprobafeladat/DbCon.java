package szamlazzhuprobafeladat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbCon {


private String connectionUrl = "jdbc:mysql://sql11.freemysqlhosting.net/sql11208269";
  private String uname= "sql11208269";
  private String pass= "8BI4vcDqhd";
  private PreparedStatement statement;
	
	
public Connection getConnection()  {
		
  try {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    Connection connection = DriverManager.getConnection(connectionUrl, uname, pass);
    System.out.println("Connection established.");
    return connection;
  } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
    e.printStackTrace();
    return null;
  }
}
	
public void closeConnection(Connection connection) {
  try {
    connection.close();
    System.out.println("Connection closed at request.");
  } catch (SQLException e) {
    e.printStackTrace();
    }
  }

}
