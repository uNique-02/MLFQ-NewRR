
//PCB
public class Process {
    private int id;
    private int arrivalTime;
    private int remainingTime;
    private int burstTime;
    private int completionTime;
    private int turnAroundTime;
    private int waitingTime;
    private int executionCount = 0; //Execution count of process in a queue before preemption

    public int getExecutionCount() {
        return executionCount;
    }

    public void incrementExecutionCount() {
        executionCount++;
    }

    public void resetExecutionCount() {
        executionCount = 0;
    }

    public void decrementExecutionCount() {
        executionCount--;
    }

    Process(){
        
    }
    
    public int getTurnAroundTime() {
        return turnAroundTime;
    }

    public void setTurnAroundTime(int turnAroundTime) {
        this.turnAroundTime = turnAroundTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    private int priority;

    public Process(int id, int arrivalTime, int remainingTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.remainingTime = remainingTime;
        this.priority = priority;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getPriority() {
        return priority;
    }

    // Setters
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Method to decrement the remaining time
    public void decrementRemainingTime() {
        if (remainingTime > 0) {
            remainingTime--;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }
}
