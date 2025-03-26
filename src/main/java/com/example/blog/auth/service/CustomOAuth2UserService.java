package com.example.blog.auth.service;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);
    Map<String, Object> attributes = oAuth2User.getAttributes();

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    Provider provider = Provider.from(registrationId);

    String email = extractEmail(attributes, provider);
    String providerId = extractProviderId(attributes, provider);
    String pictureUrl = oAuth2User.getAttribute("picture");
    String name = oAuth2User.getAttribute("given_name").toString() + " " + oAuth2User.getAttribute("family_name").toString();

    memberRepository.findByEmail(email).orElseGet(() -> {
      return memberRepository.save(new Member(
          email,
          null,
          email.split("@")[0],
          provider,
          providerId,
          MemberStatus.ACTIVE,
          pictureUrl,
          name,
          MemberRole.USER
      ));
    });

    return new DefaultOAuth2User(Collections.emptyList(), attributes, "email");
  }

  private String extractEmail(Map<String, Object> attributes, Provider provider) {
    return switch (provider) {
      case GOOGLE -> (String) attributes.get("email");
      case KAKAO -> (String) attributes.get("email"); // TODO : 이후 형식에 맞추어 변경
      case GITHUB -> (String) attributes.get("email"); // TODO : 이후 형식에 맞추어 변경
    };
  }

  private String extractProviderId(Map<String, Object> attributes, Provider provider) {
    return switch (provider) {
      case GOOGLE -> (String) attributes.get("sub");
      case KAKAO -> (String) attributes.get("sub"); // TODO : 이후 형식에 맞추어 변경
      case GITHUB -> (String) attributes.get("sub"); // TODO : 이후 형식에 맞추어 변경
    };
  }

}
