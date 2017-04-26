package DiceCrawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoadSomething {
    // 线程池服务接口
    private ExecutorService executor;

    
    public LoadSomething() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void beginToLoad(DownloadTask task, long timeout,
            TimeUnit timeType) {
        Future<?> future = executor.submit(task);
        try {
            future.get(timeout, timeType);
            task.throwException();
        } catch (InterruptedException e) {
            System.out.println("下载任务已经取消");
        } catch (ExecutionException e) {
            System.out.println("下载中发生错误，请重新下载");
        } catch (TimeoutException e) {
            System.out.println("下载超时，请更换下载点");
        } catch (FileNotFoundException e) {
            System.out.println("请求资源找不到");
        } catch (IOException e) {
            System.out.println("数据流出错");
        } finally {
            task.setStop(true);
           
            // 因为这里的下载测试不用得到返回结果，取消任务不会影响结果
            future.cancel(true);         
        }
    }
}