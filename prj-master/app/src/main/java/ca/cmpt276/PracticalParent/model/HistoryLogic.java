package ca.cmpt276.PracticalParent.model;

/**
* Represents an object that contains name, date and time, result of flip, and child is win or lose
**/
public class HistoryLogic {
    private String child;
    private String dateAndTime;
    private String resultOfFlip;
    private Boolean childWon;
    private String photoPath; //remove it

    public HistoryLogic(String child, String dateAndTime, String resultOfFlip, Boolean childWon, String photoPath) {
        this.child = child;
        this.dateAndTime = dateAndTime;
        this.resultOfFlip = resultOfFlip;
        this.childWon = childWon;
        this.photoPath = photoPath;
        //use child's name to find a child in child's list
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getResultOfFlip() {
        return resultOfFlip;
    }

    public void setResultOfFlip(String resultOfFlip) {
        this.resultOfFlip = resultOfFlip;
    }

    public Boolean getChildWon() {
        return childWon;
    }

    public void setChildWon(Boolean childWon) {
         this.childWon = childWon;
    }

    public String getPhotoPath() {
        Child childObject = ChildManager.getInstance().getChildByName(this.child);
        if(childObject != null){
            this.photoPath = childObject.get_photo_path();
            return this.photoPath;
        }
        else {
            if (photoPath==null){
                this.photoPath=Child.EMPTY_PHOTO_PATH;
            }
            return photoPath;
        }
    } // you are searching the name in child manager , it return the child object
    // and you can get the photo from the child class

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public boolean child_no_pic(){
        this.photoPath = getPhotoPath();
        return photoPath.equals("photo not upload yet,please using the default photo");
    }

    public String getDescription() {
        return (dateAndTime + " " + child + " " + resultOfFlip);
    }
}
