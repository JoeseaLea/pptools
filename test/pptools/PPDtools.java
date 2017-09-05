package pptools;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PPDtools {
	
	public static WebClient webClient = new WebClient();
	
	static {
		// The ScriptException is raised because you have a syntactical
        // error in your javascript.
        // Most browsers manage to interpret the JS even with some kind of
        // errors
        // but HtmlUnit is a bit inflexible in that sense.
        // 加载的页面有js语法错误会抛出异常

        webClient.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
        webClient.getOptions().setCssEnabled(false); // 禁用css支持
        // 设置Ajax异步处理控制器即启用Ajax支持
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        // 当出现Http error时，程序不抛异常继续执行
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        // 防止js语法错误抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常

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
		// 拿到这个网页
        HtmlPage page = webClient.getPage("https://ac.ppdai.com/User/Login?message=&Redirect=");

        // 填入用户名和密码
        HtmlInput inputUsername = (HtmlInput) page.getElementById("UserName");
        inputUsername.type(username);
        HtmlInput inputPassword = (HtmlInput) page.getElementById("Password");
        inputPassword.type(password);

        // 提交
        HtmlInput submit = (HtmlInput) page.getElementById("login_btn");
        HtmlPage htmlPage = submit.click();
        return htmlPage;
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
		return webClient.getPage(url);
	}
}
