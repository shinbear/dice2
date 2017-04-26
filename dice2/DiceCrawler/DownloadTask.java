package DiceCrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;

class DownloadTask implements Runnable {
    private String url;
    private String dir;
    // 接收在run方法中捕获的异常，然后自定义方法抛出异常
    private Throwable exception;
    //是否关闭此下载任务
    private boolean isStop = false;
    
    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    public DownloadTask(String url, String dir) {
        this.url = url;
        this.dir = dir;
    }

    /**
     * 下载大数据
     * 
     * @param filename
     * @throws FileNotFoundException
     *             , IOException
     */
    private void download() throws FileNotFoundException, IOException {
		try {
			URL httpurl = new URL(url);
			String fileName = getFileNameFromUrl(url);
			System.out.println(fileName);
			File f = new File(dir + "/" + fileName);
			FileUtils.copyURLToFile(httpurl, f);
		} catch (Exception e) {
			e.printStackTrace();

		}
		

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

    public void throwException() throws FileNotFoundException, IOException {
        if (exception instanceof FileNotFoundException)
            throw (FileNotFoundException) exception;
        if (exception instanceof IOException)
            throw (IOException) exception;
    }

    @Override
    public void run() {
        try {
            download();
        } catch (FileNotFoundException e) {
            exception = e;
        } catch (IOException e) {
            exception = e;
        }

    }
}
