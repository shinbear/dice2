package DiceCrawler;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Main6_Linkedin {
	public static int Sub_ID = 0;
	public static int Excel_ID = 0;
	public static int Excel_ID_linkedin = 0;
	public static int rawID = 1;
	public static String cliuid_2 = "";
	public static String unedname = "";
	public static String fname = "";
	public static String lname = "";
	public static String labname = "";
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
	public static String url_temp = "";
	public static WebClient webClient;
	public static List<String> excel_row = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

	}

	public static void run() throws IOException {
		try {
			input();
			Sheet sheet;
			Workbook book;
			try {
				// delete old files to reset
				delete("C:/postdoc/result_linkedin.xls");
				delete("C:/postdoc/log.txt");

				// create result xls
				createDir("C:/postdoc/");
				createExcel("C:/postdoc/result_linkedin.xls");

				// create log txt
				ReadWriteFile.creatTxtFile();

				book = Workbook.getWorkbook(new File(filename.getText()));
				sheet = book.getSheet(0);
				// modifyExcel("C:/postdoc/result.xls","Modified",0,0);

				rawID_Total = sheet.getRows(); // »ñÈ¡ÐÐÊý
				webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER_11);// ´´½¨WebClient
				loginLinkedin();

				for (int i = 1; i < rawID_Total; i++) {
					rawID = i;
					readExcel(sheet, i);
					createDir("C:/postdoc/temp");

					// Write the ID info into xls
					excel_row.clear();
					modifyExcel("C:/postdoc/result_linkedin.xls", cliuid_2, Excel_ID_linkedin, rawID);
					modifyExcel("C:/postdoc/result_linkedin.xls", unedname, Excel_ID_linkedin + 1, rawID);
					modifyExcel("C:/postdoc/result_linkedin.xls", labname, Excel_ID_linkedin + 2, rawID);

					// write log
					ReadWriteFile.readTxtFile();
					ReadWriteFile.writeTxtFile(cliuid_2 + "  | " + unedname);

					// Execute search program
					// initSearch(unedname, "linkedinSearch");
					try {
						initSearch(unedname, "googleSearch");
					} catch (java.lang.IllegalArgumentException e) {
						e.printStackTrace();
					} finally {
						int rTime = ((int) (60 + Math.random() * (80 - 60 + 1))) * 1000;
						try {
							Thread.sleep(rTime);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					deleteAllFilesOfDir("C:/postdoc/temp");
					Sub_ID = 0;
					Excel_ID = 0;
					Excel_ID_linkedin = 0;
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
			cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
			dirName = "c:/postdoc/" + cliuid_2_unedname;
			createDir(dirName);
			bingSearch(URL, unedname);
			flag = 1;
		} else if (engine.equals("googleSearch")) {
			URL = "https://www.google.com/search?q=linkedin+MIT+intitle:(" + unedname + ")+OR+inurl:(" + unedname + ")";
			URL url = new URL(URL);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);

			cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
			// dirName = "c:/postdoc/" + cliuid_2_unedname;
			// createDir(dirName);
			System.out.println(URL);
			googleSearch(uri.toString(), unedname);

			if (Excel_ID == 0) {
				int rTime = ((int) (60 + Math.random() * (80 - 60 + 1))) * 1000;
				try {
					Thread.sleep(rTime);
				} catch (InterruptedException e1) { // TODO Auto-generated catch
													// // block
					e1.printStackTrace();
				}
				URL = "https://www.google.com/search?q=linkedin+" + labname + "intitle:(" + unedname + ")+OR+inurl:("
						+ unedname + ")";
				googleSearch(uri.toString(), unedname);
			}

			flag = 2;
		} else if (engine.equals("linkedinSearch")) {
			URL = "https://www.linkedin.com/search/results/index/?keywords=" + unedname
					+ "&origin=GLOBAL_SEARCH_HEADER";
			cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
			dirName = "c:/postdoc/" + cliuid_2_unedname;
			createDir(dirName);
			linkedinSearch(URL);

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

	public static void loginLinkedin() {
		try {

			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			// »ñÈ¡Ò³Ãæ
			HtmlPage page = webClient.getPage("https://www.linkedin.com/uas/login"); // ´ò¿ªlinkedin

			// »ñµÃnameÎª"session_key"µÄhtmlÔªËØ
			HtmlElement usernameEle = page.getElementByName("session_key");
			// »ñµÃidÎª"session_password"µÄhtmlÔªËØ
			HtmlElement passwordEle = (HtmlElement) page.getElementById

			("session_password-login");
			usernameEle.focus(); // ÉèÖÃÊäÈë½¹µã
			usernameEle.type("z_hao2017@sina.com"); // ÌîÐ´Öµ

			passwordEle.focus(); // ÉèÖÃÊäÈë½¹µã
			passwordEle.type("zh197544"); // ÌîÐ´Öµ
			// »ñµÃnameÎª"submit"µÄÔªËØ
			HtmlElement submitEle = page.getElementByName("signin");
			// µã»÷¡°µÇÂ½¡±
			page = submitEle.click();
			String result = page.asXml();// 获得click()后的html页面（包括标签）
			if (!result.contains("feed-tab-icon")) {
				System.out.println("***********Login Linkedin Fail*********");
				ReadWriteFile.readTxtFile();
				ReadWriteFile.writeTxtFile("***********Login Linkedin Fail*********");
			}
		} catch (

		FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void linkedinSearch(String URL) throws IOException {
		// login linkedin
		loginLinkedin();
		// write log
		ReadWriteFile.readTxtFile();
		ReadWriteFile.writeTxtFile("          linkedinSearch- " + URL);

		// modifyExcel("C:/postdoc/result_linkedin.xls", URL, 3 + Sub_ID,
		// rawID);
		Sub_ID++;
		try {
			webClient.getOptions().setJavaScriptEnabled(true); // ÆôÓÃJS½âÊÍÆ÷£¬Ä¬ÈÏÎªtrue
			webClient.getOptions().setCssEnabled(false); // ½ûÓÃcssÖ§³Ö
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			// jsÔËÐÐ´íÎóÊ±£¬ÊÇ·ñÅ×³öÒì³£
			webClient.getOptions().setTimeout(30000);

			HtmlPage page2 = webClient.getPage(URL);
			String pageXml = page2.asXml();
			Document doc2 = Jsoup.parse(pageXml);
			Elements links = doc2.getAllElements();

			// a.*?bÆ¥Åä×î¶ÌµÄ£¬ÒÔa¿ªÊ¼£¬ÒÔb½áÊøµÄ×Ö·û´®¡£Èç¹û°ÑËüÓ¦ÓÃÓÚaababµÄ»°£¬Ëü»áÆ¥ÅäaabºÍab
			String regEx = "publicIdentifier.*?\\,";

			List<String> ls = new ArrayList<String>();
			// ±àÒëÕýÔò±í´ïÊ½-ºöÂÔ´óÐ¡Ð´µÄÐ´·¨
			Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(links.text());

			while (matcher.find()) {
				if (!ls.contains(matcher.group())) {
					ls.add(matcher.group());
				}

				// ls.add(matcher.group());
			}

			// Get the linkedin user's link address and add into arrarylist
			for (int i = 0; i < ls.size(); i++) {
				String temp = ls.get(i).substring(ls.get(i).indexOf("publicIdentifier") + 19,
						ls.get(i).lastIndexOf(",") - 1);
				ls.set(i, temp);
			}

			// Check if the linkedin user contains the unedname info
			for (String str : ls) {
				ReadWriteFile.readTxtFile();
				ReadWriteFile.writeTxtFile("             linkedinSearchPage- http://www.linkedin.com/in/" + str + "/");
				if (unedname.toLowerCase().contains(str.substring(0, str.indexOf("-")).toLowerCase())
						|| unedname.toLowerCase().contains(str.toLowerCase())) {

					processLinkedin("http://www.linkedin.com/in/" + str + "/", 1);
				}
			}
		} catch (

		FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void googleSearch(String URL1, String unedname) throws IOException {
		String html = getUrlHtmlByHttpClient(URL1);
		Document doc = Jsoup.parse(html);
		// Document doc = Jsoup.connect(URL1).timeout(0).get();
		Element body = doc.body();

		Elements content = body.getElementsByClass("r");
		for (int i = 0; i < content.size(); i++) {
			Elements links = content.get(i).getElementsByTag("a");
			// Check out if the links are Linkedin or Facebook
			if (ifMatch(links, unedname).equals("B_0")) {
				// linkedin process
				for (Element link : links) {
					String linkHref = link.attr("href");
					try {
						linkHref = linkHref.substring(0, linkHref.indexOf("&")).replace("/url?q=", "");
						processLinkedin(linkHref, 0);

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
			/*
			 * else { for (Element link : links) { String linkHref =
			 * link.attr("href"); try { linkHref = linkHref.substring(0,
			 * linkHref.indexOf("&")).replace("/url?q=", ""); getCV(linkHref); }
			 * catch (SQLException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } break; } }
			 */
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
						processLinkedin(linkHref, 1);
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
		// write log
		ReadWriteFile.readTxtFile();
		ReadWriteFile.writeTxtFile("          webpage- " + URL);

		if (URL.substring((URL.length() - 4), URL.length()).equals(".pdf")) {
			// String res = downloadFromUrl(URL, getDir());
			// System.out.println(res);

			url_temp = getDir() + "/" + URL.substring(URL.lastIndexOf("/") + 1, URL.length());

			String pdf_temp = "C:/postdoc/temp" + "/" + URL.substring(URL.lastIndexOf("/") + 1, URL.length());

			LoadSomething load = new LoadSomething();
			// DownloadTask downloadTask = new DownloadTask(URL, getDir());
			DownloadTask downloadTask = new DownloadTask(URL, "C:/postdoc/temp");
			// ¿ªÊ¼ÏÂÔØ£¬²¢Éè¶¨³¬Ê±ÏÞ¶îÎª3ºÁÃë
			load.beginToLoad(downloadTask, 90000, TimeUnit.MILLISECONDS);

			System.out.println("PDFMainpage- " + URL);

			// read the content
			String strContent = readPdf(pdf_temp);

			// if the condition is satisfied, then keep the file OR delete
			// if (iFunctionMatch(strContent)) {
			////////////
			if (iFunctionMatch1(strContent)) {
				copyFile(pdf_temp, url_temp);
			}

		} else {
			try {
				// modifyExcel("C:/postdoc/result.xls", URL, 3 + Sub_ID, rawID);
				// writefile(filePath(""), doc.toString(), false);
				Sub_ID++;
				Document doc = Jsoup.connect(URL).timeout(5000).get();
				Element body = doc.body();
				String str = body.text().toString();

				// iMatch method to check if including sensitive content
				System.out.println("normalewebpage- " + URL);

				boolean iFunctionMatch2 = iFunctionMatch2(str);
				boolean iFunctionMatch3 = iFunctionMatch3(str, labname);
				////////////
				if (iFunctionMatch2) {
					if (!excel_row.contains(URL)) {
						// write the result into xls
						modifyExcel("C:/postdoc/result.xls", URL, 3 + Excel_ID, rawID);
						// writefile(filePath(""), doc.toString(), false);
						excel_row.add(URL);
						Sub_ID++;
						Excel_ID++;
					}
				} else if (iFunctionMatch3) {
					if (!excel_row.contains(URL)) {
						// write the result into xls
						modifyExcel("C:/postdoc/result.xls", URL, 3 + Excel_ID, rawID);
						// writefile(filePath(""), doc.toString(), false);
						excel_row.add(URL);
						Sub_ID++;
						Excel_ID++;
					}

				}

				Elements links = body.getElementsByTag("a");
				for (Element link : links) {
					String regEx = "(download\\s*CV)|(download\\s*C.V.)|resume|CV|C.V.|(View/Download C.V.)|(Curriculum Vitae)";
					// ±àÒëÕýÔò±í´ïÊ½-ºöÂÔ´óÐ¡Ð´µÄÐ´·¨
					Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(link.text());
					while (matcher.find()) {
						String linkHref = link.attr("href");

						if (linkHref.substring((linkHref.length() - 4), linkHref.length()).equals(".pdf")) {
							// Check if it is a relative path
							linkHref = processDLURL(URL, linkHref);

							// write log
							ReadWriteFile.readTxtFile();
							ReadWriteFile.writeTxtFile("          inner pdf- " + linkHref);

							url_temp = getDir() + "/"
									+ linkHref.substring(linkHref.lastIndexOf("/") + 1, linkHref.length());

							String pdf_temp = "C:/postdoc/temp" + "/"
									+ linkHref.substring(linkHref.lastIndexOf("/") + 1, linkHref.length());

							LoadSomething load = new LoadSomething();
							DownloadTask downloadTask = new DownloadTask(linkHref, "C:/postdoc/temp");
							// ¿ªÊ¼ÏÂÔØ£¬²¢Éè¶¨³¬Ê±ÏÞ¶îÎª3ºÁÃë
							load.beginToLoad(downloadTask, 90000, TimeUnit.MILLISECONDS);
							System.out.print("inner pdf- " + linkHref);

							// read the content
							String strContent = readPdf(pdf_temp);

							// if the condition is satisfied, then keep the
							// file OR delete
							/// if (iFunctionMatch1(strContent)) {
							/////////////////
							if (iFunctionMatch1(strContent)) {
								copyFile(pdf_temp, url_temp);
							}
						}

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void processLinkedin(String URL, int flag) throws IOException, SQLException {
		// login linedin
		// loginLinkedin();

		// write log
		ReadWriteFile.readTxtFile();
		ReadWriteFile.writeTxtFile("          webpage-linkedin- " + URL);

		// modifyExcel("C:/postdoc/result.xls", URL, 3 + Sub_ID, rawID);
		// Sub_ID++;
		try {
			/*
			 * HtmlPage page2 = webClient.getPage(URL); String pageXml =
			 * page2.asXml(); Document doc2 = Jsoup.parse(pageXml); // Element
			 * background_text = doc2.getElementById("background");
			 */
			// Elements name = doc2.getElementsByClass("full-name");
			if (flag == 0) {
				System.out.println("linkedin webpage- " + URL);
			} else if (flag == 1) {
				System.out.println("linkedinSearch webpage- " + URL);
			}

			/*
			 * if (doc2 != null) { // iMatch method to check if including
			 * sensitive content boolean iFunctionMatch2 =
			 * iFunctionMatch2(doc2.text());
			 */

			////////////////

			if (flag == 0) {
				if (!excel_row.contains(URL)) {
					modifyExcel("C:/postdoc/result_linkedin.xls", URL, 3 + Excel_ID, rawID);
					excel_row.add(URL);
					Excel_ID++;
					Sub_ID++;
				}
			} else if (flag == 1) {
				modifyExcel("C:/postdoc/result_linkedin.xls", URL, 3 + Excel_ID_linkedin, rawID);
				Sub_ID++;
				Excel_ID_linkedin++;
			}

			// write into database
			/*
			 * str_SQL =
			 * " INSERT INTO waverly.tsearch (ID,cliuid_2,unedname,Sub_ID,SearchResult) VALUES (88,"
			 * + Integer.valueOf(cliuid_2) + "," + "\"" + unedname + "\"" + ","
			 * + Sub_ID + "," + "\"" +
			 * background_text.toString().replace("\"","\\\"") + "\"" + ")";
			 * 
			 * try { Class.forName("com.mysql.jdbc.Driver"); java.sql.Connection
			 * conn = DriverManager.getConnection(
			 * "jdbc:mysql://localhost:3306/waverly?user=root&password=197544"
			 * ); java.sql.Statement stmt = conn.createStatement();
			 * stmt.executeUpdate(str_SQL); Sub_ID++; } catch (SQLException e) {
			 * e.printStackTrace(); } catch (ClassNotFoundException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */

			// ´´½¨Ä¿Â¼
			// str_m=str_trim.substring(0,
			// 4-cliuid_2.length())+cliuid_2;
			// writefile("c:/1.txt", background_text.toString(), false);

		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ?ò??±????t?DD′è??úèY
	public static void writefile(String path, String content, boolean append) {
		BufferedReader bufread;
		BufferedWriter bufwriter;
		File writefile;
		String filepath, filecontent, read;
		String readStr = "";
		try {
			boolean addStr = append; // Í¨¹ýÕâ¸ö¶ÔÏóÀ´ÅÐ¶ÏÊÇ·ñÏòÎÄ±¾ÎÄ¼þÖÐ×·¼ÓÄÚÈÝ
			filepath = path; // µÃµ½ÎÄ±¾ÎÄ¼þµÄÂ·¾¶
			filecontent = content; // ÐèÒªÐ´ÈëµÄÄÚÈÝ
			writefile = new File(filepath);
			if (writefile.exists() == false) // Èç¹ûÎÄ±¾ÎÄ¼þ²»´æÔÚÔò´´½¨Ëü
			{
				writefile.createNewFile();
				writefile = new File(filepath); // ÖØÐÂÊµÀý»¯
			}
			FileWriter filewriter = new FileWriter(writefile, addStr);
			// É¾³ýÔ­ÓÐÎÄ¼þµÄÄÚÈÝ
			// Ð´ÈëÐÂµÄÎÄ¼þÄÚÈÝ
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
			System.out.println("´´½¨Ä¿Â¼" + destDirName + "Ê§°Ü£¬Ä¿±êÄ¿Â¼ÒÑ¾­´æÔÚ");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// ´´½¨Ä¿Â¼
		if (dir.mkdirs()) {
			System.out.println("´´½¨Ä¿Â¼" + destDirName + "³É¹¦£¡");
			return true;
		} else {
			System.out.println("´´½¨Ä¿Â¼" + destDirName + "Ê§°Ü£¡");
			return false;
		}
	}

	/**
	 * É¾³ýÎÄ¼þ£¬¿ÉÒÔÊÇÎÄ¼þ»òÎÄ¼þ¼Ð
	 * 
	 * @param fileName
	 *            ÒªÉ¾³ýµÄÎÄ¼þÃû
	 * @return É¾³ý³É¹¦·µ»Øtrue£¬·ñÔò·µ»Øfalse
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("É¾³ýÎÄ¼þÊ§°Ü:" + fileName + "²»´æÔÚ£¡");
			return false;
		} else {
			if (file.isFile())
				return deleteFile(fileName);
			return false;
		}
	}

	/**
	 * É¾³ýµ¥¸öÎÄ¼þ
	 * 
	 * @param fileName
	 *            ÒªÉ¾³ýµÄÎÄ¼þµÄÎÄ¼þÃû
	 * @return µ¥¸öÎÄ¼þÉ¾³ý³É¹¦·µ»Øtrue£¬·ñÔò·µ»Øfalse
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// Èç¹ûÎÄ¼þÂ·¾¶Ëù¶ÔÓ¦µÄÎÄ¼þ´æÔÚ£¬²¢ÇÒÊÇÒ»¸öÎÄ¼þ£¬ÔòÖ±½ÓÉ¾³ý
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("É¾³ýµ¥¸öÎÄ¼þ" + fileName + "³É¹¦£¡");
				return true;
			} else {
				System.out.println("É¾³ýµ¥¸öÎÄ¼þ" + fileName + "Ê§°Ü£¡");
				return false;
			}
		} else {
			System.out.println("É¾³ýµ¥¸öÎÄ¼þÊ§°Ü£º" + fileName + "²»´æÔÚ£¡");
			return false;
		}
	}

	public static String filePath(String site) {
		String filepath = "";
		cliuid_2_unedname = str_trim.substring(0, 4 - cliuid_2.length()) + cliuid_2 + "_" + unedname;
		dirName = "c:/postdoc/" + cliuid_2_unedname;
		createDir(dirName);
		// ´´½¨ÎÄ¼þ
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
		Cell cell1, cell2, cell7, cell3, cell5;
		// System.out.println("*****");
		try {
			// »ñÈ¡Ã¿Ò»ÐÐµÄµ¥Ôª¸ñ
			cell1 = sheet.getCell(0, rawID);// £¨ÁÐ£¬ÐÐ£©
			cell2 = sheet.getCell(1, rawID);
			cell3 = sheet.getCell(2, rawID);
			cell5 = sheet.getCell(4, rawID);
			cell7 = sheet.getCell(6, rawID);

			if ("".equals(cell1.getContents()) != true) // Èç¹û¶ÁÈ¡µÄÊý¾ÝÎª¿Õ
			{
				cliuid_2 = cell1.getContents();
				unedname = cell2.getContents();
				fname = cell3.getContents();
				lname = cell5.getContents();
				labname = cell7.getContents();
				System.out.println(
						rawID + " " + cell1.getContents() + " " + cell2.getContents() + " " + cell7.getContents());
			}

		} catch (Exception e) {
		}
	}

	public static void createExcel(String filepath) {
		try {
			// ´ò¿ªÎÄ¼þ
			WritableWorkbook book = Workbook.createWorkbook(new File(filepath));

			// Éú³ÉÃûÎª¡°µÚÒ»Ò³¡±µÄ¹¤×÷±í£¬²ÎÊý0±íÊ¾ÕâÊÇµÚÒ»Ò³
			WritableSheet sheet = book.createSheet("sheet0", 0);

			// ÔÚLabel¶ÔÏóµÄ¹¹Ôì×ÓÖÐÖ¸Ãûµ¥Ôª¸ñÎ»ÖÃÊÇµÚÒ»ÁÐµÚÒ»ÐÐ(0,0)
			Label label1 = new Label(0, 0, "cliuid_2");
			Label label2 = new Label(1, 0, "unedname");
			Label label3 = new Label(2, 0, "labname");
			Label label4 = new Label(3, 0, "Address1");
			Label label5 = new Label(4, 0, "Address2");
			Label label6 = new Label(5, 0, "Address3");
			Label label7 = new Label(6, 0, "Address4");
			Label label8 = new Label(7, 0, "Address5");
			Label label9 = new Label(8, 0, "Address6");

			// ½«¶¨ÒåºÃµÄµ¥Ôª¸ñÌí¼Óµ½¹¤×÷±íÖÐ
			sheet.addCell(label1);
			sheet.addCell(label2);
			sheet.addCell(label3);
			sheet.addCell(label4);
			sheet.addCell(label5);
			sheet.addCell(label6);
			sheet.addCell(label7);
			sheet.addCell(label8);
			sheet.addCell(label9);

			// Ð´ÈëÊý¾Ý²¢¹Ø±ÕÎÄ¼þ
			book.write();
			book.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void modifyExcel(String filepath, String modContent, int x, int y) {
		File f = new File(filepath);
		try {
			Workbook wb = Workbook.getWorkbook(f);//
			WritableWorkbook book = wb.createWorkbook(f, wb);
			// Sheet sheet = wb.getSheet(0); // »ñµÃµÚÒ»¸ö¹¤×÷±í¶ÔÏó
			WritableSheet st = book.getSheet(0);
			Cell cel = st.getCell(0, 0);
			if (cel.getType() == CellType.LABEL) {
				Label label = new Label(x, y, modContent);
				// Yes ÊÇÐèÒªÌî³äµ¥Ôª¸ñµÄÄÚÈÝ
				st.addCell(label);
			}

			book.write();
			book.close();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
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
				|| str.toLowerCase().contains(" cv ") || str.toLowerCase().contains("biography"))
				&& (str.toLowerCase().contains("biotechnology") || str.toLowerCase().contains("medicine")
						|| str.contains(" MIT ") || str.contains(" M.I.T ")
						|| str.toLowerCase().contains("Massachusetts Institute of Technology")
						|| str.toLowerCase().contains("biology"));
		return iMatch;
	}


	public static boolean iFunctionMatch1(String str) {
		boolean iMatch1 = (str.toLowerCase().contains("curriculum vitae") || str.toLowerCase().contains("resume")
				|| str.contains("CV") || str.toLowerCase().contains("biography")
				|| str.toLowerCase().contains("education"))
				&& (str.contains("PhD") || str.contains("Ph.D") || str.toLowerCase().contains("doctor")
						|| str.contains("M.D.") || str.toLowerCase().contains("postdoctoral")
						|| str.toLowerCase().contains("postdoc"))
				&& (str.toLowerCase().contains("massachusetts institute of technology") || str.contains("MIT,")
						|| str.contains(",MIT") || str.contains("M.I.T.") || str.contains("MIT ")
						|| str.toLowerCase().contains(labname.toLowerCase()) || str.toLowerCase().contains("whitehead")
						|| str.toLowerCase().contains("mass inst technol"))
				|| str.toLowerCase().contains("massachusetts institute of biology")
						&& (str.toLowerCase().contains("biotechnology") || str.toLowerCase().contains("medicine")
								|| str.toLowerCase().contains("biology") || str.toLowerCase().contains("biological")
								|| str.toLowerCase().contains("chemistry") || str.toLowerCase().contains("medical")
								|| str.toLowerCase().contains("chemical") || str.toLowerCase().contains("cancer"));
		return iMatch1;
	}

	public static boolean iFunctionMatch2(String str) {
		boolean iMatch2 = (str.contains("PhD") || str.contains("Ph.D") || str.toLowerCase().contains("doctor")
				|| str.contains("M.D.") || str.toLowerCase().contains("postdoctoral")
				|| str.toLowerCase().contains("postdoc"))
				&& (str.toLowerCase().contains("massachusetts institute of technology") || str.contains(" MIT")
						|| str.contains("M.I.T.") || str.contains("MIT ") || str.contains("MIT,")
						|| str.contains(",MIT") || str.toLowerCase().contains(labname.toLowerCase())
						|| str.toLowerCase().contains("whitehead"))
				&& (str.toLowerCase().contains("biotechnology") || str.toLowerCase().contains("medicine")
						|| str.toLowerCase().contains("biology") || str.toLowerCase().contains("biological")
						|| str.toLowerCase().contains("chemistry") || str.toLowerCase().contains("medical")
						|| str.toLowerCase().contains("chemical") || str.toLowerCase().contains("cancer"));

		return iMatch2;
	}

	public static boolean iFunctionMatch3(String str, String labname) {
		boolean iMatch3 = (str.contains("PhD") || str.contains("Ph.D") || str.toLowerCase().contains("doctor")
				|| str.contains("M.D.") || str.toLowerCase().contains("postdoctoral")
				|| str.toLowerCase().contains("postdoc"))
				&& (str.toLowerCase().contains("biotechnology") || str.toLowerCase().contains("medicine")
						|| str.toLowerCase().contains("biology") || str.toLowerCase().contains("biological")
						|| str.toLowerCase().contains("chemistry") || str.toLowerCase().contains("medical")
						|| str.toLowerCase().contains("chemical") || str.toLowerCase().contains("cancer"))
				&& (str.toLowerCase().contains(labname.toLowerCase())
						|| str.toLowerCase().contains("whitehead"));

		return iMatch3;
	}

	// check if the result links are special websites
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

	public static String readPdf(String URL) {

		File pdfFile = new File(URL);
		PDDocument document = null;
		try {
			// ·½Ê½Ò»£º
			/**
			 * InputStream input = null; input = new FileInputStream( pdfFile );
			 * //¼ÓÔØ pdf ÎÄµµ PDFParser parser = new PDFParser(new
			 * RandomAccessBuffer(input)); parser.parse(); document =
			 * parser.getPDDocument();
			 **/

			// ·½Ê½¶þ£º
			document = PDDocument.load(pdfFile);

			// »ñÈ¡Ò³Âë
			int pages = document.getNumberOfPages();

			// ¶ÁÎÄ±¾ÄÚÈÝ
			PDFTextStripper stripper = new PDFTextStripper();
			// ÉèÖÃ°´Ë³ÐòÊä³ö
			stripper.setSortByPosition(true);
			stripper.setStartPage(1);
			stripper.setEndPage(pages);
			String content = stripper.getText(document);
			// System.out.println(content);
			return content;
		} catch (Exception e) {
			System.out.println(e);
			return "";
		}

	}

	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // ÎÄ¼þ´æÔÚÊ±
				InputStream inStream = new FileInputStream(oldPath); // ¶ÁÈëÔ­ÎÄ¼þ
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // ×Ö½ÚÊý ÎÄ¼þ´óÐ¡
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("¸´ÖÆµ¥¸öÎÄ¼þ²Ù×÷³ö´í");
			e.printStackTrace();

		}

	}

	public static void deleteAllFilesOfDir(String folderpath) {
		File path = new File(folderpath);
		if (!path.exists())
			return;
		if (path.isFile()) {
			path.delete();
			return;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAllFilesOfDir(files[i].toString());
		}
		path.delete();
	}

	// Get the search result html from google engine
	public static String getUrlHtmlByHttpClient(String url) {
		String searchHtml = null;
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
				System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
				System.err.println(df.format(new Date()) + "  --Method failed: " + getMethod.getStatusLine());
				ReadWriteFile.readTxtFile();
				ReadWriteFile.writeTxtFile(df.format(new Date()) + "  --Method failed: " + getMethod.getStatusLine());
			}

			InputStream bodyIs = getMethod.getResponseBodyAsStream();//
			System.out.println("get reoponse body stream:" + bodyIs);

			// 如果中文乱码 修改字符集
			// BufferedReader br = new BufferedReader(
			// new InputStreamReader(bodyIs,"GBK"));
			BufferedReader br = new BufferedReader(new InputStreamReader(bodyIs));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			searchHtml = sb.toString();
			return searchHtml;
		} catch (HttpException e) {
			System.out.println("Please check your http address!");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			getMethod.releaseConnection();
		}

	}

	public static String identifMaxStr(String str) {
		// String str = "A. Hughes Goldie Waverly Ding A. Raymond Frackelton,
		// Jr.";
		String[] strs = str.split("[.,\\s+]");
		String strMax = "";
		int maxNum = 0;
		for (String s : strs) {
			if (s.length() > maxNum) {
				maxNum = s.length();
				strMax = s;
				System.out.println(maxNum);
			}
		}
		return strMax;

	}

}
