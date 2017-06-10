package josue.cs190i.cs.ucsb.edu.lobster;

import android.graphics.Bitmap;

/**
 * Created by Danielle on 6/3/2017.
 */

public class Note {
    String person_name;
    String content;
    String time;
    String category;
    Bitmap picture;

    public Note(String name, String content, String time, Bitmap picture, String category){
        this.person_name = name;
        this.content = content;
        this.time = time;
        this.picture = picture;
        this.category = category;
    }
}