import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class MLFQ {
    private List<Process> processes;
    private List<Queues> queues;
    Process currentProcess;
    int currentTime = 0;
    DefaultTableModel model;

    JPanel boxPanel;
    private HashMap<Integer, Color> processColors;

    public MLFQ(List<Process> processes, List<Queues> queues, JPanel boxPanel, DefaultTableModel model) {
        this.boxPanel = boxPanel;
        this.processes = processes;
        this.queues = queues;
        this.processColors = new HashMap<>();
        this.model=model;

        for(Process process: processes){
            System.out.println("Process ID: " + process.getId() + " Arrival Time: " + process.getArrivalTime() + " Burst Time: " + process.getBurstTime() + " Priority: " + process.getPriority());
        }

     //   for (Process process : processes) {
     //       queues.get(0).addProcess(process);
     //   }

     /*Uncomment this if maam bea does not want a hardcoded value for alloted time */

     /* 
        int maxBurstTime = Integer.MIN_VALUE;
        for (Process process : processes) {
            if (process.getBurstTime() > maxBurstTime) {
                maxBurstTime = process.getBurstTime();
            }
        }

        int alloted = queues.size() > 1 ? (int) Math.pow(2, 0+2) : maxBurstTime+1;
        queues.get(0).setAllotedTime(alloted); 
    */
    
        schedule();
    }

    public void schedule() {

        new Thread(() -> {
        currentTime = 0;
        System.out.println("Starting MLFQ Scheduling...");

        if (queues.get(0).processes.isEmpty() && queues.get(0).getScheduler() != Scheduler.SJF) {
            // Wait until a process arrives
            while (queues.get(0).processes.isEmpty()) {
                
                // Check for newly arrived processes and add them to the queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
                        queues.get(0).addProcess(process);
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                  }
                }
                currentTime++;

                // Add a 1-second delay to simulate real-time waiting
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
            }
            currentTime=0;
        }else{
            for (Process process : processes) {
                queues.get(0).addProcess(process);
            }
        }
        
        while (!allQueuesEmpty()) {
            for (Queues queue : queues) {
                System.out.println("Checking Queue with Priority: " + queue.getPriority());
                
                Queue<Process> currentQueueProcesses = new LinkedList<>(queue.processes);

                switch (queue.getScheduler()) {
                    case FCFS:
                        System.out.println("Scheduler should be FSCS, which is actually: " +queue.getScheduler() + " ");
                        
                        
                        FCFS(queue, currentQueueProcesses);

                        break;
                    case SRTF:
                        System.out.println("Scheduler should be SRTF, which is actually: " +queue.getScheduler() + " ");
                        SRTF(queue, currentQueueProcesses);
                        break;
                    case SJF:
                        System.out.println("Scheduler should be SJF, which is actually: " +queue.getScheduler() + " ");
                        SJF(queue, currentQueueProcesses);
                        break;
                    case PPRIORITY:
                        System.out.println("Scheduler should be PPRIORITY,, which is actually: " +queue.getScheduler() + " ");
                        PPRIORITY(queue, currentQueueProcesses);
                        break;
                    case NPPRIORITY:
                        System.out.println("Scheduler should be NPPRIORITY,, which is actually: " +queue.getScheduler() + " ");
                        NPPRIORITY(queue, currentQueueProcesses);
                        break;
                    case ROUND_ROBIN:
                        System.out.println("Scheduler should be ROUND_ROBIN, which is actually: " +queue.getScheduler() + " ");
                        ROUND_ROBIN(queue, currentQueueProcesses);
                        break;
                    default:
                        break;
                }
                
            }
        }

        // Call printSchedule on the EDT after scheduling is done
        SwingUtilities.invokeLater(() -> printSchedule());
                            }).start();
        
    }

    public void drawBox(int id) {

        Color color = processColors.computeIfAbsent(id, k -> {
            Random random = new Random();
            return new Color(180 + new Random().nextInt(76), 180 + new Random().nextInt(76), 180 + new Random().nextInt(76));
        });

        JPanel box = new JPanel();
        JLabel label = new JLabel("" + (id));
        box.setLayout(new BorderLayout());
        box.add(label, BorderLayout.CENTER);
        box.setPreferredSize(new Dimension(12, 30));
        box.setBorder(BorderFactory.createLineBorder(color));
        box.setBackground(color);
        boxPanel.add(box); // Add the box to the panel

        boxPanel.revalidate();
        boxPanel.repaint();

        
    }

    private boolean allQueuesEmpty() {
        for (Queues queue : queues) {
            if (!queue.processes.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void printSchedule() {
        System.out.println("\nFinal Schedule:");
        System.out.println("ID\tArrival\tBurst\tCompletion\tTurnAround\tWaiting");
        int i=0;
        for (Process process : processes) {
            model.setValueAt(process.getCompletionTime(), i, 4);
            model.setValueAt(process.getWaitingTime(), i, 5);
            model.setValueAt(process.getTurnAroundTime(), i, 6);
            System.out.println(process.getId() + "\t" + process.getArrivalTime() + "\t" + process.getBurstTime() + "\t" + process.getCompletionTime() + "\t\t" + process.getTurnAroundTime() + "\t\t" + process.getWaitingTime());
            i++;
        }
        boxPanel.revalidate();
        boxPanel.repaint();
    }

    public void FCFS(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            currentProcess = currentQueueProcesses.poll();
            queue.removeProcess(currentProcess); // Remove from the current queue
            System.out.println("Processing: " + currentProcess.getId() + " from Queue: " + queue.getPriority());

            int timeSlice = Math.min(queue.getAllotedTime(), currentProcess.getRemainingTime());
            System.out.println("Time Slice for Process " + currentProcess.getId() + ": " + timeSlice);
            
            for (int i = 0; i < timeSlice; i++) {
                currentProcess.decrementRemainingTime();
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + currentProcess.getId() + ": " + currentProcess.getRemainingTime());

                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }

                drawBox(currentProcess.getId());

                // Check if any new processes have arrived and need to be added to the first queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
                        queues.get(0).addProcess(process);
                        currentQueueProcesses.add(process);
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                    }
                }

                // Exit loop early if process is complete
                if (currentProcess.getRemainingTime() == 0) {
                    break;
                }
            }

            // If the process has finished, set its completion, turnaround, and waiting times
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnAroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                queue.removeProcess(currentProcess);
                currentQueueProcesses.remove(currentProcess);
                System.out.println("Process " + currentProcess.getId() + " completed at time " + currentProcess.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                currentQueueProcesses.remove(currentProcess); // Remove from the current queue
                int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size()) {
                    queues.get(nextQueueIndex + 1).addProcess(currentProcess);
                    System.out.println("Process " + currentProcess.getId() + " moved to Queue " + (nextQueueIndex + 1));
                } else {
                    queue.addProcess(currentProcess);
                    System.out.println("Process " + currentProcess.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
            }
        }
    
    }

    public void SRTF(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            // Find the process with the shortest remaining time
            Process shortestProcess = null;
            for (Process process : currentQueueProcesses) {
                if (shortestProcess == null || process.getRemainingTime() < shortestProcess.getRemainingTime()) {
                    shortestProcess = process;
                    System.out.println("Shortest Process: " + shortestProcess.getId());
                }
            }
    
            if (shortestProcess == null) {
                break;
            }
    
            currentQueueProcesses.remove(shortestProcess);
            queue.removeProcess(shortestProcess); // Remove from the current queue
            System.out.println("Processing: " + shortestProcess.getId() + " from Queue: " + queue.getPriority());
            
            int timeslice=0;
            if(shortestProcess.getExecutionCount()>0) {
                timeslice = queue.getAllotedTime();
            }else{
                timeslice = Math.min(queue.getAllotedTime(), shortestProcess.getRemainingTime());
            }
            System.out.println("Time Slice for Process " + shortestProcess.getId() + ": " + timeslice);
    
            for (int i = 0; shortestProcess.getExecutionCount() < timeslice; i++) {
                shortestProcess.decrementRemainingTime();
                shortestProcess.incrementExecutionCount();
                System.out.println("Execution count before preemption: " + shortestProcess.getExecutionCount());
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + shortestProcess.getId() + ": " + shortestProcess.getRemainingTime());
    
                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
    
                drawBox(shortestProcess.getId());
    
                // Check if any new processes have arrived and need to be added to the first queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
                        queues.get(0).addProcess(process);
                        currentQueueProcesses.add(process);
                        if(shortestProcess.getRemainingTime() > process.getRemainingTime()){
                            if(shortestProcess.getExecutionCount()<timeslice){
                                currentQueueProcesses.add(shortestProcess);
                                queue.addProcess(shortestProcess);
                            }
                            shortestProcess = process;
                        }
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                    }
                }
    
                // Exit loop early if process is complete
                if (shortestProcess.getRemainingTime() == 0) {
                    break;
                }
            }
    
            // If the process has finished, set its completion, turnaround, and waiting times
            if (shortestProcess.getRemainingTime() == 0) {
                shortestProcess.setCompletionTime(currentTime);
                shortestProcess.setTurnAroundTime(shortestProcess.getCompletionTime() - shortestProcess.getArrivalTime());
                shortestProcess.setWaitingTime(shortestProcess.getTurnAroundTime() - shortestProcess.getBurstTime());
                queue.removeProcess(shortestProcess);
                currentQueueProcesses.remove(shortestProcess);
                System.out.println("Process " + shortestProcess.getId() + " completed at time " + shortestProcess.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                    queue.removeProcess(shortestProcess);
                    currentQueueProcesses.remove(shortestProcess);
                    int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size()) {
                    shortestProcess.resetExecutionCount();
                    queues.get(nextQueueIndex + 1).addProcess(shortestProcess);
                    System.out.println("Process " + shortestProcess.getId() + " moved to Queue " + (nextQueueIndex + 1));
                } else {
                    queue.addProcess(shortestProcess);
                    shortestProcess.resetExecutionCount();
                    System.out.println("Process " + shortestProcess.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
                
            }
        }
    }

    public void SJF(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            // Find the process with the shortest remaining time
            Process shortestJob = null;
            for (Process process : currentQueueProcesses) {
                if (shortestJob == null || process.getRemainingTime() < shortestJob.getRemainingTime()) {
                    shortestJob = process;
                }
            }
    
            // Poll the selected process (shortestJob) from the queue
            if (shortestJob != null) {
                currentQueueProcesses.remove(shortestJob);
               // queue.removeProcess(shortestJob); // Remove from the queue
            }
            //queue.removeProcess(shortestJob);
            System.out.println("Processing: " + shortestJob.getId() + " from Queue: " + queue.getPriority());
    
            int timeSlice = Math.min(queue.getAllotedTime(), shortestJob.getRemainingTime());
            System.out.println("Time Slice for Process " + shortestJob.getId() + ": " + timeSlice);
    
            for (int i = 0; i < timeSlice; i++) {
                shortestJob.decrementRemainingTime();
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + shortestJob.getId() + ": " + shortestJob.getRemainingTime());
    
                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
    
                drawBox(shortestJob.getId());
    
                // Check if any new processes have arrived and need to be added to the first queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
//                        queues.get(0).addProcess(process);
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                    }
                }
    
                // Exit loop early if process is complete
                if (shortestJob.getRemainingTime() == 0) {
                    break;
                }
            }
            // If the process has finished, set its completion, turnaround, and waiting times
            if (shortestJob.getRemainingTime() == 0) {
                shortestJob.setCompletionTime(currentTime);
                shortestJob.setTurnAroundTime(shortestJob.getCompletionTime() - shortestJob.getArrivalTime());
                shortestJob.setWaitingTime(shortestJob.getTurnAroundTime() - shortestJob.getBurstTime());
                queue.removeProcess(shortestJob);
                System.out.println("Process " + shortestJob.getId() + " completed at time " + shortestJob.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size()) {
                    queues.get(nextQueueIndex + 1).addProcess(shortestJob);
                    System.out.println("Process " + shortestJob.getId() + " moved to Queue " + (nextQueueIndex + 1));
                    queue.removeProcess(shortestJob);
                    System.out.println("Process " + shortestJob.getId() + " removed from Queue " + (nextQueueIndex));
                } else {
                    queue.removeProcess(shortestJob);
                    System.out.println("Process " + shortestJob.getId() + " removed from Queue " + (nextQueueIndex));
                    queue.addProcess(shortestJob);
                    System.out.println("Process " + shortestJob.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
            }
        
        }
    }
    
    public void PPRIORITY(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            // Find the process with the shortest remaining time
            Process priorityProcess = null;
            for (Process process : currentQueueProcesses) {
                if (priorityProcess == null || process.getPriority() < priorityProcess.getPriority()) {
                    priorityProcess = process;
                    System.out.println("Shortest Process: " + priorityProcess.getId());
                }
            }
    
            if (priorityProcess == null) {
                break;
            }
    
            currentQueueProcesses.remove(priorityProcess);
            queue.removeProcess(priorityProcess); // Remove from the current queue
            System.out.println("Processing: " + priorityProcess.getId() + " from Queue: " + queue.getPriority());
            
            int timeslice=0;
            if(priorityProcess.getExecutionCount()>0) {
                timeslice = queue.getAllotedTime();
            }else{
                timeslice = Math.min(queue.getAllotedTime(), priorityProcess.getRemainingTime());
            }
            System.out.println("Time Slice for Process " + priorityProcess.getId() + ": " + timeslice);
    
            for (int i = 0; priorityProcess.getExecutionCount() < timeslice; i++) {
                priorityProcess.decrementRemainingTime();
                priorityProcess.incrementExecutionCount();
                System.out.println("Execution count before preemption: " + priorityProcess.getExecutionCount());
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + priorityProcess.getId() + ": " + priorityProcess.getRemainingTime());
    
                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
    
                drawBox(priorityProcess.getId());
    
                // Check if any new processes have arrived and need to be added to the first queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
                        queues.get(0).addProcess(process);
                        currentQueueProcesses.add(process);
                        if(priorityProcess.getPriority() > process.getPriority()){
                            if(priorityProcess.getExecutionCount()<timeslice){
                                currentQueueProcesses.add(priorityProcess);
                                queue.addProcess(priorityProcess);
                            }
                            priorityProcess = process;
                        }
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                    }
                }
    
                // Exit loop early if process is complete
                if (priorityProcess.getRemainingTime() == 0) {
                    break;
                }
            }
    
            // If the process has finished, set its completion, turnaround, and waiting times
            if (priorityProcess.getRemainingTime() == 0) {
                priorityProcess.setCompletionTime(currentTime);
                priorityProcess.setTurnAroundTime(priorityProcess.getCompletionTime() - priorityProcess.getArrivalTime());
                priorityProcess.setWaitingTime(priorityProcess.getTurnAroundTime() - priorityProcess.getBurstTime());
                queue.removeProcess(priorityProcess);
                currentQueueProcesses.remove(priorityProcess);
                System.out.println("Process " + priorityProcess.getId() + " completed at time " + priorityProcess.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                    queue.removeProcess(priorityProcess);
                    currentQueueProcesses.remove(priorityProcess);
                    int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size()) {
                    priorityProcess.resetExecutionCount();
                    queues.get(nextQueueIndex + 1).addProcess(priorityProcess);
                    System.out.println("Process " + priorityProcess.getId() + " moved to Queue " + (nextQueueIndex + 1));
                } else {
                    priorityProcess.resetExecutionCount();
                    queue.addProcess(priorityProcess);
                    System.out.println("Process " + priorityProcess.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
                
            }
        }
    }

    public void NPPRIORITY(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            // Find the process with the shortest remaining time
            Process priorityProcess = null;
            for (Process process : currentQueueProcesses) {
                if (priorityProcess == null || process.getPriority() < priorityProcess.getPriority()) {
                    priorityProcess = process;
                    System.out.println("Shortest Process: " + priorityProcess.getId());
                }
            }
    
            if (priorityProcess == null) {
                break;
            }
    
            currentQueueProcesses.remove(priorityProcess);
            queue.removeProcess(priorityProcess); // Remove from the current queue
            System.out.println("Processing: " + priorityProcess.getId() + " from Queue: " + queue.getPriority());
            
            int timeslice=0;
            if(priorityProcess.getExecutionCount()>0) {
                timeslice = queue.getAllotedTime();
            }else{
                timeslice = Math.min(queue.getAllotedTime(), priorityProcess.getRemainingTime());
            }
            System.out.println("Time Slice for Process " + priorityProcess.getId() + ": " + timeslice);
    
            for (int i = 0; priorityProcess.getExecutionCount() < timeslice; i++) {
                priorityProcess.decrementRemainingTime();
                priorityProcess.incrementExecutionCount();
                System.out.println("Execution count before preemption: " + priorityProcess.getExecutionCount());
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + priorityProcess.getId() + ": " + priorityProcess.getRemainingTime());
    
                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
    
                drawBox(priorityProcess.getId());
    
                // Check if any new processes have arrived and need to be added to the first queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime) {
                        queues.get(0).addProcess(process);
                        currentQueueProcesses.add(process);
                        System.out.println("Process " + process.getId() + " arrived and added to Queue 1");
                    }
                }
    
                // Exit loop early if process is complete
                if (priorityProcess.getRemainingTime() == 0) {
                    break;
                }
            }
    
            // If the process has finished, set its completion, turnaround, and waiting times
            if (priorityProcess.getRemainingTime() == 0) {
                priorityProcess.setCompletionTime(currentTime);
                priorityProcess.setTurnAroundTime(priorityProcess.getCompletionTime() - priorityProcess.getArrivalTime());
                priorityProcess.setWaitingTime(priorityProcess.getTurnAroundTime() - priorityProcess.getBurstTime());
                queue.removeProcess(priorityProcess);
                currentQueueProcesses.remove(priorityProcess);
                System.out.println("Process " + priorityProcess.getId() + " completed at time " + priorityProcess.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                    queue.removeProcess(priorityProcess);
                    currentQueueProcesses.remove(priorityProcess);
                    int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size()) {
                    priorityProcess.resetExecutionCount();
                    queues.get(nextQueueIndex + 1).addProcess(priorityProcess);
                    System.out.println("Process " + priorityProcess.getId() + " moved to Queue " + (nextQueueIndex + 1));
                } else {
                    queue.addProcess(priorityProcess);
                    priorityProcess.resetExecutionCount();
                    System.out.println("Process " + priorityProcess.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
                
            }
        }
    }

    public void ROUND_ROBIN(Queues queue, Queue<Process> currentQueueProcesses) {
        while (!currentQueueProcesses.isEmpty()) {
            Process currentProcess = currentQueueProcesses.poll();
            System.out.println("Processing: " + currentProcess.getId() + " from Queue");

            int timeSlice=0;
            if(currentProcess.getExecutionCount()>0) {
                timeSlice = Math.min(queue.getTimeQuantum()+currentProcess.getExecutionCount(), queue.getAllotedTime());
            }else{
                timeSlice = Math.min(queue.getTimeQuantum(), currentProcess.getRemainingTime());
                timeSlice = Math.min(timeSlice, queue.getAllotedTime());
            }
            System.out.println("Time Slice for Process " + currentProcess.getId() + ": " + timeSlice);
    
            for (int i = 0; currentProcess.getExecutionCount() < timeSlice; i++) {
                currentProcess.decrementRemainingTime();
                currentProcess.incrementExecutionCount();
                currentTime++;
                System.out.println("Current Time: " + currentTime + " | Remaining Time for Process " + currentProcess.getId() + ": " + currentProcess.getRemainingTime());
    
                // Add a 1-second delay
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
    
                drawBox(currentProcess.getId());
    
                // Check if any new processes have arrived and need to be added to the queue
                for (Process process : processes) {
                    if (process.getArrivalTime() == currentTime && !currentQueueProcesses.contains(process)) {
                        queue.addProcess(process);
                        currentQueueProcesses.add(process);
                        System.out.println("Process " + process.getId() + " arrived and added to the Queue");
                    }
                }
    
                // Exit loop early if process is complete
                if (currentProcess.getRemainingTime() == 0) {
                    break;
                }
            }
    
            // If the process has finished, set its completion, turnaround, and waiting times
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompletionTime(currentTime);
                currentProcess.setTurnAroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                queue.removeProcess(currentProcess);
                currentQueueProcesses.remove(currentProcess);
                System.out.println("Process " + currentProcess.getId() + " completed at time " + currentProcess.getCompletionTime());
            } else {
                // If the process hasn't finished, move it to the next lower-priority queue
                    queue.removeProcess(currentProcess);
                    currentQueueProcesses.remove(currentProcess);
                    int nextQueueIndex = queue.getPriority();
                if (nextQueueIndex + 1 < queues.size() && currentProcess.getExecutionCount()>=queue.getAllotedTime()) {
                    currentProcess.resetExecutionCount();
                    queues.get(nextQueueIndex + 1).addProcess(currentProcess);
                    System.out.println("Process " + currentProcess.getId() + " moved to Queue " + (nextQueueIndex + 1));
                } else {
                    queue.addProcess(currentProcess);
                    currentQueueProcesses.add(currentProcess);
                    //currentProcess.resetExecutionCount();
                    System.out.println("Process " + currentProcess.getId() + " re-queued in the same Queue " + queue.getPriority());
                }
                
            }
        }
    }
    
}
