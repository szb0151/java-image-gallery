package edu.au.cc.gallery.aws;

import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;

public class S3 {

  private static final Region region = Region.US_WEST_1;
  private S3Client client;

  public void connect() {
    client = S3Client.builder().region(region).build();
  }

  public void createBucket(String bucketName) {
    CreateBucketRequest createBucketRequest = CreateBucketRequest
        .builder()
        .bucket(bucketName)
        .createBucketConfiguration(CreateBucketConfiguration.builder()
              .locationConstraint(region.id())
              .build())
        .build();
    client.createBucket(createBucketRequest);
  }

  public void putObject(String bucketName, String key, String value, String contentType) {
    PutObjectRequest por = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(contentType)
        .build();

    client.putObject(por, RequestBody.fromString(value));
  }

  public void deleteObject(String bucketName, String key) {
    DeleteObjectRequest dor = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    client.deleteObject(dor);
  }

  public String getObject(String bucketName, String key) {
    GetObjectRequest gor = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();
    try {
      ResponseInputStream<GetObjectResponse> response = client.getObject(gor);
      return new String(response.readAllBytes(), "UTF-8");
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }

    return null;
  }

  public static void demo() {
    String bucketName = "edu.au.cc.ram-image-gallery-config";
    S3 s3 = new S3();
    s3.connect();
    //s3.createBucket(bucketName);
    // s3.putObject(bucketName, "banana", "yellow");
  }

}
