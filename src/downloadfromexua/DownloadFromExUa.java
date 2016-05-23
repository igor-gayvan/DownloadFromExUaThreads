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
                    DownloadFile.getFiles(new URL(console.getInputText()));
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
}
