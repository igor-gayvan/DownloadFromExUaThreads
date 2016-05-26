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
    public static void main(String[] args) throws MalformedURLException {
        Console console = new Console(System.in);

        console.addActionListener(new ActionListener() {
            // Выход
            @Override
            public void exitAction() {
                System.exit(0);
            }

            @Override
            public void getUrl4DownloadAction() {
                try {
                    getFiles(new URL(console.getInputText()));
                } catch (IOException ex) {
                    Logger.getLogger(DownloadFromExUa.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void confirmReplaceFiletAction() {
                System.exit(0);
            }
        });

        console.working();
    }
    
    public static void getFiles(URL playlistUrl) throws IOException {
//        URL playlistUrl = new URL("http://www.ex.ua/playlist/17427869.m3u");
        List<URL> fileList = new ArrayList<>();

        List<DownloadFile> downloadFileList = new ArrayList<>();

        DataSource ds = new DataSource(downloadFileList);

        DownloadFile.setIsReplaceAllFile(0);

        ShowData.ShowListAlreadyDownloadFiles(downloadFileList);

        try (Scanner scanner = new Scanner(playlistUrl.openStream())) {
            while (scanner.hasNextLine()) {
                URL fileUrl = new URL(scanner.nextLine());
                fileList.add(fileUrl);
            }
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        for (URL fileDownloadURL : fileList) {
            System.out.printf("%s%n", fileDownloadURL);

            DownloadFile df = new DownloadFile(fileDownloadURL);
            
            df.setDs(ds);
            df.setDownloadFileList(downloadFileList);

            threadPool.execute(df);
        }

//        for (String url : urls) {
//            try {
//                DownloadTask dt = new DownloadTask(storage, new URL(url));
//
////                new Thread(dt).start();
//            } catch (MalformedURLException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        // Soft stoping...
//        threadPool.shutdown();
//
//        if (!threadPool.isTerminated()) {
//            System.out.println("for cancel downloading press any key");
//            try {
//                System.in.read();
//            } catch (IOException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            System.out.println("CANCELING...");
//
//            try {
//                // Wait 1 second for tasks.
//                if (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
//                    // Hard stop...
//                    List<Runnable> shutdownNow = threadPool.shutdownNow();
//
//                    for (Runnable runnable : shutdownNow) {
//                        DownloadTask dt = (DownloadTask) runnable;
//
//                        System.out.println("Not downloaded: " + dt.getLinkedFile());
//                    }
//                }
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }

    }
}
