package Main;

import Controller.Initialize;
import GUI.TicketBookingGUI;
import javax.swing.SwingUtilities;
public class GUILauncher {
    public static void main(String[] args) {
        // Initialize data
        Initialize.initialize();
        
        try {
            System.out.println("creating GUI window...");
            TicketBookingGUI gui = new TicketBookingGUI();
            gui.pack();
            gui.setSize(1000, 600);
            gui.setVisible(true);
            System.out.println("GUI window should now be visible");
        } catch (Exception e) {
            System.err.println("Error launching GUI:");
            e.printStackTrace();
        }
    }
} 