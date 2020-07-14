package edu.au.cc.gallery.ui;

import edu.au.cc.gallery.data.*;

import static spark.Spark.*;
import spark.Response;

import spark.ModelAndView;

import edu.au.cc.gallery.data.Postgres;
import edu.au.cc.gallery.data.UserDAO;
import edu.au.cc.gallery.data.User;

import java.io.BufferedReader;
import java.io.FileReader;

import spark.template.handlebars.HandlebarsTemplateEngine;


public class App {

  public static void main(String[] args) throws Exception {
    // Get Jetty Port
    String portString = System.getenv("JETTY_PORT");

    String pg_host = System.getenv("PG_HOST");
    String pg_port = System.getenv("PG_PORT");
    String ig_database = System.getenv("IG_DATABASE");
    String ig_user = System.getenv("IG_USER");
    String ig_passwd = getPassword();
    String s3_image_bucket = System.getenv("S3_IMAGE_BUCKET");

    // Setup DB
    DB.setHostname(pg_host, pg_port, ig_database);
    DB.setIg_password(ig_passwd);
    DB.setIg_user(ig_user);

    // Setup S3
    S3ImageDAO.setBucketname(s3_image_bucket);

  	if (portString == null || portString.equals("")) {
  		port(5000);
  	} else {
  		port(Integer.parseInt(portString));
  	}

    new Admin().addRoutes();
    new ImageAdmin().addRoutes();
  }

  private static String getPassword() {
    if (System.getenv("IG_PASSWD_FILE") == null) {
      return System.getenv("IG_PASSWD");
    } else {
      return readPassword(System.getenv("IG_PASSWD_FILE"));
    }
  }

  private static String readPassword(String passFile){
    try {
      BufferedReader br = new BufferedReader(new FileReader(passFile));
      String result = br.readLine();
      br.close();
      return result;
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
    return "";
  }
}
