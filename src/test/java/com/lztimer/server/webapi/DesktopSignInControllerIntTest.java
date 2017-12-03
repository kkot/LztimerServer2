package com.lztimer.server.webapi;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.config.SocialProviders;
import com.lztimer.server.repository.SocialUserConnectionRepository;
import com.lztimer.server.security.StateProvider;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test on getting access token by desktop application using Google sign-in.
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
        "spring.social.google.app-id=clientId",
        "spring.social.google.app-secret=secret",
        "spring.social.google.authorize-url=http://localhost:8123/o/oauth2/auth",
        "spring.social.google.access-token-url=http://localhost:8123/o/oauth2/token",
        "spring.social.google.user-info-url=http://localhost:8123/oauth2/v2/userinfo"
})
public class DesktopSignInControllerIntTest {
    private static WebDriver driver;

    private static int desktopPort = 8122;

    private static int googlePort = 8123;

    @Autowired
    private SocialUserConnectionRepository repository;

    @ClassRule
    public static WireMockRule wireMockDesktopRule = new WireMockRule(desktopPort);

    @ClassRule
    public static WireMockRule wireMockGoogleRule = new WireMockRule(googlePort);

    @MockBean
    private StateProvider stateProvider;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    @Before
    public void setUp() throws Exception {
        WireMock.reset();
        repository.deleteAll();
        setUpGoogle();
        setUpDesktop();
        setupNormal();
    }

    private void setUpGoogle() throws Exception {
        String clientId = "clientId";
        String secret = "secret";
        String token = "token";
        String code = "code_string";
        String redirectUri = "http://localhost:8080/signin/desktop/google";
        String state = "123";

        String location = redirectUri + "?state=" + state + "&code=" + code;

        wireMockGoogleRule.stubFor(get(urlPathEqualTo("/o/oauth2/auth"))
                .withQueryParam("client_id", equalTo(clientId))
                .withQueryParam("port", equalTo(String.valueOf(desktopPort)))
                .withQueryParam("redirect_uri", equalTo(redirectUri))
                .withQueryParam("response_type", equalTo("code"))
                .withQueryParam("scope", equalTo(SocialProviders.getScope("google")))
                .withQueryParam("state", equalTo(state))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", location)
                )
        );
        wireMockGoogleRule.stubFor(post(urlPathEqualTo("/o/oauth2/token"))
                .withRequestBody(equalTo("client_id=" + clientId + "&client_secret=" + secret + "&code=" + code
                        + "&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fsignin%2Fdesktop%2Fgoogle&grant_type=authorization_code"))
                .willReturn(aResponse().withBody("{\n" +
                        "  \"access_token\": \"" + token + "\", \n" +
                        "  \"token_type\": \"Bearer\", \n" +
                        "  \"expires_in\": 3600, \n" +
                        "  \"refresh_token\": \"xxx_refresh_token\"\n" +
                        "}")
                        .withHeader("Content-Type", "application/json; charset=UTF-8"))
        );
        wireMockGoogleRule.stubFor(get(urlPathEqualTo("/oauth2/v2/userinfo"))
                .withQueryParam("access_token", equalTo(token))
                .willReturn(aResponse().withBody("{\n" +
                        "  \"family_name\": \"K\", \n" +
                        "  \"name\": \"Krzysztof K\", \n" +
                        "  \"picture\": \"photo.jpg\", \n" +
                        "  \"locale\": \"pl\", \n" +
                        "  \"gender\": \"male\", \n" +
                        "  \"email\": \"krzykot123@gmail.com\", \n" +
                        "  \"link\": \"https://plus.google.com/110368901594149035689\", \n" +
                        "  \"given_name\": \"Krzysztof\", \n" +
                        "  \"id\": \"1234\", \n" +
                        "  \"verified_email\": true\n" +
                        "}"
                ).withHeader("Content-Type", "application/json; charset=UTF-8"))
        );

    }

    private void setUpDesktop() {
        WireMock.reset();
        wireMockDesktopRule
                .stubFor(options(urlPathEqualTo("/"))
                        .willReturn(status(200)
                                .withHeader("Access-Control-Allow-Origin", "*")
                                .withHeader("Access-Control-Allow-Methods", "*")
                                .withHeader("Access-Control-Allow-Headers", "Content-Type", "Accept-Encoding", "Accept", "DNT")
                        ));
        wireMockDesktopRule
                .stubFor(post(urlPathEqualTo("/"))
                        .willReturn(status(200)
                                .withHeader("Access-Control-Allow-Origin", "*")
                        ));
    }

    private void setupNormal() {
        connectionFactoryLocator.getConnectionFactory("google");
        when(stateProvider.generateState()).thenReturn("123");
    }

    private void initChromeDriver() throws Exception {
        ChromeDriverManager.getInstance().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");

        driver = new ChromeDriver(chromeOptions);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.close();
        }
    }

    private void assertTokenReceived() {
        wireMockDesktopRule
                .verify(postRequestedFor(urlPathEqualTo("/"))
                        .withRequestBody(containing("token")));
    }

    @Test
    public void chrome_shouldReturnTokenWhenLoginWebsiteWasOpened() throws Exception {
        // given
        initChromeDriver();
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

        // when
        driver.get("http://localhost:8080/signin/desktop/google?port=" + desktopPort);

        // then
        assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Completed"));
        Thread.sleep(1000);
        assertTokenReceived();
    }

    @Test
    public void htmlUnit_shouldReturnTokenWhenLoginWebsiteWasOpened() throws InterruptedException, IOException {
        // given
        try (final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            // when
            final HtmlPage page = webClient.getPage("http://localhost:8080/signin/desktop/google?port=" + desktopPort);

            // then
            final String pageAsXml = page.asText();
            assertThat(pageAsXml, containsString("Completed"));
            Thread.sleep(1000);
            assertTokenReceived();
        }
    }
}
