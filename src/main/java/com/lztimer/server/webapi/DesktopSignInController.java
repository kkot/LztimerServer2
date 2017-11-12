package com.lztimer.server.webapi;

import com.lztimer.server.config.SocialProviders;
import com.lztimer.server.entity.User;
import com.lztimer.server.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.*;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Krzysztof Kot (krzykot@gmail.com)
 */
@RequestMapping("/signin/desktop")
public class DesktopSignInController extends ProviderSignInController {
    private final Logger log = LoggerFactory.getLogger(DesktopSignInController.class);

    private final SocialService socialService;

    private final ProviderSignInUtils providerSignInUtils;

    private final SignInAdapter signInAdapter;

    private final SocialProviders socialProviders;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    public DesktopSignInController(ConnectionFactoryLocator connectionFactoryLocator,
                                   UsersConnectionRepository usersConnectionRepository,
                                   SignInAdapter signInAdapter, SocialService socialService,
                                   ProviderSignInUtils providerSignInUtils, SocialProviders socialProviders) {
        super(connectionFactoryLocator, usersConnectionRepository, signInAdapter);
        this.socialService = socialService;
        this.providerSignInUtils = providerSignInUtils;
        this.signInAdapter = signInAdapter;
        this.socialProviders = socialProviders;
        addSignInInterceptor(new GoogleProviderSigninInterceptor(socialProviders));
    }

    @GetMapping("/new_user")
    public RedirectView signUp(NativeWebRequest webRequest,
                               @CookieValue(name = "NG_TRANSLATE_LANG_KEY", required = false, defaultValue = "\"en\"") String langKey,
                               RedirectAttributes attributes) {
        try {
            Connection<?> connection = providerSignInUtils.getConnectionFromSession(webRequest);
            User user = socialService.createSocialUser(connection, langKey.replace("\"", ""));
            signInAdapter.signIn(user.getLogin(), connection, webRequest);
            attributes.addAttribute("just_created", true);
            attributes.addAttribute("port", sessionStrategy.getAttribute(webRequest, "port"));
            return new RedirectView("completed");
        } catch (Exception e) {
            log.error("Exception creating social user: ", e);
            return new RedirectView(URIBuilder.fromUri("/#/social-register/no-provider")
                    .queryParam("success", "false")
                    .build().toString(), true);
        }
    }

    @RequestMapping(value="/{providerId}", method= RequestMethod.GET, params="port")
    public RedirectView signIn(@PathVariable String providerId, @RequestParam("port") Integer port,
                               NativeWebRequest request) {
        sessionStrategy.setAttribute(request, "port", port);
        request.setAttribute("scope", socialProviders.getScope(providerId), RequestAttributes.SCOPE_REQUEST);
        return this.signIn(providerId, request);
    }

    @GetMapping("/completed")
    public ModelAndView completed(NativeWebRequest webRequest) {
        return new ModelAndView("/completed.html");
    }
}
