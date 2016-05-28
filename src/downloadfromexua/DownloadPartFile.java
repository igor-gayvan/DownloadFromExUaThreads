/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor Gayvan
 */
public class DownloadPartFile implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024 * 1024;

    private BufferedInputStream fileIS;
    private int startingByte;
    private int endingByte;
    private long filePartSize;
    private int numPartFile;
    private String fileName;
    private URLConnection conn;
    BufferedOutputStream outputStream;
    public static String targetFileName;

    private RandomAccessFile sourceFile;

    public DownloadPartFile(URLConnection conn, int startingByte, long filePartSize, int numPartFile, String fileName, /*BufferedOutputStream outputStream,*/ String targetFileName) {
        this.startingByte = startingByte;
        this.filePartSize = filePartSize;
        this.numPartFile = numPartFile;
        this.fileName = fileName;
        this.conn = conn;
//        this.outputStream = outputStream;
        this.targetFileName = targetFileName;
    }

    public int getNumberPartFile() {
        return numPartFile;
    }

    public void setNumberPartFile(int numPartFile) {
        this.numPartFile = numPartFile;
    }

    public BufferedInputStream getFileIS() {
        return fileIS;
    }

    public void setFileIS(BufferedInputStream fileIS) {
        this.fileIS = fileIS;
    }

    public int getStartingByte() {
        return startingByte;
    }

    public void setStartingByte(int startingByte) {
        this.startingByte = startingByte;
    }

    public int getEndingByte() {
        return endingByte;
    }

    public void setEndingByte(int endingByte) {
        this.endingByte = endingByte;
    }

    public RandomAccessFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(RandomAccessFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public void run() {
        try {
            getFilePart();
        } catch (IOException ex) {
            Logger.getLogger(DownloadPartFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getFilePart() throws FileNotFoundException, IOException {
//        sourceFile = new RandomAccessFile(fileURL.getPath(), "r");

        BufferedInputStream fileIS = new BufferedInputStream(conn.getInputStream());

        RandomAccessFile targetFile = new RandomAccessFile(this.targetFileName, "rw");

        int copied = 0;
        fileIS.skip(this.startingByte);
        targetFile.seek(startingByte);

        while (true) {
            byte buffer[];
            if (this.filePartSize - copied > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            } else {
                buffer = new byte[(int) (this.filePartSize - copied)];
            }
//            int read = sourceFile.read(buffer, 0, buffer.length);
            int read = fileIS.read(buffer, 0, buffer.length);

            if (read == -1 || copied == this.filePartSize) {
                break;
            }
            targetFile.write(buffer, 0, read);
//            outputStream.write(buffer, (int) startingByte, (int) read);
            copied += read;
        }
        targetFile.close();
        fileIS.close();
        System.out.printf("Download: %s Part: %d%n", this.fileName, this.numPartFile);
    }

}
