package www.directi.com.junk;

import java.io.IOException;
import java.io.RandomAccessFile;

/*
 * copy file(filePath) to (targetFileName) in chunks. It divide the file in parts of chuckOfFile and copy each part concurrently
 * $author = Paras Malik(masterofmasters22@gmail.com)
 * $Date = 20 June, 2011
 */
public class CopyFile implements Runnable {

    public static String filePath = "source.mkv";  //file to copy
    public static String targetFileName = "target.mkv";
    private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 5;
    private int startingByte;
    private int endingByte;
    private static int chunkOfFile = 1024 * 1024 * 70;  //size of one chunk of file

    public CopyFile(int startingByte, int endingByte) {
        this.startingByte = startingByte;
        this.endingByte = endingByte;
    }

    public static void main(String s[]) throws IOException {
        int copyStartingByte = 0;
        int copyEndingByte = chunkOfFile;
        int fileSize = (int) getFileSize();
        while (fileSize > copyStartingByte) {
            new Thread(new CopyFile(copyStartingByte, copyEndingByte)).start();
            copyStartingByte = copyEndingByte;
            if (copyEndingByte + chunkOfFile <= fileSize) {
                copyEndingByte = copyEndingByte + chunkOfFile;
            } else {
                copyEndingByte = fileSize;
            }
        }
    }

    static long getFileSize() throws IOException {
        RandomAccessFile f = new RandomAccessFile(filePath, "r");
        long contentLength = f.length();
        return contentLength;
    }

    /* copy one part of the file starting from startingByte till endingByte*/
    public void copyPartOfFile(int startingByte, int endingByte) throws IOException {
        int copied = 0;
        int contentLength = endingByte - startingByte;
        RandomAccessFile sourceFile = new RandomAccessFile(filePath, "rw");
        RandomAccessFile targetFile = new RandomAccessFile(this.targetFileName, "rw");
        targetFile.seek(startingByte);
        sourceFile.seek(startingByte);
        while (true) {
            byte buffer[];
            if (contentLength - copied > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            } else {
                buffer = new byte[contentLength - copied];
            }
            int read = sourceFile.read(buffer, 0, buffer.length);

            if (read == -1 || copied == contentLength) {
                break;
            }
            targetFile.write(buffer, 0, read);
            copied += read;
        }
        sourceFile.close();
        targetFile.close();
    }

    @Override
    public void run() {
        try {
            copyPartOfFile(startingByte, endingByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
