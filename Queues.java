import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Queues {
    private int allotedTime;
    private Scheduler scheduler;
    private int priority;

    private int timeQuantum;

    Queue<Process> processes = new LinkedList<>();

    public void addProcess(Process process){
        processes.add(process);
    }

    public void removeProcess(Process process){
        processes.remove(process);
    }

    public int getAllotedTime() {
        return allotedTime;
    }

    public void setAllotedTime(int allotedTime) {
        this.allotedTime = allotedTime;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public Queue<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(Queue<Process> processes) {
        this.processes = processes;
    }
}
