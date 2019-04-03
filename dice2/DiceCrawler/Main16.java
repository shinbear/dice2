package DiceCrawler;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import DiceCrawler.App.MyRun;



public class Main16 {
	public static int i = 0;
	public static int j = 0;
	public static int pages = 0;
	public static JTextField searchstring = new JTextField();
	public static JTextField location = new JTextField();
	public static JTextField filename = new JTextField("E:/Jobs");
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
	public static String[] compsim = { "Y", "N" };
	public static JComboBox combo1 = new JComboBox(emptype);
	public static JComboBox combo2 = new JComboBox(compseg);
	public static JComboBox combo3 = new JComboBox(compsim);
	public static JComboBox comborad = new JComboBox(r);
	public static JFrame frame = new JFrame();
	public static PrintWriter writer;
	private static ReadProgress dataProgress;
	private static int total=0;
	private static int page=0;
	private static int row=0;
	private static int sim_row=0;
	/*
	 * store the page data Easy Apply, Assoc. Position ID, Dice ID Position ID,
	 * Job Title, Employer, Job Description Location, Posted Keyword1, Keyword2,
	 * Keyword3, Keyword4, comlink posiCount, companyOverview, companyWebsite,
	 * quickFacts, easyApply2
	 */
	public static String[] result = new String[15];
	public static String[] result_sub = new String[15];
	public static String easyflag = "";

	public static void main(String[] args) throws IOException {
		try {				
			input();
			q = "q-" + searchstring.getText();
			if (location.getText() == null || location.getText().equals("")) {
				l = "";
			} else {
				l = "-l-" + location.getText();
			}
			if (combo1.getSelectedItem().toString() == null || combo1.getSelectedItem().toString().equals("")
					|| combo1.getSelectedItem().toString() == "None") {
				jtype = "";
			} else {
				jtype = "-jtype-" + combo1.getSelectedItem().toString();
			}
			if (combo2.getSelectedItem().toString() == null || combo2.getSelectedItem().toString().equals("")
					|| combo2.getSelectedItem().toString() == "None") {
				dcs = "";
			} else {
				dcs = "-dcs-" + combo2.getSelectedItem().toString();
			}
			if (comborad.getSelectedItem().toString() == null || comborad.getSelectedItem().toString().equals("")
					|| comborad.getSelectedItem().toString() == "None") {
				radius = "";
			} else {
				radius = "-radius-" + comborad.getSelectedItem().toString();
			}

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
				filename.setText("E:/jobs");
				input();
			}

			URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-jobs.html";
			System.getProperties().setProperty("webdriver.chrome.driver",
					"C:\\chromedriver.exe");

			//Display data extract progress
			dataProgress = new ReadProgress();
			dataProgress.setVisible(true);
			Thread thread1 = new Thread(dataProgress);
			thread1.start();
			
			// count the the page number of result			
			pages = noofpages(URL);	
			total = pages;
			dataProgress.setPanel(total, page, row, sim_row);
			try {
				writer = new PrintWriter(filename.getText() + ".xls", "UTF-8");
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				writer.close();
			}
			

			// write the excel the top item
			writer.println(
					"Easy Apply\tAssoc. Position ID\tDice ID\tPosition ID\tJob Title\tEmployer\tJob Description\tLocation\tPosted\tKeyword1\tKeyword2\tKeyword3\tKeyword4\tcomlink\tposiCount\tcompanyOverview\tcompanyWebsite\tquickFacts\teasyApply2\tpositionURL");
		
			for (i = 1; i <= pages; i++) {
				if (i > 1) {
					URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-jobs?p=" + i;
				}
				page = i;
				dataProgress.setPanel(total, page, row, sim_row);
				processPage(URL, 0);
			}
			writer.close();
			JOptionPane.showMessageDialog(frame, "Downloading over. Data ready in " + filename.getText() + ".xls");
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}

		writer.close();
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
		panel.add(new JLabel("Similar Position?:"));
		panel.add(combo3);
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

