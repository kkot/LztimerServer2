package com.lztimer.server.webapi;

import com.lztimer.server.entity.User;
import com.lztimer.server.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/social")
public class SocialController {

    private final Logger log = LoggerFactory.getLogger(SocialController.class);

    private final SocialService socialService;

    private final ProviderSignInUtils providerSignInUtils;

    private final SignInAdapter signInAdapter;

    public SocialController(SocialService socialService, ProviderSignInUtils providerSignInUtils,
                            SignInAdapter signInAdapter) {
        this.socialService = socialService;
        this.providerSignInUtils = providerSignInUtils;
        this.signInAdapter = signInAdapter;
    }

    @GetMapping("/signup")
    public RedirectView signUp(NativeWebRequest webRequest, @CookieValue(name = "NG_TRANSLATE_LANG_KEY", required = false, defaultValue = "\"en\"") String langKey) {
        try {
            Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
            User user = socialService.createSocialUser(connection, langKey.replace("\"", ""));
            signInAdapter.signIn(user.getLogin(), connection, webRequest);
            return new RedirectView("/");
        } catch (Exception e) {
            log.error("Exception creating social user: ", e);
            return new RedirectView(URIBuilder.fromUri("/#/social-register/no-provider")
                .queryParam("success", "false")
                .build().toString(), true);
        }
    }
}
