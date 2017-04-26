package DiceCrawler;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main
 {
	public static int i = 0;
	public static int j = 0;
	public static int sizemain = 0;
	public static int sizeref = 0;
	public static int pages = 0;
	public static JTextField searchstring = new JTextField();
	public static JTextField location = new JTextField();
	public static JTextField filename = new JTextField("F:/Jobs");
	public static String URL = "";
	public static String q;
	public static String dcs;
	public static String jtype;
	public static String radius;
	public static String l;
	public static String dc = "";
	public static String[] compseg = { "Recruiter", "DirectHire", "None" };
	public static String[] r = { "30", "5", "10", "20", "40", "50", "75", "100" };
	public static String[] emptype = { "Full Time", "Part Time", "Contracts", "None" };
	
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

	
	 public static void main(String[] args) throws IOException
	 {
		 try
		 {
			input();
			
			q = searchstring.getText();
			l = location.getText();
			jtype = combo1.getSelectedItem().toString();
			dcs = combo2.getSelectedItem().toString();
			
			radius = comborad.getSelectedItem().toString();
			
			String q1 = "q-" + q;
			String l1 = "-l-" + l;
			String jtype1 = "-jtype-" + jtype;
			String dcs1 = "-dcs-" + dcs;
			String radius1 = "-radius-" + radius;
			
			if (l.isEmpty())/* 76 */ l1 = "";
			if (dcs.equalsIgnoreCase("None"))/* 78 */ dcs1 = "";
			if (jtype.equalsIgnoreCase("None"))/* 80 */ jtype1 = "";
			if (filename.getText().equalsIgnoreCase(""))
			 {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("F:/jobs");
				input();
				 }
			
			URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-jobs.html";
			
			pages = noofpages(URL);
			 try
			 {
				/* 96 */ writer = new PrintWriter(filename.getText() + ".xls", "UTF-8");
				 }
			 catch (Exception e1)
			 {
				/* 100 */ JOptionPane.showMessageDialog(null,
						"File already open with same path & file name. Please close it & re-run the application");
				/* 101 */ writer.close();
				 }
			
			/* 104 */ writer.println(
					"Easy Apply\tAssoc. Position ID\tDice ID\tPosition ID\tJob Title\tEmployer\tJob Description\tLocation\tPosted\tKeyword1\tKeyword2\tKeyword3\tKeyword4\tcomlink\tposiCount\tcompanyOverview\tcompanyWebsite\tquickFacts\teasyApply2");
			
			/* 106 */ for (i = 1; i <= pages; i += 1)
			 {
				/* 108 */ URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-startPage-" + i
						+ "-limit-30-" + "-jobs.html";
				
				/* 110 */ processPage(URL, 0);
				 }
			
			/* 113 */ String[] URLprocess = urllist.split("\n");
			
			/* 115 */ i = 0;
			/* 116 */ sizemain = URLprocess.length;
			
			/* 118 */ while (i < sizemain)
			 {
				/* 120 */ parser(URLprocess[(i++)], "None");
				 }
			
			/* 123 */ writer.close();
			
			/* 125 */ JOptionPane.showMessageDialog(frame,
					"Downloading over. Data ready in " + filename.getText() + ".xls");
			 }
		 catch (Exception e2)
		 {
			/* 129 */ JOptionPane.showMessageDialog(null, e2.getMessage());
			 }
		
		/* 132 */ System.exit(0);
		 }

	
	 public static void input() throws IOException
	 {
		/* 138 */ JPanel panel = new JPanel(new GridLayout(0, 1));
		/* 139 */ panel.add(new JLabel("Search string:"));
		/* 140 */ panel.add(searchstring);
		/* 141 */ panel.add(new JLabel("Location:"));
		/* 142 */ panel.add(location);
		/* 143 */ panel.add(new JLabel("Radius(miles):"));
		/* 144 */ panel.add(comborad);
		/* 145 */ panel.add(new JLabel("Employment Type:"));
		/* 146 */ panel.add(combo1);
		/* 147 */ panel.add(new JLabel("Company Segment:"));
		/* 148 */ panel.add(combo2);
		/* 149 */ panel.add(new JLabel("File path to store results (without extention):"));
		/* 150 */ panel.add(filename);
		
		/* 152 */ int result = JOptionPane.showConfirmDialog(null, panel, "Dice.com - Search Criteria", /* 153 */ 2,
				-1);
		/* 154 */ if (result == 0) {
			/* 155 */ return;
			 }
		/* 157 */ JOptionPane.showMessageDialog(frame, "Cancelled");
		/* 158 */ System.exit(0);
		 }

	
	 public static int noofpages(String URL1) throws IOException
	 {
		/* 164 */ Document doc = Jsoup.connect(URL1).timeout(0).get();
		/* 165 */ Element header = doc.head();
		/* 166 */ Elements script = header.select("script");
		
		/* 168 */ Pattern p = Pattern.compile("(?is)pageCount : \\d(.+?)");
		
		/* 170 */ Matcher m = p.matcher(script.html());
		
		/* 172 */ String pc = null;
		/* 173 */ String pc1 = null;
		
		/* 175 */ while (m.find())
		 {
			/* 177 */ pc = m.group();
			 }
		/* 179 */ pc1 = pc.replaceAll("\\D+", "");
		/* 180 */ int pc2 = Integer.parseInt(pc1);
		/* 181 */ return pc2;
		 }

	public static void processPage(String URL1, int flag) throws IOException {
		try {
			Document doc = Jsoup.connect(URL1).timeout(5000).get();

			Elements questions = doc.select("a[href]");
			Elements easyApply = doc.getElementsByClass("list-inline");
			// int easylength = easyApply.size()/2;
			if (flag == 0) {
				String tag = "";
				String total_easyflag = "";
				for (Element link1 : easyApply) {

					tag = link1.getElementsByClass("easyApply").toString();
					if (tag.equals("")) {
						total_easyflag = total_easyflag + "N";
					} else {
						total_easyflag = total_easyflag + "Y";
					}

				}

				int i = 0;
				for (Element link : questions) {
					if ((link.attr("href").contains("jobs/detail")) || (link.attr("href").contains("job/result"))) {
						if (!allurllist.contains(link.attr("abs:href"))) {
							urllist = urllist + link.attr("abs:href") + "||" + total_easyflag.charAt(i) + "\n";
							i++;
							allurllist += urllist;
						}
					}
				}
				System.out.print(allurllist);
			} else if (flag == 1) {

				String tag_sim = "";
				String total_easyflag_sim = "";
				for (Element link1 : easyApply) {

					tag_sim = link1.getElementsByClass("easyApply").toString();
					if (tag_sim.equals("")) {
						total_easyflag_sim = total_easyflag_sim + "S";
					} else {
						total_easyflag_sim = total_easyflag_sim + "Y";
					}

				}

				int i = 0;
				for (Element link : questions) {
					if ((link.attr("href").contains("jobs/detail")) || (link.attr("href").contains("job/result"))) {
						if (!allurllist_sim.contains(link.attr("abs:href"))) {
							urllist_sim = urllist_sim + link.attr("abs:href") + "||" + total_easyflag_sim.charAt(i)
									+ "\n";
							i++;
							allurllist_sim += urllist_sim;
						}
					}
				}

			}

		} catch (Exception localException) {
		}
	}

	 public static void parser(String URL1, String refPos) throws IOException
	 {
		 try
		 {
			String URL2 = URL1.substring(0, URL1.indexOf("||"));
			String easytag = URL1.substring(URL1.indexOf("||") + 2);
			/* 213 */ Document doc1 = Jsoup.connect(URL2).timeout(0).get();
			/* 214 */ Element body = doc1.body();
			
			/* 216 */ String jobTitle = body.select("h1.jobTitle").text();
			/* 217 */ String loc = body.select("li.location").text();
			/* 218 */ String posted = body.select("li.posted").text();
			
			/* 220 */ Elements keywordcase = doc1.select("div.iconsiblings");
			/* 221 */ String keyword1 = keywordcase.first().text();
			/* 222 */ String keyword2 = ((Element) keywordcase.get(1)).text();
			/* 223 */ String keyword3 = ((Element) keywordcase.get(2)).text();
			/* 224 */ String keyword4 = ((Element) keywordcase.get(3)).text();
			/* 225 */ String jobdesc = body.select("div.highlight-black").first().text();
			
			/* 228 */ String employer = body.select("li.employer").text();
			
			/* 231 */ Elements dicelink = doc1.select("a.dice-btn-link");
			/* 232 */ String comlink = ((Element) dicelink.get(2)).attr("abs:href");
			
			/// * 235 */ boolean tag = doc1.getElementById("easyApply") == null;
			/// * 236 */ String easytag = "";
			/// * 237 */ if (!tag) {
			/// * 238 */ easytag = "Y";
			/// * */ }
			
			/* 241 */ URL url = new URL(URL2);
			
			/* 243 */ String ID1 = url.getPath();
			/* 244 */ String[] ID = ID1.split("/");
			/* 245 */ int pathArrSize = ID.length;
			
			/* 247 */ String posiCount = parser2(comlink)[0];
			/* 248 */ String companyOverview = parser2(comlink)[1];
			/* 249 */ String companyWebsite = parser2(comlink)[2];
			/* 250 */ String quickFacts = parser2(comlink)[3];
					  String easyApply2 = parser2(comlink)[4];
			
			/* 252 */ if (refPos.compareTo("None") == 0)
			 {
				/* 254 */ writer.println(easytag + "\t" + ID[(pathArrSize - 1)] + "\t" + ID[(pathArrSize - 2)] + "\t"
						+ ID[(pathArrSize - 1)] + "\t" +
						/* 255 */ jobTitle + "\t" + employer + "\t" + jobdesc + "\t" + loc + "\t" + posted +
						/* 256 */ "\t" + keyword1 + "\t" + keyword2 + "\t" + keyword3 + "\t" + keyword4 + "\t" +
						/* 257 */ comlink + "\t" + posiCount + "\t" + companyOverview + "\t" + companyWebsite +
						/* 258 */ "\t" + quickFacts + "\t" + easyApply2);
				 }
			 else
			 {
				/* 262 */ writer
						.println(easytag + "\t" + refPos + "\t" + ID[(pathArrSize - 2)] + "\t" + ID[(pathArrSize - 1)] +
				/* 263 */ "\t" + jobTitle + "\t" + employer + "\t" + jobdesc + "\t" + loc + "\t" + posted + "\t"
								+ keyword1 +
								/* 264 */ "\t" + keyword2 + "\t" + keyword3 + "\t" + keyword4 + "\t" + comlink +
								/* 265 */ "\t" + posiCount + "\t" + companyOverview + "\t" + companyWebsite +
								/* 266 */ "\t" + quickFacts + "\t" + easyApply2);
				 }
			
			/* 269 */ String simpos = body.select("h4.poistionat").text();
			
			/* 271 */ easytag = "";
			
			/* 273 */ if ((simpos.equalsIgnoreCase("Similar Positions")) && (refPos.equalsIgnoreCase("None")))
			 {
				/* 275 */ urllist_sim = "";
				/* 276 */ processPage(URL2, 1);
				
				/* 278 */ String[] URLprocess_sim = urllist_sim.split("\n");
				
				/* 280 */ sizeref = URLprocess_sim.length;
				
				/* 282 */ int i = 0;
				
				/* 284 */ while (i < sizeref)
				 {
					/* 286 */ parser(URLprocess_sim[(i++)], ID[(pathArrSize - 1)]);
					 }
				 }
			
			/* 290 */ throw new Exception();
			 }
		 catch (Exception localException)
		 {
			 }
		 }

	
	 public static String[] parser2(String URL1) throws IOException
	 {
		/* 301 */ String[] str = new String[4];
		 try
		 {
			/* 304 */ Document doc1 = Jsoup.connect(URL1).timeout(0).get();
			/* 305 */ Element body = doc1.body();
			
			/* 307 */ String posiCountTemp = body.select(".posiCount").text();
			/* 308 */ String posiCount = posiCountTemp.substring(posiCountTemp.indexOf("f") + 1);
			/* 309 */ String companyOverview = body.select("div.compant-block").text();
			/* 310 */ String companyWebsite = ((Element) body.select(".undeline_URL").get(0)).attr("abs:href");
			/* 311 */ String StrquickFacts = "";
			/* 312 */ Elements companyHeader = body.select(".clabel,.ctxt");
			/* 313 */ for (Element quckFacts : companyHeader)
			 {
				/* 315 */ if (!quckFacts.hasAttr("href"))
				 {
					/* 321 */ StrquickFacts = StrquickFacts + "||" + quckFacts.text();
					 }
				
				 }
			  		  String easyApply2 = body.select(".easyApply").text();
			/* 326 */ String[] companyParse = new String[5];
			/* 327 */ companyParse[0] = posiCount;
			/* 328 */ companyParse[1] = companyOverview;
			/* 329 */ companyParse[2] = companyWebsite;
			/* 330 */ companyParse[3] = StrquickFacts;
					  if (easyApply2.equals(""))
					  {
						  companyParse[4]="N";
					  }
					  else
					  {
						  companyParse[4]="Y";
					  }
					  
			/* 331 */ return companyParse;
			 }
		 catch (Exception localException)
		 {
			/* 338 */ String[] companyParse = new String[4];
			/* 339 */ companyParse[0] = "error";
			/* 340 */ companyParse[1] = "error";
			/* 341 */ companyParse[2] = "error";
			/* 342 */ companyParse[3] = "error";
					  companyParse[4] = "error";
			/* 343 */ return companyParse;
			 }
		 }
	 }

/*
 * Location: E:\sistertask\dice\Dice-Crawler_V6\Dice-Crawler_V6.jar Qualified
 * Name: DiceCrawler.Main JD-Core Version: 0.6.2
 */