import Gui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // SwingUtilities.invokeLater ensures all GUI creation happens on Swing's dedicated path
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
