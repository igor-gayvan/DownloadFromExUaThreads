/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Gayvan
 */
public class DownloadFromExUa {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, InterruptedException {

        try {
            getFiles();
        } catch (IOException ex) {
            Logger.getLogger(DownloadFromExUa.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    ;

//        console.working();

    private static void getFiles() throws MalformedURLException {
        URL playlistUrl = new URL("http://www.ex.ua/playlist/101529168.m3u");

        List<URL> fileList = new ArrayList<>();

        List<DownloadFile> downloadFileList = new ArrayList<>();

        DataSource ds = new DataSource(downloadFileList);
        DownloadFile.setIsReplaceAllFile(1);

        try (Scanner scanner = new Scanner(playlistUrl.openStream())) {
            while (scanner.hasNextLine()) {
                URL fileUrl = new URL(scanner.nextLine());
                fileList.add(fileUrl);
            }
        } catch (IOException ex) {
            Logger.getLogger(DownloadFromExUa.class.getName()).log(Level.SEVERE, null, ex);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(3);

        for (URL fileDownloadURL : fileList) {
            System.out.printf("%s%n", fileDownloadURL);

            DownloadFile df = new DownloadFile(fileDownloadURL);

            df.setDs(ds);
            df.setDownloadFileList(downloadFileList);

            threadPool.execute(df);
        }

        // Soft stoping...
        threadPool.shutdown();

        if (!threadPool.isTerminated()) {
            System.out.println("for cancel downloading press any key");
            try {
                System.in.read();
            } catch (IOException ex) {
                Logger.getLogger(DownloadFromExUa.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("CANCELING...");

            try {
                // Wait 1 second for tasks.
                if (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                    // Hard stop...
                    List<Runnable> shutdownNow = threadPool.shutdownNow();

                    for (Runnable runnable : shutdownNow) {
                        DownloadFile df = (DownloadFile) runnable;

                        System.out.println("Not downloaded: " + df.getFileURL());
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(DownloadFromExUa.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
