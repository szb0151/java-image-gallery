package edu.au.cc.gallery.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Scanner;


public class UserAdmin {

  private static final String dbUrl = "jdbc:postgresql://database-m2.ckokefrtieqf.us-west-1.rds.amazonaws.com/user_admin";
  private Connection connection;

  private String getPassword() {
    try {
      BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/.sql-passwd"));
      String result = br.readLine();
      return result;
    } catch (IOException ex) {
      System.err.println("Error opening password file. Make sure .sql-passwd exists and contains your SQL password.");
      System.exit(1);
    }
    return null;
  }

  public void connect() throws SQLException {
    try {
      Class.forName("org.postgresql.Driver");
      connection = DriverManager.getConnection(dbUrl, "visitor1", getPassword());
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  public ResultSet execute(String query) throws SQLException {
    PreparedStatement stmt = connection.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();
    return rs;
  }

  public void execute(String query, String[] values) throws SQLException {
    PreparedStatement stmt = connection.prepareStatement(query);
    for (int i = 0; i < values.length; i++) {
      stmt.setString(i + 1, values[i]);
    }
    stmt.execute();
  }

  public void close() throws SQLException {
    connection.close();
  }

  public void printMenu() {
    System.out.println("\n1)  List users");
    System.out.println("2)  Add user");
    System.out.println("3)  Edit user");
    System.out.println("4)  Delete user");
    System.out.println("5)  Quit \n");
    System.out.print("Enter command> ");
  }

  public void listUsers(UserAdmin db) throws SQLException {
    ResultSet rs = db.execute("select username, password, full_name from users");
    System.out.println();
    // Establish formatting
    System.out.format("%-15s%-15s%-15s\n", "username", "password", "full name");

    System.out.println("----------------------------------------------");
    while(rs.next()) {
	System.out.format("%-15s%-15s%-15s\n", rs.getString(1),
	rs.getString(2), rs.getString(3));
    }
    rs.close();
  }

  public void addUser(UserAdmin db, String username, String password, String fullName) 
		throws SQLException {
	 try {
                db.execute("insert into users values(?, ?, ?)",
                new String[] {username, password, fullName});
            } catch (SQLException ex) {
                System.err.println("\nError: user with username " +
                username + " already exists");
            }
  }

  public void deleteUser(UserAdmin db, String username, String delete) 
		throws SQLException {
            if (delete.equals("yes")) {
              db.execute("delete from users where username=?", new String[] {username});
              System.out.println("\nDeleted.");
            } else if (delete.equals("no")) {
              return;
            } else {
              System.out.println("Please enter 'yes' or 'no'");
              return;
            }
  }

  public static void accessDB() throws SQLException {
    UserAdmin db = new UserAdmin();
    db.connect();

    int command = -1;
    Scanner scanner = new Scanner(System.in);
    String username = "";
    String password = "";
    String fullName = "";

    while (command != 5) {
      db.printMenu();
      command = scanner.nextInt();
      scanner.nextLine();
      switch (command) {
          case 1:
	    // List users
	    db.listUsers(db);
            break;

          case 2:
	    // Add user
	    System.out.print("Username> ");
            username = scanner.nextLine();
            System.out.print("Password> ");
            password = scanner.nextLine();
            System.out.print("Full name> ");
            fullName = scanner.nextLine();

	    db.addUser(db, username, password, fullName);
            break;

          case 3:
	    // Edit user
		System.out.print("Username to edit> ");
            	username = scanner.nextLine();
  	        String fullQuery = "update users";
            	System.out.print("New password (press enter to keep current)> ");
            	password = scanner.nextLine();
	    	if (!password.isEmpty()) {
	  		fullQuery += " set password=?";
	    	}
            	System.out.print("New full name (press enter to keep current)> ");
            	fullName = scanner.nextLine();
	    	if (!password.isEmpty() && !fullName.isEmpty()) {
                	fullQuery += ", full_name=? where username=?";
            	} else if(password.isEmpty() && !fullName.isEmpty()) {
			fullQuery += " set full_name=? where username=?";
	    	}
	    	db.execute(fullQuery, new String[] {password, fullName, username});
          	break;

          case 4:
	    // Delete user
            System.out.print("Enter username to delete> ");
            username = scanner.nextLine();
	    System.out.print("Are you sure that you want to delete " + username + "? ");
            String delete = scanner.nextLine().toLowerCase();

	    db.deleteUser(db, username, delete);
            break;

          case 5:
            System.out.println("Bye.\n");
            break;
   
          default:
            break;

      }
    }
    // Close DB
    scanner.close();
    db.close();
  }
}

