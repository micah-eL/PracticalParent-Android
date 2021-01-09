package ca.cmpt276.PracticalParent.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
* Stores collection of historyLogic
**/
public class HistoryManager implements Iterable<HistoryLogic>{
    public List<HistoryLogic> historyInfo = new ArrayList<>();

    private static HistoryManager instance;
    public static HistoryManager getInstance(){
        if(instance == null){
            instance = new HistoryManager();
        }
        return instance;
    }

    private HistoryManager(){
        //Nothing for singleton
    }

    public void add(HistoryLogic historylogic){
        historyInfo.add(historylogic);
    }

    public HistoryLogic get(int i) {
        return historyInfo.get(i);
    }

    public void remove(HistoryLogic item){
        historyInfo.remove(item);
    }

    @Override
    public Iterator<HistoryLogic> iterator(){
        return historyInfo.iterator();
    }


    public int getNumHistoryInfo(){
        return historyInfo.size();
    }
}
