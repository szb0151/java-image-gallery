package edu.au.cc.gallery.data;

import java.util.List;

public interface ImageDAO {

    /*
     * Returns the list of images for a specified user
     */
    List<Image> getImages(User user) throws Exception;

    /*
     * Returns the image (if it exists)
     */
    Image getImage(User user, String name) throws Exception;

    /*
     * Add an image
     */
    void addImage(User user, Image image) throws Exception;

    /*
     * Delete an image
     */
    void deleteImage(User user, Image image) throws Exception;

}
