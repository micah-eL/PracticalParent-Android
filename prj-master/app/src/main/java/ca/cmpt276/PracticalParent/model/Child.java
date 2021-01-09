package ca.cmpt276.PracticalParent.model;

/**
 * Child object
 */
public class Child {
    public static final String EMPTY_PHOTO_PATH = "photo not upload yet,please using the default photo";
    private String name;
    public String photo_path;

    public Child(String name){
        this.name = name;
        this.photo_path = EMPTY_PHOTO_PATH;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPhoto_path(String input) {
        this.photo_path = input;
    }

    public String getName(){
        return name;
    }

    public String get_photo_path(){
        return photo_path;
    }

    public boolean child_no_pic(){
        return photo_path.equals("photo not upload yet,please using the default photo");
    }

    //find child by name... which return child object, that contain the photopath...
    //what
}

