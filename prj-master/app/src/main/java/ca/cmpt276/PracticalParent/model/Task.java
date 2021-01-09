package ca.cmpt276.PracticalParent.model;
/*
** Represents a Task object that has the name of the task and concatenated name of task as variables
 */
public class Task {
    private String taskName;
    private String currentTaskKey;

    public Task(String taskName){
        this.taskName = taskName;
        this.currentTaskKey = null;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }

    public String getTaskName(){
        return taskName;
    }

    public String getCurrentTaskKey() {
        return currentTaskKey;
    }

    public void setTaskNameForPref(String taskName){
        this.currentTaskKey = taskName.replaceAll("\\s", "");
    }

}
