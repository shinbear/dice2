package DiceCrawler;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import java.io.File;
import java.io.FileOutputStream;

import jxl.*;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.Boolean;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import jxl.*;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.Boolean;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import java.util.Scanner;

public class Main4 {
	public static int Sub_ID = 0;
	public static String cliuid_2 = "";
	public static String unedname = "";
	public static String search_method = "";
	public static int rawID_Total = 0;
	public static JTextField filename = new JTextField("c:/p.xls");
	public static String URL = "";
	public static JFrame frame = new JFrame();
	public static PrintWriter writer;
	public static DB db;
	public static String str_SQL;
	public static String str_trim = "0000";
	public static String cliuid_2_unedname = "";
	public static String dirName = "";
	public static String fileName_final = "";
	

	public static void main(String[] args) throws IOException {
		try {
			input();
			Sheet sheet;
			Workbook book;
			try {
				book = Workbook.getWorkbook(new File(filename.getText()));
				sheet = book.getSheet(0);
				rawID_Total = sheet.getRows(); // 获取行数
				for (int i = 1; i < rawID_Total; i++) {
					readExcel(sheet, i);
					initSearch(unedname, "bingSearch");
					Sub_ID = 0;
				}
				book.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}

		System.exit(0);
	}

	public static void run() throws IOException {
		try {
			input();
			Sheet sheet;
			Workbook book;
			try {
				book = Workbook.getWorkbook(new File(filename.getText()));
				sheet = book.getSheet(0);
				rawID_Total = sheet.getRows(); // 获取行数
				for (int i = 1; i < rawID_Total; i++) {
					readExcel(sheet, i);
					//initSearch(unedname, "bingSearch");
					initSearch(unedname, "googleSearch");
					Sub_ID = 0;
				}
				book.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e2) {
			JOptionPane.showMessageDialog(null, e2.getMessage());
		}

		System.exit(0);
	}

	public static int initSearch(String unedname, String engine) throws IOException {
		int flag = 0;
		if (engine.equals("bingSearch")) {
			URL = "http://cn.bing.com/search?q=" + unedname;
			bingSearch(URL, unedname);
			flag = 1;
		} else if (engine.equals("googleSearch")) {
			URL = "https://www.google.com。hk/search?q=" + unedname + "&num=10";
			googleSearch(URL, unedname);
			flag = 1;
		}

		return flag;
	}

	public static void input() throws IOException {
		JPanel panel = new JPanel(new GridLayout(0, 1));

		panel.add(filename);
		panel.add(new JLabel("Save into C:\\postdoc:"));

		int result = JOptionPane.showConfirmDialog(null, panel, "Search filename", 2, -1);
		if (result == 0) {
			return;
		}

		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
	}

	public static void googleSearch(String URL1, String unedname) throws IOException {

	    try{
	        URL url = new URL(URL1);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("GET");
	        InputStream in = conn.getInputStream();
	        Scanner scanner = new Scanner(in);
	         
	        while (scanner.hasNextLine()) {
	            System.out.println(scanner.nextLine());
	        }
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }

	}

	public static void bingSearch(String URL1, String unedname) throws IOException {
		Document doc = Jsoup.connect(URL1).timeout(0).get();
		Element body = doc.body();

		// Get the search result keylink contents
		Elements content = body.getElementsByClass("b_title");
		for (int i = 0; i < content.size(); i++) {
			// Get the inner keywords in results
			Elements Elements_h2 = content.get(i).getElementsByTag("h2");
			Elements links = Elements_h2.get(0).getElementsByTag("a");

			// Check out if the links are Linkedin or Facebook
			if (ifMatch(links, unedname).equals("B_0")) {
				// linkedin process
				for (Element link : links) {
					String linkHref = link.attr("href");
					try {
						loginLinkedin(linkHref);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			} else {
				for (Element link : links) {
					String linkHref = link.attr("href");
					try {
						getCV(linkHref);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	public static void getCV(String URL) throws IOException, SQLException {
		if (URL.substring((URL.length() - 4), URL.length()).equals(".pdf")) {
			// String res = downloadFromUrl(URL, getDir());
			// System.out.println(res);
			LoadSomething load = new LoadSomething();
			DownloadTask downloadTask = new DownloadTask(URL, getDir());
			// 开始下载，并设定超时限额为3毫秒
			load.beginToLoad(downloadTask, 60000, TimeUnit.MILLISECONDS);
			System.out.print("Mainpage" + URL);

		} else {
			try {
				Document doc = Jsoup.connect(URL).timeout(5000).get();
				Element body = doc.body();
				String str = body.text().toString();

				// iMatch method to check if including sensitive content
				boolean iContentMatch = iContentMatch(str);
				if (iContentMatch) {
					writefile(filePath(""), doc.toString(), false);
					Sub_ID++;
					Elements links = body.getElementsByTag("a");
					for (Element link : links) {
						String regEx = "(download\\s*CV)|(download\\s*C.V.)|resume|CV|C.V.|(View/Download C.V.)|(Curriculum Vitae)";
						// 编译正则表达式-忽略大小写的写法
						Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(link.text());
						while (matcher.find()) {
							String linkHref = link.attr("href");
							// Check if it is a relative path
							linkHref = processDLURL(URL, linkHref);

							System.out.print(linkHref);
							// String res = downloadFromUrl(linkHref, getDir());
							// System.out.println(res);
							LoadSomething load = new LoadSomething();
							DownloadTask downloadTask = new DownloadTask(linkHref, getDir());
							// 开始下载，并设定超时限额为3毫秒
							load.beginToLoad(downloadTask, 60000, TimeUnit.MILLISECONDS);
							System.out.print("Spage" + linkHref);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void loginLinkedin(String URL) throws IOException, SQLException {
		try {
			WebClient webClient = new WebClient();// 创建WebClient
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			// 获取页面
			HtmlPage page = webClient.getPage("https://www.linkedin.com/uas/login"); // 打开linkedin

			// 获得name为"session_key"的html元素
			HtmlElement usernameEle = page.getElementByName("session_key");
			// 获得id为"session_password"的html元素
			HtmlElement passwordEle = (HtmlElement) page.getElementById

			("session_password-login");
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
				HtmlPage page2 = webClient.getPage(URL);
				String pageXml = page2.asXml();
				Document doc2 = Jsoup.parse(pageXml);
				Element background_text = doc2.getElementById("background");
				Elements name = doc2.getElementsByClass("full-name");
				if (background_text != null) {
					// System.out.println(background_text.toString());

					// iMatch method to check if including sensitive content
					boolean iMatch = iContentMatch(background_text.toString());
					if (iMatch) {
						// String res = downloadFromUrl(URL, getDir());
						writefile(filePath("linkedin"), name.toString() + "<br>" + background_text.toString(), false);
						// writefile(filePath("linkedin"), result, false);
						Sub_ID++;
					}

					// write into database
					/*
					 * str_SQL =
					 * " INSERT INTO waverly.tsearch (ID,cliuid_2,unedname,Sub_ID,SearchResult) VALUES (88,"
					 * + Integer.valueOf(cliuid_2) + "," + "\"" + unedname +
					 * "\"" + "," + Sub_ID + "," + "\"" +
					 * background_text.toString().replace("\"","\\\"") + "\"" +
					 * ")";
					 * 
					 * try { Class.forName("com.mysql.jdbc.Driver");
					 * java.sql.Connection conn = DriverManager.getConnection(
					 * "jdbc:mysql://localhost:3306/waverly?user=root&password=197544"
					 * ); java.sql.Statement stmt = conn.createStatement();
					 * stmt.executeUpdate(str_SQL); Sub_ID++; } catch
					 * (SQLException e) { e.printStackTrace(); } catch
					 * (ClassNotFoundException e) { // TODO Auto-generated catch
					 * block e.printStackTrace(); }
					 */

					// 创建目录
					// str_m=str_trim.substring(0,
					// 4-cliuid_2.length())+cliuid_2;
					// writefile("c:/1.txt", background_text.toString(), false);
				} else {
					System.out.println("登陆失败");
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 向文本文件中写入内容
	public static void writefile(String path, String content, boolean append) {
		BufferedReader bufread;
		BufferedWriter bufwriter;
		File writefile;
		String filepath, filecontent, read;
		String readStr = "";
		try {
			boolean addStr = append; // 通过这个对象来判断是否向文本文件中追加内容
			filepath = path; // 得到文本文件的路径
			filecontent = content; // 需要写入的内容
			writefile = new File(filepath);
			if (writefile.exists() == false) // 如果文本文件不存在则创建它
			{
				writefile.createNewFile();
				writefile = new File(filepath); // 重新实例化
			}
			FileWriter filewriter = new FileWriter(writefile, addStr);
			// 删除原有文件的内容
			// 写入新的文件内容
			writefile.delete();
			filewriter.write(filecontent);
			filewriter.flush();
			filewriter.close();
		} catch (Exception d) {
			System.out.println(d.getMessage());
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			System.out.println("创建目录" + destDirName + "成功！");
			return true;
		} else {
			System.out.println("创建目录" + destDirName + "失败！");
			return false;
		}
	}

	public static String filePath(String site) {
		String filepath = "";
		cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
		dirName = "c:/postdoc/" + cliuid_2_unedname;
		createDir(dirName);
		// 创建文件
		if (site.equals("")) {
			filepath = dirName + "/" + cliuid_2_unedname + "_" + Sub_ID + ".html";
		} else {
			filepath = dirName + "/" + cliuid_2_unedname + "_" + Sub_ID + "_" + site + ".html";
		}

		return filepath;

	}

	public static String getDir() {
		cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
		dirName = "c:/postdoc/" + cliuid_2_unedname;
		createDir(dirName);
		return dirName;
	}

	public static void readExcel(Sheet sheet, int rawID) {
		Cell cell1, cell2;
		// System.out.println("*****");
		try {
			// 获取每一行的单元格
			cell1 = sheet.getCell(0, rawID);// （列，行）
			cell2 = sheet.getCell(1, rawID);

			if ("".equals(cell1.getContents()) != true) // 如果读取的数据为空
			{
				cliuid_2 = cell1.getContents();
				unedname = cell2.getContents();
				System.out.println(cell1.getContents() + " " + cell2.getContents());
			}

		} catch (Exception e) {
		}
	}

	public static String downloadFromUrl(String url, String dir) {
		try {
			URL httpurl = new URL(url);
			String fileName = getFileNameFromUrl(url);
			System.out.println(fileName);
			File f = new File(dir + "/" + fileName);

			FileUtils.copyURLToFile(httpurl, f);
		} catch (Exception e) {
			e.printStackTrace();
			return "Fault!";
		}
		return "Successful!";
	}

	public static String getFileNameFromUrl(String url) {
		String name = new Long(System.currentTimeMillis()).toString() + ".X";
		int index = url.lastIndexOf("/");
		if (index > 0) {
			name = url.substring(index + 1);
			if (name.trim().length() > 0) {
				return name;
			}
		}
		return name;
	}

	public static boolean iContentMatch(String str) {
		boolean iMatch = (str.toLowerCase().contains("background") || str.toLowerCase().contains("profile")
				|| str.toLowerCase().contains("resume") || str.toLowerCase().contains("curriculum vitae")
				|| str.toLowerCase().contains("cv") || str.toLowerCase().contains("biography"))
				&& (str.toLowerCase().contains("biotechnology") || str.toLowerCase().contains("medicine")
						|| str.contains("MIT") || str.contains("M.I.T")
						|| str.toLowerCase().contains("Massachusetts Institute of Technology")
						|| str.toLowerCase().contains("biology"));
		return iMatch;
	}

	public static String processDLURL(String url, String linkHref) {
		String head = url.substring(0, url.indexOf("/", 8));
		String head2 = url.substring(0, url.lastIndexOf("/"));
		// identify if the link is an absolute path
		if (linkHref.substring(0, 4).equals("http")) {
			return linkHref;
		} else if (linkHref.toString().substring(0, 1).equals("/")) {
			// if "/" at the begining, the download file is at the root of
			// domain
			linkHref = head + linkHref;
			return linkHref;
		} else {
			// these is a relative path, assemble the path where the page file
			// is at
			linkHref = head2 + "/" + linkHref;
			return linkHref;
		}
	}

	public static String ifMatch(Elements content, String unedname) throws IOException {
		String flag = "0";
		for (Element content_strong : content) {
			String strong_words = content_strong.getElementsByTag("a").text();
			if (strong_words.contains(" | LinkedIn")) {
				flag = "B_0";
				break;
			}
		}
		return flag;
	}
}
