package pptools;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PPDtoolsTest {

	@Test
	public void testLogin() {
		try {
			HtmlPage htmlPage = null;
			
			htmlPage = PPDtools.login("319011485@qq.com", "ppdsecurity123");
			
			htmlPage = PPDtools.getUrlPage("http://invest.ppdai.com/account/blacklist?LateDayTo=1&LateDayFrom=#list");
			System.out.println(htmlPage.asText());
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}