		// invoke getPageDoc method to get Doc object from web url
		Document doc = getPageDoc(URL1);
		int pc;
		String pc_string = doc.select("span#posiCountId").text();
		if (!pc_string.equals("")) {
			pc = Integer.parseInt(doc.select("#posiCountId").text()) / 20 + 1;
		}
		else
			pc= 1;
		return pc;
	}

	/**
	 * process the webpage,
	 * 
	 * @param page
	 *            URL, flag,0-listpage, 1-subpage, 2 similar position page
	 */
	public static void processPage(String URL1, int flag) throws IOException {
		try {
			if (flag == 0) {
				Document doc = getPageDoc(URL1);
				
				Elements jobitem = doc.select("div.complete-serp-result-div");
				// Elements jobitem = doc.getElementsByClass("serp-result-content");
				String job_href = "";

				// .select("serp-result-content")	
				
				for (Element link1 : jobitem) {
					// Get the flag of "east apply"
					if (!link1.getElementsByClass("easyApply").isEmpty()) {
						easyflag = "Y";
					} else {
						easyflag = "N";
					}
					// Get the job href
					Elements job_Title_array = link1.select("a[title]");
					for (Element link2 : job_Title_array) {
						if (!link2.attr("title").isEmpty()) {
							job_href = "https://www.dice.com" + link2.attr("href");
						}
					}
					// process the 1st sub page
					row++;
					dataProgress.setPanel(total, page, row, sim_row);
					processPage(job_href, 1);
				}
				row =0;
			} else if (flag == 1 || flag == 2) {
				Document doc_sub = getPageDoc(URL1);
				if (flag == 2) {
					easyflag = "S";
				}
				String dice_ID = "";
				String position_ID = "";
				String job_Title = "";
				String employer = "";
				String job_desc = "";
				String location = "";
				String posted = "";
				String[] keyword = new String[4];
				String comlink = "";
				String sim_position_link = "";
				// comPageData[0]-posiCount, comPageData[1]-companyOverview,
				// comPageData[2]-companyWebsite, comPageData[3]-quickFacts,
				// comPageData[4]-easyApply2
				String[] comPageData = new String[5];

				// Get the dice_id
				for (Element dice_id_element : doc_sub.select("div.col-md-12")) {
					if (dice_id_element.text().contains("Dice Id")) {
						dice_ID = dice_id_element.text().replaceAll("Dice Id : ", "");
						break;
					}
				}
				// Get the poisition_id
				for (Element position_ID_element : doc_sub.select("div.col-md-12")) {
					if (position_ID_element.text().contains("Position Id : ")) {
						position_ID = position_ID_element.text().replaceAll("Position Id : ", "");
						break;
					}
				}
				// Get the job_title
				job_Title = doc_sub.select("h1.jobTitle").first().text();
				// Get the employer
				employer = doc_sub.select("span#hiringOrganizationName").first().text();
				// Get the job desc
				job_desc = doc_sub.select("div#jobdescSec").first().text();
				// Get the location
				location = doc_sub.select("li.location").first().text();
				// Get the posted date
				posted = doc_sub.select("li.posted").first().text();
				// Get the keyword1,keyword2,keyword3,keyword4
				int count = 0;
				for (Element keywords_element : doc_sub.select(".iconsiblings")) {
					keyword[count] = keywords_element.text();
					count++;
				}
				// Get the company link in Dice
	
				comlink = "https://www.dice.com" + doc_sub.select("a#companyNameLink").attr("href");
				if (comlink.equals("https://www.dice.com")) {
					comlink = "https://www.dice.com" + doc_sub.select("li.hiringOrganization >a").attr("href");
				}
				comPageData = processComPage(comlink);

				writer.println(easyflag + "\t" + "" + "\t" + dice_ID + "\t" + position_ID + "\t" + job_Title + "\t"
						+ employer + "\t" + job_desc + "\t" + location + "\t" + posted + "\t" + keyword[0] + "\t"
						+ keyword[1] + "\t" + keyword[2] + "\t" + keyword[3] + "\t" + comlink + "\t" + comPageData[0]
						+ "\t" + comPageData[1] + "\t" + comPageData[2] + "\t" + comPageData[3] + "\t"
						+ comPageData[4] + "\t" + URL1);
				

				// If it is 1st sub page, then get the similar position link and
				// parse them
				if (flag == 1) {
					if (combo3.getSelectedItem().toString() == "Y") {
						for (Element sim_pos_link : doc_sub.select(".mTB10")) {
							sim_position_link = "https://www.dice.com" + sim_pos_link.select("a").attr("href");
							// process the similar job page
							if (sim_position_link != "https://www.dice.com/"
									&& sim_position_link != "https://www.dice.com") {
								sim_row++;
								dataProgress.setPanel(total, page, row, sim_row);
								processPage(sim_position_link, 2);
							}
						}
					}
					sim_row = 0;
				}
			}
		} catch (Exception localException) {
		}
	}

	/**
	 * Get the company overview page
	 * 
	 * @param page
	 *            URL
	 */
	public static String[] processComPage(String URL) {
		String[] comPageData = new String[5];
		Document doc_comOver = getPageDoc(URL);
		// comPageData[0]-posiCount, comPageData[1]-companyOverview,
		// comPageData[2]-companyWebsite, comPageData[3]-quickFacts,
		// comPageData[4]-easyApply2
		String composCount_temp = doc_comOver.select("span.posiCount").text();
		if (composCount_temp.contains("of")) {
			composCount_temp = composCount_temp.substring(composCount_temp.indexOf("of") + 3);
			comPageData[0] = composCount_temp;
		} else {
			comPageData[0] = doc_comOver.select("span.posiCount").text();
		}
		comPageData[1] = doc_comOver.select(".compant-block").text();

		for (Element comWebsite : doc_comOver.select(".clabel")) {
			if (comWebsite.text().contains("Company Website")) {
				comPageData[2] = comWebsite.attr("href");
			}

		}
		comPageData[3] = "";
		comPageData[4] = "";
		return comPageData;
	}

	public static Document getPageDoc(String URL) {
		// ChromeOptions options = new ChromeOptions();

		WebDriver webDriver = new ChromeDriver();		
		webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		String responseBody = "";
		try {
			webDriver.get(URL);
		} catch (Exception e) {
			webDriver.close();
		}
		if (webDriver.getPageSource() != null) {
			responseBody = webDriver.getPageSource();
		}

/*		File file = new File("E:/sistertask/2019-02-24/log.txt");
        String pageXml = txt2String(file);
        Document doc = Jsoup.parse(pageXml);// 获取html文档
        */
		Document doc = Jsoup.parse(responseBody);// 获取html文档
		webDriver.close();

        
		return doc;
	}
	
	public static Document getPageDocByHtmlunit(String URL) {
		System.out.print("read page:"+page+" row:"+row+" sim_row:"+row);
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);// 新建一个模拟谷歌Chrome浏览器的浏览器客户端对象
		webClient.getOptions().setThrowExceptionOnScriptError(false);// 当JS执行出错的时候是否抛出异常,
																		// //
																		// 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);// 当HTTP的状态非200时是否抛出异常,
																			// //
																			// 这里选择不需要
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);// 是否启用CSS, 因为不需要展现页面,
		webClient.getOptions().setJavaScriptEnabled(true); // 很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());// 很重要，设置支持AJAX
		// webClient.waitForBackgroundJavaScript(10 * 1000);
		webClient.getOptions().setTimeout(5 * 1000);
		//webClient.setJavaScriptTimeout(5 * 1000);
		//webClient.getOptions().setTimeout(5000);

		HtmlPage page = null;
		try {
			page = webClient.getPage(URL);
		} catch (Exception e) {
			e.printStackTrace();
			Document doc = Jsoup.parse(" ");
			System.out.print("read FAIL on the page:"+page+" row:"+row+" sim_row:"+row);
			return doc;
		} finally {
			webClient.closeAllWindows();
		}

		// webClient.waitForBackgroundJavaScript(10000);
		// 异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
		String pageXml = page.asXml();// 直接将加载完成的页面转换成xml格式的字符串

		// File file = new File("e:\\log.txt");
		// String pageXml = txt2String(file);
	
		Document doc = Jsoup.parse(pageXml);// 获取html文档			
		return doc;
	}

	public static void contentToTxt(String filePath, String content) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath), true));
			writer.write("\n" + content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String txt2String(File file) {
		StringBuilder result = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

}