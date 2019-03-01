package DiceCrawler;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Main9 {
	public static int i = 0;
	public static int j = 0;
	public static int sizemain = 0;
	public static int sizeref = 0;
	public static int pages = 0;
	public static JTextField searchstring = new JTextField();
	public static JTextField location = new JTextField();
	public static JTextField filename = new JTextField("C:/Jobs");
	public static String URL = "";
	public static String q;
	public static String dcs;
	public static String jtype;
	public static String radius;
	public static String l;
	public static String dc = "";
	public static String[] compseg = { "Recruiter", "DirectHire", "None" };
	public static String[] r = { "30", "5", "10", "20", "40", "50", "75", "100" };
	public static String[] emptype = { "Full Time", "Part Time", "Contracts", "Third Party", "None" };

	public static JComboBox combo1 = new JComboBox(emptype);
	public static JComboBox combo2 = new JComboBox(compseg);
	public static JComboBox comborad = new JComboBox(r);
	public static JFrame frame = new JFrame();
	public static String urllist = "";
	public static String allurllist = "";
	public static String urllist_sim = "";
	public static String allurllist_sim = "";
	public static String ref = "";
	public static PrintWriter writer;

	public static void main(String[] args) throws IOException {
		try {
			input();

			q = "q-" + "%28" + searchstring.getText() + "%29";
			l = "-l-" + location.getText();
			jtype = "-jtype-" + combo1.getSelectedItem().toString();
			dcs = "-dcs-" + combo2.getSelectedItem().toString();
			radius = "-radius-" + comborad.getSelectedItem().toString();

			String q1 = q.replace(" ", "_");
			String l1 = l.replace(" ", "_");
			String jtype1 = jtype.replace(" ", "_");
			String dcs1 = dcs.replace(" ", "_");
			String radius1 = radius.replace(" ", "_");

			if (l.isEmpty())
				l1 = "";
			if (dcs.equalsIgnoreCase("None"))
				dcs1 = "";
			if (jtype.equalsIgnoreCase("None"))
				jtype1 = "";
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("C:/jobs");
				input();
			}

			// URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 +
			// radius1 + "-jobs.html";
			URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-jobs.html";

			pages = noofpages(URL);
			try {
				writer = new PrintWriter(filename.getText() + ".xls", "UTF-8");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				writer.close();
			}

			writer.println(
					"Easy Apply\tAssoc. Position ID\tDice ID\tPosition ID\tJob Title\tEmployer\tJob Description\tLocation\tPosted\tKeyword1\tKeyword2\tKeyword3\tKeyword4\tcomlink\tposiCount\tcompanyOverview\tcompanyWebsite\tquickFacts\teasyApply2");

			for (i = 1; i <= pages; i += 1) {
				URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-startPage-" + 1 + "-limit-"
						+ (pages + 1) + "-" + "-jobs.html" + "?p=" + i;

				processPage(URL, 0);
			}

			String[] URLprocess = urllist.split("\n");

			i = 0;
			sizemain = URLprocess.length;

			while (i < sizemain) {
				parser(URLprocess[(i++)], "None");
			}

			writer.close();

			JOptionPane.showMessageDialog(frame, "Downloading over. Data ready in " + filename.getText() + ".xls");
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}

		System.exit(0);
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(new JLabel("Search string:"));
		panel.add(searchstring);
		panel.add(new JLabel("Location:"));
		panel.add(location);
		panel.add(new JLabel("Radius(miles):"));
		panel.add(comborad);
		panel.add(new JLabel("Employment Type:"));
		panel.add(combo1);
		panel.add(new JLabel("Company Segment:"));
		panel.add(combo2);
		panel.add(new JLabel("File path to store results (without extention):"));
		panel.add(filename);

		int result = JOptionPane.showConfirmDialog(null, panel, "Dice.com - Search Criteria", 2, -1);
		if (result == 0) {
			return;
		}
		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
	}

	public static int noofpages(String URL1) throws IOException {
		// Document doc = Jsoup.connect(URL1).timeout(0).get();

		// Connection conn = Jsoup.connect(URL1).timeout(5000);
		// conn.header("Accept",
		// "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		// conn.header("Accept-Encoding", "gzip, deflate, sdch, br");
		// conn.header("Accept-Language", "zh-CN,zh;q=0.8");
		// conn.header("Cache-Control", "max-age=0");
		// conn.header("Connection", "keep-alive");
		// conn.header("Host", "www.dice.com");
		// conn.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64)
		// AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98
		// Safari/537.36 LBBROWSER");
		// conn.ignoreContentType(true);
		// conn.ignoreContentType(true);

/*		final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

		webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常,
																		// 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常,
																			// 这里选择不需要
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面,
													// 所以不需要启用
		webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX
		webClient.waitForBackgroundJavaScript(10 * 1000);
		webClient.setJavaScriptTimeout(5 * 1000);
		webClient.getOptions().setTimeout(5000);

		HtmlPage page = null;
		try {
			page = webClient.getPage(URL1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webClient.closeAllWindows();
			;
		}

		webClient.waitForBackgroundJavaScript(30000);// 异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束

		String pageXml = page.asXml();// 直接将加载完成的页面转换成xml格式的字符串

		// TODO 下面的代码就是对字符串的操作了,常规的爬虫操作,用到了比较好用的Jsoup库

		Document doc = Jsoup.parse(pageXml);// 获取html文档
*/
		// Document doc = conn.get();
		
		Document doc = getPageDoc(URL1);
		Element header = doc.head();
		Elements script = header.select("script");

		Pattern p = Pattern.compile("(?is)pageCount : \\d(.+?)");

		Matcher m = p.matcher(script.html());

		String pc = null;
		String pc1 = null;

		while (m.find()) {
			pc = m.group();
		}
		pc1 = pc.replaceAll("\\D+", "");
		int pc2 = Integer.parseInt(pc1);
		return pc2;
	}

	public static void processPage(String URL1, int flag) throws IOException {
		try {
			// Document doc = Jsoup.connect(URL1).timeout(5000).get();
			Document doc = getPageDoc(URL1);
			Elements questions = doc.select("a[href]");
			Elements easyApply = doc.getElementsByClass("list-inline");
			Iterator localIterator2;
			Object link;
			if (flag == 0) {
				String tag = "";
				String total_easyflag = "";
				for (Element link1 : easyApply) {
					tag = link1.getElementsByClass("easyApply").toString();
					if (tag.equals(""))
						total_easyflag = total_easyflag + "N";
					else {
						total_easyflag = total_easyflag + "Y";
					}

				}

				int i = 0;
				for (localIterator2 = questions.iterator(); localIterator2.hasNext();) {
					link = (Element) localIterator2.next();
					if (((((Element) link).attr("href").contains("jobs/detail"))
							|| (((Element) link).attr("href").contains("job/result")))
							&& (!allurllist.contains(((Element) link).attr("abs:href")))) {
						urllist = urllist + ((Element) link).attr("abs:href") + "||" + total_easyflag.charAt(i) + "\n";
						i++;
						allurllist += urllist;
					}
				}

				System.out.print(allurllist);
			} else if (flag == 1) {
				String tag_sim = "";
				String total_easyflag_sim = "";
				for (link = easyApply.iterator(); ((Iterator) link).hasNext();) {
					Element link1 = (Element) ((Iterator) link).next();

					tag_sim = link1.getElementsByClass("easyApply").toString();
					if (tag_sim.equals(""))
						total_easyflag_sim = total_easyflag_sim + "S";
					else {
						total_easyflag_sim = total_easyflag_sim + "Y";
					}

				}

				int i = 0;
				for (Element link1 : questions)
					if (((link1.attr("href").contains("jobs/detail")) || (link1.attr("href").contains("job/result")))
							&& (!allurllist_sim.contains(link1.attr("abs:href")))) {
						urllist_sim = urllist_sim + link1.attr("abs:href") + "||" + total_easyflag_sim.charAt(i) + "\n";
						i++;
						allurllist_sim += urllist_sim;
					}
			}
		} catch (Exception localException) {
		}
	}

	public static void parser(String URL1, String refPos) throws IOException {
		try {
			String URL2 = URL1.substring(0, URL1.indexOf("||"));
			String easytag = URL1.substring(URL1.indexOf("||") + 2);
			Document doc1 = Jsoup.connect(URL2).timeout(0).get();
			Element body = doc1.body();

			String jobTitle = body.select("h1.jobTitle").text();
			String loc = body.select("li.location").text();
			String posted = body.select("li.posted").text();

			Elements keywordcase = doc1.select("div.iconsiblings");
			String keyword1 = keywordcase.first().text();
			String keyword2 = ((Element) keywordcase.get(1)).text();
			String keyword3 = ((Element) keywordcase.get(2)).text();
			String keyword4 = ((Element) keywordcase.get(3)).text();
			String jobdesc = body.select("div.highlight-black").first().text();

			String employer = body.select("li.employer").text();

			Elements dicelink = doc1.select("a.dice-btn-link");
			String comlink = ((Element) dicelink.get(2)).attr("abs:href");

			URL url = new URL(URL2);

			String ID1 = url.getPath();
			String[] ID = ID1.split("/");
			int pathArrSize = ID.length;

			String posiCount = parser2(comlink)[0];
			String companyOverview = parser2(comlink)[1];
			String companyWebsite = parser2(comlink)[2];
			String quickFacts = parser2(comlink)[3];
			String easyApply2 = parser2(comlink)[4];

			if (refPos.compareTo("None") == 0) {
				writer.println(easytag + "\t" + ID[(pathArrSize - 1)] + "\t" + ID[(pathArrSize - 2)] + "\t"
						+ ID[(pathArrSize - 1)] + "\t" + jobTitle + "\t" + employer + "\t" + jobdesc + "\t" + loc + "\t"
						+ posted + "\t" + keyword1 + "\t" + keyword2 + "\t" + keyword3 + "\t" + keyword4 + "\t"
						+ comlink + "\t" + posiCount + "\t" + companyOverview + "\t" + companyWebsite + "\t"
						+ quickFacts + "\t" + easyApply2);
			} else {
				writer.println(easytag + "\t" + refPos + "\t" + ID[(pathArrSize - 2)] + "\t" + ID[(pathArrSize - 1)]
						+ "\t" + jobTitle + "\t" + employer + "\t" + jobdesc + "\t" + loc + "\t" + posted + "\t"
						+ keyword1 + "\t" + keyword2 + "\t" + keyword3 + "\t" + keyword4 + "\t" + comlink + "\t"
						+ posiCount + "\t" + companyOverview + "\t" + companyWebsite + "\t" + quickFacts + "\t"
						+ easyApply2);
			}

			String simpos = body.select("h4.poistionat").text();

			easytag = "";

			if ((simpos.equalsIgnoreCase("Similar Positions")) && (refPos.equalsIgnoreCase("None"))) {
				urllist_sim = "";
				processPage(URL2, 1);

				String[] URLprocess_sim = urllist_sim.split("\n");

				sizeref = URLprocess_sim.length;

				int i = 0;

				while (i < sizeref) {
					parser(URLprocess_sim[(i++)], ID[(pathArrSize - 1)]);
				}
			}

			throw new Exception();
		} catch (Exception localException) {
		}
	}

	public static String[] parser2(String URL1) throws IOException {
		String[] str = new String[4];
		try {
			Document doc1 = Jsoup.connect(URL1).timeout(0).get();
			Element body = doc1.body();

			String posiCountTemp = body.select(".posiCount").text();
			String posiCount = posiCountTemp.substring(posiCountTemp.indexOf("f") + 1);
			String companyOverview = body.select("div.compant-block").text();
			String companyWebsite = ((Element) body.select(".undeline_URL").get(0)).attr("abs:href");
			String StrquickFacts = "";
			Elements companyHeader = body.select(".clabel,.ctxt");
			for (Element quckFacts : companyHeader) {
				if (!quckFacts.hasAttr("href")) {
					StrquickFacts = StrquickFacts + "||" + quckFacts.text();
				}
			}

			String easyApply2 = body.select(".easyApply").text();
			String[] companyParse = new String[5];
			companyParse[0] = posiCount;
			companyParse[1] = companyOverview;
			companyParse[2] = companyWebsite;
			companyParse[3] = StrquickFacts;
			if (easyApply2.equals("")) {
				companyParse[4] = "N";
			} else {
				companyParse[4] = "Y";
			}

			return companyParse;
		} catch (Exception localException) {
			String[] companyParse = new String[4];
			companyParse[0] = "error";
			companyParse[1] = "error";
			companyParse[2] = "error";
			companyParse[3] = "error";
			companyParse[4] = "error";
			return companyParse;
		}
	}

	public static Document getPageDoc(String URL) {
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象
		webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常,																	// 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常,																			// 这里选择不需要
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面,
		webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX
		webClient.waitForBackgroundJavaScript(10 * 1000);
		webClient.setJavaScriptTimeout(5 * 1000);
		webClient.getOptions().setTimeout(5000);

		HtmlPage page = null;
		try {
			page = webClient.getPage(URL);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webClient.closeAllWindows();
			;
		}

		webClient.waitForBackgroundJavaScript(30000);// 异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
		String pageXml = page.asXml();// 直接将加载完成的页面转换成xml格式的字符串

		// TODO 下面的代码就是对字符串的操作了,常规的爬虫操作,用到了比较好用的Jsoup库
		Document doc = Jsoup.parse(pageXml);// 获取html文档
		return doc;
	}
}