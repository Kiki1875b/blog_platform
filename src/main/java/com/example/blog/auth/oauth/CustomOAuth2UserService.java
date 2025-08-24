package com.example.blog.auth.oauth;

import com.example.blog.common.enumerated.MemberStatus;
import com.example.blog.common.enumerated.Provider;
import com.example.blog.domain.member.entity.Member;
import com.example.blog.domain.member.entity.MemberRole;
import com.example.blog.domain.member.repository.MemberRepository;
import com.example.blog.mapper.MemberMapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;
  private final MemberMapper mapper;
  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    Map<String, Object> attributes = oAuth2User.getAttributes();

    OAuth2Provider provider = OAuth2Provider.valueOf(registrationId.toUpperCase());
    Member member = provider.getOrCreateUser(attributes, memberRepository, mapper);

    // OAuth2User 의 attributes 는 unmodifiable
    Map<String, Object> attributesCopy = new HashMap<>(attributes);
    attributesCopy.put("email", member.getEmail());

    return new CustomOAuth2User(member, attributesCopy);
  }

  enum OAuth2Provider {
    GOOGLE {
      @Override
      Member getOrCreateUser(Map<String, Object> attributes, MemberRepository memberRepository, MemberMapper mapper){
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get("sub");

        return memberRepository.findByEmail(email).orElseGet(() -> {
          Member newUser = mapper.fromOAuthToMember(name, email, providerId, Provider.GOOGLE, "temp");
          return memberRepository.save(newUser);
        });
      }
    },
    KAKAO{
      @Override
      Member getOrCreateUser(Map<String, Object> attributes, MemberRepository memberRepository, MemberMapper mapper){

        String providerId = attributes.get("id").toString();
        String nickname = (String) ((Map<?, ?>)  attributes.get("properties")).get("nickname");
        String email = nickname + "@kakao.com";

        return memberRepository.findByEmail(email).orElseGet(() -> {
          Member newUser = mapper.fromOAuthToMember(nickname, email, providerId, Provider.KAKAO, "temp");
          return memberRepository.save(newUser);
        });
      }
    };

    abstract Member getOrCreateUser(Map<String, Object> attributes, MemberRepository memberRepository, MemberMapper mapper);
  }


}
