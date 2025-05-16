package GUI;

import javax.swing.*;
import java.awt.*;

public class TestGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Creating test window...");
                JFrame frame = new JFrame("Test Window");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                
                JLabel label = new JLabel("If you can see this, the GUI is working correctly!");
                label.setHorizontalAlignment(JLabel.CENTER);
                frame.add(label);
                
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                System.out.println("Test window should be visible now");
            } catch (Exception e) {
                System.err.println("Error creating test window:");
                e.printStackTrace();
            }
        });
    }
} 