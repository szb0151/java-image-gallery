package edu.au.cc.gallery.data;

public class S3ImageFactory {
    public static ImageDAO getImageDAO() { return new S3ImageDAO();}
}
