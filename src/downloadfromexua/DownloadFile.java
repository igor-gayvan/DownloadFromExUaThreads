/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.Math.floor;
import static java.lang.Math.round;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Gayvan
 */
public class DownloadFile implements Runnable {

    private static final int COUNT_DOWNLOAD_PART = 1;

    private String fileName;
    private long fileSize;
    private URL fileURL;
    private int isAlreadyDownload;

    private static int isReplaceAllFile = 1;

    private DataSource ds;
    private List<DownloadFile> downloadFileList;

    public DownloadFile() {
    }

    public DownloadFile(String fileName, int fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public DownloadFile(URL fileURL) {
        this.fileURL = fileURL;
    }

    public DataSource getDs() {
        return ds;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    public List<DownloadFile> getDownloadFileList() {
        return downloadFileList;
    }

    public void setDownloadFileList(List<DownloadFile> downloadFileList) {
        this.downloadFileList = downloadFileList;
    }

    public int getIsAlreadyDownload() {
        return isAlreadyDownload;
    }

    public void setIsAlreadyDownload(int isAlreadyDownload) {
        this.isAlreadyDownload = isAlreadyDownload;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public URL getFileURL() {
        return fileURL;
    }

    public void setFileURL(URL fileURL) {
        this.fileURL = fileURL;
    }

    public static int getIsReplaceAllFile() {
        return isReplaceAllFile;
    }

    public static void setIsReplaceAllFile(int isReplaceAllFile) {
        DownloadFile.isReplaceAllFile = isReplaceAllFile;
    }

    public void loadFile() throws IOException, InterruptedException {
        URLConnection conn = fileURL.openConnection();

        String mime = conn.getContentType();
        String urlFilename = new String(conn.getURL().getFile().getBytes("ISO-8859-1"), "utf-8");
        this.fileName = URLDecoder.decode(new File(urlFilename).getName(), "utf-8");

        this.fileSize = conn.getContentLengthLong();

        System.out.printf("Downloading: (%s) %s [%.1fMB]\n", mime, fileName, Float.valueOf(fileSize / Utils.COUNT_BYTES_IN_MEGABYTE));

        // write the inputStream to a FileOutputStream
//        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(DataSource.PATH_DOWNLOAD + "/" + fileName)));

        ExecutorService threadPoolPartFile = Executors.newFixedThreadPool(COUNT_DOWNLOAD_PART);

        int lenDownloadPart = (int) floor(fileSize / COUNT_DOWNLOAD_PART);

        for (int i = 0; i < COUNT_DOWNLOAD_PART; i++) {
            System.out.printf("File: %s Part: %d%n", fileName, i + 1);

            int startPos = lenDownloadPart * i;
            long filePartSize = ((i + 1) != COUNT_DOWNLOAD_PART ? lenDownloadPart : lenDownloadPart + fileSize - (lenDownloadPart * COUNT_DOWNLOAD_PART));

            DownloadPartFile dpf = new DownloadPartFile(conn, startPos, filePartSize, i + 1, fileName, /*outputStream,*/ DataSource.PATH_DOWNLOAD + "/" + fileName);
            threadPoolPartFile.execute(dpf);
        }

        // Soft stoping...
        threadPoolPartFile.shutdown();

        if (!threadPoolPartFile.isTerminated()) {
            // Wait 1 second for tasks.
            if (!threadPoolPartFile.awaitTermination(1, TimeUnit.SECONDS)) {
                // Hard stop...
                List<Runnable> shutdownNow = threadPoolPartFile.shutdownNow();

                for (Runnable runnable : shutdownNow) {
                    DownloadPartFile dpf = (DownloadPartFile) runnable;

                    System.out.println("Not downloaded: " + dpf.getNumberPartFile());
                }
            }
        }

//        outputStream.close();
        System.out.printf("\nDownloaded %s%n", fileName);

        downloadFileList.add(this);

    }

    @Override
    public void run() {
        try {
            loadFile();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DownloadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
