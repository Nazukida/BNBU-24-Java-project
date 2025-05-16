package Main;

import Controller.Initialize;
import GUI.TicketBookingGUI;
import javax.swing.SwingUtilities;

/**
 * Ticket System GUI Launcher
 * Responsible for initializing system data and launching the graphical interface
 */
public class GUILauncher {
    /**
     * Main entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize system data
        Initialize.initialize();
        
        try {
            System.out.println("Creating GUI window...");
            // Create and display the ticket system main interface
            TicketBookingGUI graphicalUserInterface = new TicketBookingGUI();
            graphicalUserInterface.pack();
            graphicalUserInterface.setSize(1000, 600);
            graphicalUserInterface.setVisible(true);
            System.out.println("GUI window is now visible");
        } catch (Exception exception) {
            System.err.println("Error starting GUI:");
            exception.printStackTrace();
        }
    }
} 