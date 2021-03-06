package josue.cs190i.cs.ucsb.edu.lobster;

import android.graphics.Bitmap;

/**
 * Created by Danielle on 6/3/2017.
 */

public class Note {

    private String person_name;
    private String content;
    private String time;
    private String category;
    private String noteKey;
    private String pictureUrl;
    private String userPhotoUrl;

    public Note(String name, String content, String time, String pictureUrl, String category){
        this.person_name = name;
        this.content = content;
        this.time = time;
        this.pictureUrl = pictureUrl;
        this.category = category;
    }

    public Note(){}

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public void setNoteKey(String noteKey) {
        this.noteKey = noteKey;
    }

    public String getNoteKey() {
        return noteKey;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getPerson_name() {
        return person_name;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }


}