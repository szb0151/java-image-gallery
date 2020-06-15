package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.Secrets;
import java.util.Scanner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.Request;
import spark.Response;

public class UserAdmin {

  private static final String dbUrl = "jdbc:postgresql://image-gallery.ckokefrtieqf.us-west-1.rds.amazonaws.com/image_gallery";
  private Connection connection;
  private static Scanner scanner = new Scanner(System.in);


  private JSONObject getSecret() {
        String s = Secrets.getSecretImageGallery();
        return new JSONObject(s);
  }

  private String getPassword(JSONObject secret) {
        return secret.getString("password");
  }

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

   public void addRoutes() {

    get("/admin", (req,res) -> admin(req, res));
    get("/admin/addUser", (req, res) -> addUserPage(req, res));
    post("/admin/addUser/add", (req, res) -> addUser(req, res));
    post("/admin/editUser", (req, res) -> editUserPage(req, res));
    post("/admin/editUser/:username/edit", (req, res) -> editUser(req, res));
    get("/admin/deleteUser", (req, res) -> deleteUserPage(req, res));
    post("/admin/deleteUser/:username/edit", (req, res) -> deleteUser(req, res));

   }

   public String editUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", req.params(":username"));
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "editUser.hbs"));
   }

   public String editUser(Request req, Response res) throws SQLException {
    UserAdmin.updateUserToDB(req.params(":username"), req.queryParams("password"), req.queryParams("fullName"));
    return "Updated user " + req.params(":username") + "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body>><p><a href=\"/admin\">Return to Users</a></p></body></html>";
   }

   public static void updateUserToDB(String username, String password, String fullName) throws SQLException {
    UserAdmin db = new UserAdmin();
    db.connect();
    String query = String.format("select * from users where username='%s'", username);
             try {
                 ResultSet rs = db.execute(query);
                 if (!rs.isBeforeFirst()) {
                         System.out.println("\nNo such user.");
                 }

                 while (rs.next()) {
                         if (password.isEmpty()) {
                          password = rs.getString(2);
                         }
                         if (fullName.isEmpty()) {
                          fullName = rs.getString(3);
                         }
                         db.execute("update users set password=?, full_name=? where username=?",
                         new String[] {password, fullName, username});
                         }
                       } catch (SQLException ex) {
                         System.err.println("\nNo such user.");
                       }

   }


   public String deleteUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
	model.put("username", req.params(":username"));
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "deleteUser.hbs"));
    }



   public String deleteUser(Request req, Response res) throws SQLException {
    UserAdmin.deleteUserInDB(req.params(":username"), req.queryParams("password"), req.queryParams("fullName"));
    return "Deleted user " + req.params(":username") + "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body>><p><a href=\"/admin\">Return to Users</a></p></body></html>";
   }

  public static void deleteUserInDB(String username, String password, String fullName) throws SQLException{
   UserAdmin db = new UserAdmin();
   db.connect();
   String query = String.format("select password, full_name from users where username='%s'", username);
   ResultSet rs = db.execute(query);

   if (!rs.isBeforeFirst()) {
     System.out.println("\nNo such user.");
     return;
   }

   try {
     db.execute("select username, password, full_name from users where username=?", new String[] {username});
     db.execute("delete from users where username=?", new String[] {username});
   } catch (SQLException ex) {
     System.err.println("\nNo such user.");
   }
  }

    public String addUser(Request req, Response res) throws SQLException {
    UserAdmin.addUserToDB(req.queryParams("username"), req.queryParams("password"), req.queryParams("fullName"));
    return "Added user " + req.queryParams("username") + "<!DOCTYPE html><html><head><meta charset=\"utf-8\"/></head><body><p><a href=\"/admin\">Return to Users</a></p></body></html>";
   }


   public String addUserPage(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "addUser.hbs"));
    }


   public static void addUserToDB(String username, String password, String fullName) throws SQLException {
    UserAdmin db = new UserAdmin();
    db.connect();
    try {
     db.execute("insert into users values(?, ?, ?)",
                new String[] {username, password, fullName});
    } catch (SQLException ex) {
      System.out.println("Error: user with " + username + " already exists.");
    }
   }




   public String admin(Request req, Response res) throws SQLException {
    List<String> userList = getAllUsers();
    Map<String, Object> model = new HashMap<String, Object>();
    model.put("users", userList);
    return new HandlebarsTemplateEngine()
                .render(new ModelAndView(model, "admin.hbs"));
   }

   public ArrayList getAllUsers() throws SQLException {
    UserAdmin db = new UserAdmin();
    db.connect();
    ArrayList<String> users = new ArrayList<String>();
    ResultSet rs = db.execute("select username from users");
    while(rs.next()) {
        users.add(rs.getString(1));
    }
    rs.close();
    db.close();
    return users;
   }






}

