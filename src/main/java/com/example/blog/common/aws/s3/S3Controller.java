package com.example.blog.common.aws.s3;


import com.example.blog.auth.service.PrincipalMember;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3Controller {


  private final S3Service s3Service;

  @PostMapping("/presign")
  public ResponseEntity<Map<String,Object>> presign(
      @RequestBody PresignRequestDto request,
      @AuthenticationPrincipal PrincipalMember member
  ){

    return ResponseEntity.ok(
        s3Service.generateProfilePresignedUploadUrl(request.fileName(), request.contentType(), member)
    );
  }

}
