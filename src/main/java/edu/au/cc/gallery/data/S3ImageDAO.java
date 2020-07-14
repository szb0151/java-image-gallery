package edu.au.cc.gallery.data;

import java.util.List;
import java.util.ArrayList;
import edu.au.cc.gallery.aws.*;
import edu.au.cc.gallery.ui.*;

public class S3ImageDAO implements ImageDAO {
    private static String bucketName;

    public static void setBucketname(String bucket_name) {
        bucketName = bucket_name;
    }

    @Override
    public List<Image> getImages(User user) throws Exception {
        S3 s3 = new S3();
        List<Image> images = new ArrayList<>();
        try {
          s3.connect();
          List<String> uuids = Admin.getUserDAO().getImageIds(user);
          for (String uuid: uuids) {
            String imageData = s3.getObject(bucketName, uuid);
            Image image = new Image(user, uuid, imageData);
            images.add(image);
          }

        } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
        }

        return images;
    }

    @Override
    public Image getImage(User user, String uuid) throws Exception {
        S3 s3 = new S3();
        try {
            s3.connect();
            String imageData = s3.getObject(bucketName, uuid);
            Image image = new Image(user, uuid, imageData);
            return image;

        } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
        }

        return null;
    }

    @Override
    public void addImage(User user, Image image) throws Exception {
        S3 s3 = new S3();
        if (user == null || image == null)
            return;
        try {
            s3.connect();
            if (Admin.getUserDAO().getUserByUsername(user.getUsername()) != null) {
                s3.putObject(bucketName, image.getId(), image.getImageData(), "image/image");
                Admin.getUserDAO().addImage(user, image);
            }
        } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(User user, Image image) throws Exception {
        S3 s3 = new S3();
        System.out.println("***** S3ImageDAO deleteImage() " + image.getId() + " *****");
        if (user == null || image == null)
            return;
        try {
            s3.connect();
            if (Admin.getUserDAO().getUserByUsername(user.getUsername()) != null) {
                s3.deleteObject(bucketName, image.getId());
                Admin.getUserDAO().deleteImage(user, image);
            }

        } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
        }
    }
}
