package edu.au.cc.gallery.data;

import edu.au.cc.gallery.aws.Secrets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DB {

  private static final String dbUrl = "jdbc:postgresql://m5-rds.ckokefrtieqf.us-west-1.rds.amazonaws.com/image_gallery";
  private Connection connection;

  public void connect() throws SQLException {
     try {
       Class.forName("org.postgresql.Driver");
       JSONObject secret = getSecret();
       connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword(secret));
     } catch (ClassNotFoundException ex) {
       ex.printStackTrace();
       System.exit(1);
     }
   }

  private JSONObject getSecret() {
        String s = Secrets.getSecretImageGallery();
        return new JSONObject(s);
  }

  private String getPassword(JSONObject secret) {
        return secret.getString("password");
  }

  public ResultSet executeQuery(String query) throws SQLException {
         PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery();
         return rs;
   }

  public ResultSet executeQuery(String query, String[] values) throws SQLException {
     PreparedStatement stmt = connection.prepareStatement(query);
     for (int i = 0; i < values.length; i++) {
       stmt.setString(i + 1, values[i]);
     }
     return stmt.executeQuery();
   }

   /*
    * Perform an update
    */
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

}
