/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.au.cc.gallery;

import edu.au.cc.gallery.tools.UserAdmin;
import edu.au.cc.gallery.tools.Secrets;
import java.util.Scanner;

import static spark.Spark.*;

public class App {

  public static void main(String[] args) throws Exception {
        UserAdmin.accessDB();

	//port(5000);
	//get("/hello", (req,res) -> "Hello World");
  }
}
