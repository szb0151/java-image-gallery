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
}
