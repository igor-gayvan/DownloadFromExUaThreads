/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloadfromexua;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Igor Gayvan
 */
public class Console {

    private Scanner scanner;

    private List<ActionListener> actionListeners;

    private String modeWorking = "GET_URL_4_DOWNLOAD";

    private String inputText;

    public Console(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);

        this.actionListeners = new ArrayList<>();
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public String getInputText() {
        return inputText.trim();
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getModeWorking() {
        return modeWorking;
    }

    /**
     *
     * @param modeWorking CONFIRM_REPLACE_FILE - подтверждение перезаписи файла
     *
     */
    public void setModeWorking(String modeWorking) {
        this.modeWorking = modeWorking;
    }

    public void working() {
        while (true) {
            switch (modeWorking) {
                case "GET_URL_4_DOWNLOAD": {
                    System.out.println("Enter URL playlist for start download");
                    System.out.println("Press Enter for exit");
                }
            }

            inputText = scanner.nextLine().trim();

            switch (modeWorking) {
                case "GET_URL_4_DOWNLOAD":
                    switch (inputText) {
                        case "": {
                            for (ActionListener actionListener : actionListeners) {
                                actionListener.exitAction();
                            }
                            break;
                        }
                        default: {
                            for (ActionListener actionListener : actionListeners) {
                                actionListener.getUrl4DownloadAction();
                            }
                            break;

                        }
                    }
                case "CONFIRM_REPLACE_FILE":
                    switch (inputText.toLowerCase().trim()) {
                        case "yes":
                            for (ActionListener actionListener : actionListeners) {
                                actionListener.confirmReplaceFiletAction();
                            }
                            break;

                    }

            }
        }
    }
}
