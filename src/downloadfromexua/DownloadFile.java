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
import static java.lang.Math.round;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Igor Gayvan
 */
public class DownloadFile {

    private String fileName;
    private Long fileSize;
    private URL fileURL;
    private int isAlreadyDownload;

    private static int isReplaceAllFile = 0;

    public DownloadFile() {
    }

    public DownloadFile(String fileName, Long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public DownloadFile(URL fileURL) {
        this.fileURL = fileURL;
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

    public Long getFileSize() {
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

    public void loadFile(DataSource ds, List<DownloadFile> downloadFileList) throws IOException {
        URLConnection conn = fileURL.openConnection();

        String mime = conn.getContentType();
        String urlFilename = new String(conn.getURL().getFile().getBytes("ISO-8859-1"), "utf-8");        
        this.fileName = URLDecoder.decode(new File(urlFilename).getName(), "utf-8");

        int isLoadFile = 1;

        if (DownloadFile.getIsReplaceAllFile() != 1) {
            // проверяем наличие файла среди уже загруженных
            for (DownloadFile dfl : downloadFileList) {
                if (fileName.equals(dfl.fileName)) {
                    System.err.printf("File %s was already downloaded%n", fileName);
                    isAlreadyDownload = 1;
                    break;
                }
            }
        }

        if (isAlreadyDownload == 1 && DownloadFile.getIsReplaceAllFile() != 1) {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                isLoadFile = -1;
                System.out.print("Replace file (yes/no/all)? ");
                String line = scanner.nextLine();

                switch (line.toLowerCase().trim()) {
                    case "yes":
                        isLoadFile = 1;
                        break;
                    case "all":
                        isLoadFile = 1;
                        DownloadFile.setIsReplaceAllFile(1);
                        break;
                    case "no":
                        isLoadFile = 0;
                        break;
                }

                if (isLoadFile != -1) {
                    break;
                }
            }
        }

        if (isLoadFile == 1) {
            this.fileSize = conn.getContentLengthLong();

            System.out.printf("Downloading: (%s) %s [%.1fMB]\n", mime, fileName, Float.valueOf(fileSize / Utils.COUNT_BYTES_IN_MEGABYTE));

//            InputStream fileIS = conn.getInputStream();
            BufferedInputStream fileIS =  new BufferedInputStream(conn.getInputStream());

            // write the inputStream to a FileOutputStream
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(ds.PATH_DOWNLOAD + "/" + fileName)));

            int read = 0;
            byte[] bytes = new byte[1024];

            double cntByteCurFile = 0;
            int prevProgress = 0;

            while ((read = fileIS.read(bytes)) != -1) {
                cntByteCurFile = cntByteCurFile + read;
                int curProgress = (int) round(cntByteCurFile / fileSize * 100);
                if (prevProgress != curProgress) {
                    String strPad = Utils.padl("", curProgress + 1, '=');

                    System.out.printf("\r[%-100s]", strPad);
                }
                outputStream.write(bytes, 0, read);
                prevProgress = curProgress;
            }

            fileIS.close();
            outputStream.close();
            System.out.println("\nDownloaded");

            downloadFileList.add(this);
        }
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

            for (URL fileDownloadURL : fileList) {
                System.out.printf("%s%n", fileDownloadURL);

                DownloadFile df = new DownloadFile(fileDownloadURL);
                df.loadFile(ds, downloadFileList);
            }
        }

    }
}
