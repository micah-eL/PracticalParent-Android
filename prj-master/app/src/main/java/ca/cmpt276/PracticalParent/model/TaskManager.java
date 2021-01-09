package ca.cmpt276.PracticalParent.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Stores collection of tasks
 */
public class TaskManager implements Iterable<Task>{
    public List<Task> taskList = new ArrayList<>();

    private static TaskManager instance;
    public static TaskManager getInstance(){
        if (instance == null){
            instance = new TaskManager();
        }
        return instance;
    }

    private TaskManager(){
        // Nothing. For singleton
    }

    public void add(Task task){
        taskList.add(task);
    }

    public void remove(Task task){
        taskList.remove(task);
    }

    public Task get(int index){
        return taskList.get(index);
    }

    public String getInfo(int i) {
        Task temp = taskList.get(i);
        String strInfo = temp.getTaskName();
        return strInfo;
    }

    public void edit(int index, String newName){
        taskList.get(index).setTaskName(newName);
    }

    @Override
    public Iterator<Task> iterator() {
        return taskList.iterator();
    }

    public int getNumTask(){
        return taskList.size();
    }
}
