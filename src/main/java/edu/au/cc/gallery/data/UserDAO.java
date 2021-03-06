package edu.au.cc.gallery.data;

import java.util.List;


public interface UserDAO {
  /*
   * @return return the (possibly empty) list of Users
   */
  List<User> getUsers() throws Exception;

  /*
   * @return user with specified username or null if no user
   */
  User getUserByUsername(String username) throws Exception;

  /*
   * Add user to the database
   */
  void addUser(User u) throws Exception;

  /*
   * Delete user from the database
   */
  void deleteUser(String username) throws Exception;

  /*
   * Edit user in the database
   */
  void editUser(String password, String fullName, String username) throws Exception;

  /*
   * Add an image for a user into the database
   */
  void addImage(User u, Image i) throws Exception;

  /*
   * Returns the list of all images for specified user
   */
  List<String> getImageIds(User u) throws Exception;

  /*
   * Deletes an image for a specified user
   */
  void deleteImage(User u, Image i) throws Exception;

}
