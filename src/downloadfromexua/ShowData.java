/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.util.List;

/**
 *
 * @author Igor Gayvan
 */
public class ShowData {

    public static void ShowListAlreadyDownloadFiles(List<DownloadFile> downloadFileList) {
        System.out.println("Already downloaded file names and sizes");
        for (DownloadFile downloadFile : downloadFileList) {
            // TODO: Print already downloaded file names and sizes. "music.mp3 (12345 bytes); \n music.wav (41212 bytes)"
            System.out.printf("%s (%d bytes);%n", downloadFile.getFileName(), downloadFile.getFileSize());

        }
    }

}
