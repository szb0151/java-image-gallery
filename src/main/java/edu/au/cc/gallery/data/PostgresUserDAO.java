package edu.au.cc.gallery.data;

import java.util.List;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUserDAO implements UserDAO {
  private DB connection;

  public PostgresUserDAO() throws SQLException{
    connection = new DB();
    connection.connect();
  }
  public List<User> getUsers() throws SQLException {
    List<User> result = new ArrayList<>();
    ResultSet rs = connection.executeQuery("select username, password, full_name from users");
    while (rs.next()) {
      result.add(new User(rs.getString(1), rs.getString(2), rs.getString(3)));
    }
    rs.close();
    return result;
  }

  public User getUserByUsername(String username) throws SQLException {
    ResultSet rs = connection.executeQuery("select username,password,full_name from users where username=?",
                                      new String[] {username});
    if (rs.next()) {
      return new User(rs.getString(1), rs.getString(2), rs.getString(3));
    }
    rs.close();
    return null;
  }

  public void addUser(User u) throws SQLException {
    connection.execute("insert into users(username, password, full_name) values (?, ?, ?)",
                       new String[] {u.getUsername(), u.getPassword(), u.getFullName()});
  }

  public void editUser(String password, String fullName, String username) throws SQLException {
    connection.execute("update users set password=?, full_name=? where username=?",
                       new String[] {password, fullName, username});
  }

  public void deleteUser(String username) throws SQLException {
    connection.execute("delete from users where username=?", new String[] {username});
  }

  public void addImage(User u, Image i) throws SQLException {
    connection.execute("insert into images(imageid, username) values (?,?)",
            new String[]{i.getId(), u.getUsername()});
  }

  public List<String> getImageIds(User u) throws SQLException {
    List<String> ids = new ArrayList<>();
    ResultSet rs = connection.executeQuery("select imageid from images where username=?",
            new String[] { u.getUsername()});
    while (rs.next()) {
        ids.add(rs.getString(1));
    }

    return ids;
  }

  public void deleteImage(User u, Image i) throws SQLException {
      connection.execute("delete from images where username=? and imageid=?",
              new String[] {u.getUsername(), i.getId()});
  }


}
