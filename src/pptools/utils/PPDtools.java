package pptools.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class PPDtools {
	
	public static WebClient webClient = null;
	private static Set<Cookie> cookies = null;
	
	public static WebClient getWebClient() {

		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
		webClient.getOptions().setCssEnabled(false); // 禁用css支持
		// 设置Ajax异步处理控制器即启用Ajax支持
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		// 当出现Http error时，程序不抛异常继续执行
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		// 防止js语法错误抛出异常
		webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		webClient.getCookieManager().setCookiesEnabled(true);//开启cookie管理
		webClient.getOptions().setRedirectEnabled(true); 
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		
//		webClient.getOptions().getProxyConfig().setProxyHost("61.54.56.222");
//		webClient.getOptions().getProxyConfig().setProxyPort(80);
		return webClient;
	}
	
	
	/**
	 * 登录拍拍贷
	 * @param username 用户名
	 * @param password 密码
	 * @return htmlPage 登录后跳转的页面数据
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HtmlPage login(String username, String password) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient = getWebClient();
		// 拿到这个网页
        HtmlPage page = webClient.getPage("https://ac.ppdai.com/User/Login?message=&Redirect=");

        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
//        Iterator<Cookie> it = cookies.iterator();
        StringBuilder cookiessb = new StringBuilder();
        for (Cookie cookie : cookies) {
        		cookiessb.append("; " + cookie.getName() + "=" + cookie.getValue());
		}
        
        // 填入用户名和密码
        HtmlInput inputUsername = (HtmlInput) page.getElementById("UserName");
        inputUsername.type(username);
        HtmlInput inputPassword = (HtmlInput) page.getElementById("Password");
        inputPassword.type(password);

        // 提交
        HtmlInput submit = (HtmlInput) page.getElementById("login_btn");
        HtmlPage htmlPage = submit.click();
        
        cookies = webClient.getCookieManager().getCookies();
        webClient.close();
        
        return htmlPage;
	}
	
	/**
	 * 判断登录是否成功
	 * @param cookies 登录后的cookies
	 * @return 登录成功返回true，不成功返回false
	 */
	public static boolean isLoginSuccess() {
		for (Cookie c : cookies) {
			if (c.getName().equals("authid"))
				return true;
		}
		return false;
	}
	
	/**
	 * 获取指定url页面数据
	 * @param url url地址
	 * @return 页面数据
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static HtmlPage getUrlPage(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient = getWebClient();
		Iterator<Cookie> i = cookies.iterator();
		while (i.hasNext()) {
			webClient.getCookieManager().addCookie(i.next());
		}
		HtmlPage htmlPage = webClient.getPage(url);
		webClient.close();
		return htmlPage;
	}
}
