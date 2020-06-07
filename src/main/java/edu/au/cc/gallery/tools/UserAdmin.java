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
import java.util.List;
import java.util.ArrayList;

public class UserAdmin {

  private static final String dbUrl = "jdbc:postgresql://database-m2.ckokefrtieqf.us-west-1.rds.amazonaws.com/user_admin";
  private Connection connection;
  private static Scanner scanner = new Scanner(System.in);
  private List<User> userList = new ArrayList<User>();
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

  public void listUsers() throws SQLException {
    ResultSet rs = this.execute("select username, password, full_name from users");
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

  public void addUser() throws SQLException {

            System.out.print("Username> ");
            String username = scanner.nextLine();
            System.out.print("Password> ");
            String password = scanner.nextLine();
            System.out.print("Full name> ");
            String fullName = scanner.nextLine();

	    User user = new User(username, password, fullName);
	    userList.add(user);

	    try {
                this.execute("insert into users values(?, ?, ?)",
                new String[] {username, password, fullName});
            } catch (SQLException ex) {
                System.err.println("\nError: user with username " +
                username + " already exists");
            }
  }

  public void editUser() throws SQLException {
	    System.out.print("Username to edit> ");
            String username = scanner.nextLine();
	    User currentUser = new User();
            for (User user : userList) {
                if (user.getUsername().equals(username)) {
                        currentUser = user;
                }
            }
	    if (currentUser  == null) {
		System.err.println("\nNo such user.");
		return;
	    } else {

   	    	System.out.print("New password (press enter to keep current)> ");
            	String password = scanner.nextLine();
            	if (password.isEmpty()) {
               		password = currentUser.getPassword();
            	} else {
			currentUser.setPassword(password);
	    	}
            	System.out.print("New full name (press enter to keep current)> ");
            	String fullName = scanner.nextLine();
            	if (fullName.isEmpty()) {
               		fullName = currentUser.getFullName();
            	} else {
			currentUser.setFullName(fullName);
	    	}
	    	this.execute("update users set password=?, full_name=? where username=?",
                	new String[] {password, fullName, username});
 	 }
}

  public void deleteUser() throws SQLException {
	    System.out.print("Enter username to delete> ");
            String username = scanner.nextLine();
            System.out.print("Are you sure that you want to delete " + username + "? ");
            String delete = scanner.nextLine().toLowerCase();
            if (delete.equals("yes")) {
              this.execute("delete from users where username=?", new String[] {username});
              System.out.println("\nDeleted.");
            } else if (delete.equals("no")) {
              return;
            } else {
              System.out.println("Please enter 'yes' or 'no'");
              return;
             }
	    User currentUser = new User();
	    for (User user : userList) {
                if (user.getUsername().equals(username)) {
                        currentUser = user;
                }
            }
	    userList.remove(currentUser);
  }

  public static void accessDB() throws SQLException {
    UserAdmin db = new UserAdmin();
    db.connect();

    int command = -1;

    while (command != 5) {
      db.printMenu();
      command = scanner.nextInt();
      scanner.nextLine();
      switch (command) {
          case 1:

	    db.listUsers();
            break;

          case 2:

	    db.addUser();
            break;

          case 3:

            db.editUser();
	    break;

          case 4:

	    db.deleteUser();
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

