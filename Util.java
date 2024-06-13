
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

import java.awt.List;
import java.util.ArrayList;

public class Util {

    DefaultTableModel model;

    public Util(){
        
    }
    
    public void enter_process(JFrame frame){
        JDialog dialog = new JDialog(frame, "No Process Error", true);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(frame);
        JLabel label = new JLabel("Enter process first!");
        label.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(label);
        dialog.setVisible(true);
    }

    public ArrayList<Process> createProcesses(DefaultTableModel model) {
        this.model = model;
        ArrayList<Process> processes = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            System.out.println("Row count: " + model.getRowCount());
            int id = Integer.parseInt((String) model.getValueAt(i, 0)); // First column is for ID
            int arrivalTime = Integer.parseInt((String) model.getValueAt(i, 1)); // Second column is for arrival time
            int burstTime = Integer.parseInt((String) model.getValueAt(i, 2)); // Third column is for burst time
            int priority = Integer.parseInt((String) model.getValueAt(i, 3)); // Fourth column is for priority

            Process newProcess = new Process();
            newProcess.setId(id);
            newProcess.setBurstTime(burstTime);
            newProcess.setArrivalTime(arrivalTime);
            newProcess.setPriority(priority);
            newProcess.setRemainingTime(burstTime);
            processes.add(newProcess);
        }
        return processes;
    }


    public JComboBox createBox(){
        final String algos[] = {"First Come First Serve", "Shortest Job First", "Shortest Remaining Time First", "Round-Robin", "Priority Scheduler", ""};

        JComboBox algoBox = new JComboBox(Scheduler.values()); 
        algoBox.setSelectedIndex(0);
        algoBox.setEditable(false);
        return algoBox;
    }
}
