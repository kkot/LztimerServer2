package com.lztimer.server.webapi;

import com.lztimer.server.LztimerServerApplication;
import com.lztimer.server.config.SocialProviders;
import com.lztimer.server.security.StateProvider;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static io.specto.hoverfly.junit.core.SimulationSource.dsl;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.response;
import static io.specto.hoverfly.junit.dsl.HoverflyDsl.service;
import static io.specto.hoverfly.junit.dsl.ResponseCreators.success;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.any;
import static io.specto.hoverfly.junit.dsl.matchers.HoverflyMatchers.equalsTo;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DesktopSignInControllerIntTest {
    private static WebDriver driver;

    // TODO: is it needed in argument
    private static String desktopPort = "8123";

    @MockBean
    private StateProvider stateProvider;

    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    public static SimulationSource dsl1() {
        //response().status(302).header("Location", "http://localhost:8080/signin/desktop/google?state=81093253-44a5-4487-b413-4f2c099d4116&code=4%2F5XSkKkHjLJFItqNMCvT0feOWk1wcj3IGfNfKPFTKndo#"))
        String clientId = "1377997861-3a8oahagqanum65ipk39boocl5bevue7.apps.googleusercontent.com";
        String redirectUri = "http://localhost:8080/signin/desktop/google";
        String state = "123";
        return dsl(
                service("accounts.google.com")
                        .get("/o/oauth2/auth")
                        .queryParam("client_id", clientId)
                        .queryParam("port", desktopPort)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("response_type", "code")
                        .queryParam("scope", SocialProviders.getScope("google"))
                        .queryParam("state", "123")
                        .willReturn(response().status(302).header("Location", redirectUri + "?state=" + state + "&code=4%2F5XSkKkHjLJFItqNMCvT0feOWk1wcj3IGfNfKPFTKndo#"))
                        .post("/o/oauth2/token").body("client_id=" + clientId + "&client_secret=bI4jbbBc_tBwXqzd0yCFxqxi&code=4%2F5XSkKkHjLJFItqNMCvT0feOWk1wcj3IGfNfKPFTKndo&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fsignin%2Fdesktop%2Fgoogle&grant_type=authorization_code")
                        .willReturn(success("{\n" +
                                "  \"access_token\": \"ya29.GlsBBfVrCpy5bpQxixKQ5wobD7qW1_bOUC_ckZgSqDu1nh9PICR2b0zitr6KGJz8lhlLTzP7tbpomLeth4LKrsJfN1wXA9LMB7uWgKyfHkSD5bW4KJIuHHhI_BqQ\", \n" +
                                "  \"token_type\": \"Bearer\", \n" +
                                "  \"expires_in\": 3600, \n" +
                                "  \"refresh_token\": \"1/IwS67z9rCLZczc7eMNqzY6z5oIG2cTz2a10f6zsp5E8\"\n" +
                                "}", "application/json; charset=UTF-8")),
                service("www.googleapis.com").get("/oauth2/v2/userinfo").queryParam("access_token", "ya29.GlsBBfVrCpy5bpQxixKQ5wobD7qW1_bOUC_ckZgSqDu1nh9PICR2b0zitr6KGJz8lhlLTzP7tbpomLeth4LKrsJfN1wXA9LMB7uWgKyfHkSD5bW4KJIuHHhI_BqQ").willReturn(success("{\n" +
                        "  \"family_name\": \"Kot\", \n" +
                        "  \"name\": \"Krzysztof Kot\", \n" +
                        "  \"picture\": \"https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg\", \n" +
                        "  \"locale\": \"pl\", \n" +
                        "  \"gender\": \"male\", \n" +
                        "  \"email\": \"krzykot@gmail.com\", \n" +
                        "  \"link\": \"https://plus.google.com/110368901594149035689\", \n" +
                        "  \"given_name\": \"Krzysztof\", \n" +
                        "  \"id\": \"110368901594149035689\", \n" +
                        "  \"verified_email\": true\n" +
                        "}", "application/json; charset=UTF-8")));
    }

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule
            .inSimulationMode(dsl1());

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setupNormal() {
        connectionFactoryLocator.getConnectionFactory("google");
        when(stateProvider.generateState()).thenReturn("123");
    }

    @BeforeClass
    public static void setUp() throws Exception {

        ChromeDriverManager.getInstance().setup();
        System.setProperty("webdriver.chrome.verboseLogging", "true");

        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("--headless");
        //chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--verbose");
        //chromeOptions.addArguments("--args");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--allow-insecure-localhost");
        chromeOptions.addArguments("--allow-running-insecure-content");

        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        final Proxy proxy = new Proxy();
        proxy.setNoProxy("localhost,code.jquery.com");
        proxy.setHttpProxy("localhost:" + hoverflyRule.getProxyPort());
        proxy.setSslProxy("localhost:" + hoverflyRule.getProxyPort());
        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
        desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        driver = new ChromeDriver(desiredCapabilities);
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

    @Test
    public void helloPageHasTextHelloWorld() throws InterruptedException {
        ClientHttpRequestFactorySelector.setAllTrust(true);

        Thread.sleep(3000);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("http://localhost:8080/signin/desktop/google?port=" + desktopPort);

        Thread.sleep(3000);

        assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Completed"));
    }
}
