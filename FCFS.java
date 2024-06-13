
import java.util.Arrays;
import java.util.Comparator;

public class FCFS {
    private Process[] processes;

    public FCFS(Process[] processes) {
        this.processes = processes;
    }

    public void schedule() {
        // Sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        for (Process process : processes) {
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }

            process.setCompletionTime(currentTime + process.getBurstTime());
            process.setTurnAroundTime(process.getCompletionTime() - process.getArrivalTime());
            process.setWaitingTime(process.getTurnAroundTime() - process.getBurstTime());

            currentTime += process.getBurstTime();
        }
    }

    public void printSchedule() {
        System.out.println("ID\tArrival\tBurst\tCompletion\tTurnAround\tWaiting");
        for (Process process : processes) {
            System.out.println(process.getId() + "\t" + process.getArrivalTime() + "\t" + process.getBurstTime() + "\t" + process.getCompletionTime() + "\t\t" + process.getTurnAroundTime() + "\t\t" + process.getWaitingTime());
        }
    }
}
