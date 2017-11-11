package com.lztimer.server.webapi;

import com.lztimer.server.LztimerServerApplication;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.*;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * TODO:
 *
 * @author Krzysztof Kot (krzysztof.kot.pl@gmail.com)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LztimerServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DesktopSignInControllerIntTest {
    private static WebDriver driver;

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule
            .inCaptureOrSimulationMode("oauth_flow.json");
//    public static HoverflyRule hoverflyRule = HoverflyRule
//            .inCaptureMode("oauth_flow.json", configs()
//                    .sslCertificatePath("ssl/cert.pem")
//                    .sslKeyPath("ssl/key.pem"));

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @BeforeClass
    public static void setUp() throws Exception {
        ChromeDriverManager.getInstance().setup();

        final DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        final Proxy proxy = new Proxy();
        proxy.setNoProxy("localhost");
        proxy.setHttpProxy("localhost:" + hoverflyRule.getProxyPort());
        proxy.setSslProxy("localhost:" + hoverflyRule.getProxyPort());
        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
        desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        driver = new ChromeDriver(desiredCapabilities);
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }

    //@Test
    public void helloPageHasTextHelloWorld2() throws InterruptedException {
        ClientHttpRequestFactorySelector.setAllTrust(true);
        driver.get("http://localhost:8080/signin/desktop/google?port=1234");
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.findElement(By.id("identifierId")).sendKeys("krzykot@gmail.com");
        driver.findElement(By.id("identifierNext")).click();

        Thread.sleep(1000);
        driver.findElement(By.name("password")).sendKeys("11purgatory");
        driver.findElement(By.id("passwordNext")).click();

        Thread.sleep(1000);
        assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Completed"));
    }

    @Test
    public void helloPageHasTextHelloWorld() throws InterruptedException {
        hoverflyRule.resetJournal();
        ClientHttpRequestFactorySelector.setAllTrust(true);
        driver.get("http://localhost:8080/signin/desktop/google?port=1234");
        //driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        Thread.sleep(1000);
        assertThat(driver.findElement(By.tagName("body")).getText(), containsString("Completed"));
    }
}
