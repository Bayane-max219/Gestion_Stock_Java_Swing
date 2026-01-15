package com.miguel.gestionstock;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.miguel.gestionstock.ui.LoginFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
            }
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
