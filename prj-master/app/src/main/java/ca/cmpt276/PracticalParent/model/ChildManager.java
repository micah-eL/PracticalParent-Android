package ca.cmpt276.PracticalParent.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Store a collection of child
 */
public class ChildManager implements Iterable<Child>{

    public List<Child> childList = new ArrayList<>();
    public List<Integer> indexList=new ArrayList<>();
    private static ChildManager instance;
    public static ChildManager getInstance(){
        if (instance == null){
            instance = new ChildManager();
        }
        return instance;
    }

    private ChildManager(){
        // Nothing. For singleton
    }

    public void add(Child child){
        indexList.add(new Integer(childList.size()));
        childList.add(child);
    }

    public void remove(Child child){
        childList.remove(child);
    }

    public void removeIndex(int i){
        indexList.remove(new Integer(i));
        for(int j=0;j<indexList.size();j++){
            if(indexList.get(j)>new Integer(i)){
                indexList.set(j,indexList.get(j)-1);
            }
        }
    }

    public int getCurrentIndex(int i){
        return indexList.get(i).intValue();
    }

    public Child getChildByName(String childName){
        Child childObject = null;
        for(int index = 0; index < childList.size(); index++){
            if(childName.equals(childList.get(index).getName())){
                //in java == does not work for strings, you have to use .equals
                childObject = childList.get(index);
            }
        }
        return childObject;
    }

    public Child get(int i){
        return childList.get(i);
    }

    public String getInfo(int i) {
        Child temp = childList.get(i);
        String strInfo = temp.getName();
        return strInfo;
    }

    public void edit(int i, String newName){
        childList.get(i).setName(newName);
    }

    @Override
    public Iterator<Child> iterator(){
        return childList.iterator();
    }

    public int getNumChildren(){
        return childList.size();
    }

    // should have a function to search for a child name and it returns child object
    // it's like get function
    // do a linear search

}
