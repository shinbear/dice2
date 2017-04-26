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

public class Main2 {
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

	public static void main(String[] args) throws IOException {
		try {
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
			if (filename.getText().equalsIgnoreCase("")) {
				JOptionPane.showMessageDialog(null, "Please enter the file path.");
				filename.requestFocusInWindow();
				filename.setText("F:/jobs");
				input();
			}

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
				URL = "https://www.dice.com/jobs/" + q1 + l1 + dcs1 + jtype1 + radius1 + "-startPage-" + i
						+ "-limit-30-" + "-jobs.html";

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

		int result = JOptionPane.showConfirmDialog(null, panel, "Dice.com - Search Criteria", /* 153 */ 2, -1);
		if (result == 0) {
			return;
		}
		JOptionPane.showMessageDialog(frame, "Cancelled");
		System.exit(0);
	}

	public static int noofpages(String URL1) throws IOException {
		Document doc = Jsoup.connect(URL1).timeout(0).get();
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
}

/*
 * Location: E:\sistertask\dice\Dice-Crawler_V6\Dice-Crawler_V6.jar Qualified
 * Name: DiceCrawler.Main JD-Core Version: 0.6.2
 */