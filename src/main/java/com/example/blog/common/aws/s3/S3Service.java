package com.example.blog.common.aws.s3;

import com.example.blog.auth.service.PrincipalMember;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

  private final S3Presigner presigner;

  @Value("${app.aws.s3.bucket}")
  private String bucket;
  @Value("${cloud.aws.presigned-url-expiration}")
  private Long presignExpiration;

  public Map<String, Object> generateProfilePresignedUploadUrl(
      String originalFileName, String contentType, PrincipalMember member
  ){

    String key = "uploads/profile/%s-%s".formatted(member.getMember().getId(), originalFileName);

    PutObjectRequest objectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(contentType)
        .build();

    PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(
        r -> r.signatureDuration(Duration.ofSeconds(presignExpiration))
            .putObjectRequest(objectRequest)
    );

    URL presignedUrl = presignedRequest.url();

    Map<String, Object> result = new HashMap<>();
    result.put("url", presignedUrl.toString());
    result.put("method", "PUT");
    result.put("key", key);
    result.put("requiredHeaders", Map.of("Content-Type", contentType));
    return result;
  }

}
