package josue.cs190i.cs.ucsb.edu.lobster;

/**
 * Created by Danielle on 6/9/2017.
 */

public class User {
    public String roomKey;
    public String name;


    public User() {
    }

    public User(String roomKey, String name) {
        this.roomKey = roomKey;
        this.name = name;
    }

    public String getRoomKey(){return this.roomKey;}
    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
    public void setRoomKey(String roomKey){this.roomKey = roomKey;}


}
