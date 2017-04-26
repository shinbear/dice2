package DiceCrawler;

import java.awt.FlowLayout;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JPanel;  
  
public class Test extends JFrame {  
    SheThread thread = null;  
  
    public Test() {  
        try {  
            createFrame();  
            }  
            catch(Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    private void createFrame() throws IOException {  

        JPanel jp = new JPanel(new FlowLayout());  
        this.add(jp);  
  
        JButton jbStart = new JButton("start ");  
        JButton jbEnd = new JButton("stop");  
        //jp.add(jbStart);  
        jp.add(jbEnd);  
  
        this.setSize(300, 100);  
        this.setVisible(true);  
        this.setResizable(false);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
  
        jbStart.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                if (thread != null)  
                    thread.stop();  
                thread = new SheThread();  
                thread.start();  
            }  
        });  
        jbEnd.addActionListener(new ActionListener() {  
            public void actionPerformed(ActionEvent e) {  
                if (thread != null)  
                    thread.stop(); 
                	System.exit(0);
                thread = null;  
            }  
        });  
        
        Main6T.run();
    }  
  
    public static void main(String[] args) {  
        new Test().show();  
    }  
  
}  
  
class SheThread extends Thread {  
    public SheThread() {  
    }  
  
    public void run() {  
        while (true) {  
            try {  
                sleep(1000);  
            } catch (InterruptedException e) {  
            }  
            System.out.println("this is a test!");  
        }  
    }  
  
}  