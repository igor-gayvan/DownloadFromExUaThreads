/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.io.File;
import java.util.List;

/**
 *
 * @author Igor Gayvan
 */
public class DataSource {

    public static final String PATH_DOWNLOAD = "./download";    

    public DataSource(List<DownloadFile> downloadFileList) {
        File pathDownload = new File(PATH_DOWNLOAD);

        downloadFileList.clear();
        long sizeDownloadFolder = 0;

        if (!pathDownload.exists()) {
            pathDownload.mkdir();
        } else {
            File f = new File(PATH_DOWNLOAD); // current directory

            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files) {

                    downloadFileList.add(new DownloadFile(file.getName(), (int) file.length()));
                    sizeDownloadFolder += file.length();
                }
            }
        }

        System.out.printf("Download folder's size : [%.1fMB]%n", Float.valueOf(sizeDownloadFolder / Utils.COUNT_BYTES_IN_MEGABYTE));
    }

}
