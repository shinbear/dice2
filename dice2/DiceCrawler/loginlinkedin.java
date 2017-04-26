package DiceCrawler;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.*;

public class loginlinkedin {
	public static void main(String[] args) throws IOException {
		WebClient webClient = new WebClient();// 创建WebClient
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		// 获取页面
		HtmlPage page = webClient.getPage("https://www.linkedin.com/uas/login"); // 打开linkedin

		// 获得name为"session_key"的html元素
		HtmlElement usernameEle = page.getElementByName("session_key");
		// 获得id为"session_password"的html元素
		HtmlElement passwordEle = (HtmlElement) page.getElementById("session_password-login");
		usernameEle.focus(); // 设置输入焦点
		usernameEle.type("z_hao1975@hotmail.com"); // 填写值

		passwordEle.focus(); // 设置输入焦点
		passwordEle.type("zh197544"); // 填写值
		// 获得name为"submit"的元素
		HtmlElement submitEle = page.getElementByName("signin");
		// 点击“登陆”
		page = submitEle.click();
		String result = page.asXml();// 获得click()后的html页面（包括标签）
		if (result.contains("Sign Out")) {
			System.out.println("登陆成功");
		} else {
			System.out.println("登陆失败");
		}

	}

}
