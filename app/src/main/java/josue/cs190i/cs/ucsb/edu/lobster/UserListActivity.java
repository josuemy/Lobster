package josue.cs190i.cs.ucsb.edu.lobster;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private static final String USER_LIST = "users";
    private static final String MESSAGES = "messages";
    private static final String ROOMS = "rooms";

    public static String roomName;

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        Context context;

        public UserViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.user_name);
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: I just cklicasdf");
                    if(StartingActivity.mUsername.compareTo(username.getText().toString()) > 0){
                        roomName = StartingActivity.mUsername + "_" + username.getText();
                    } else {
                        roomName =  username.getText() + "_" + StartingActivity.mUsername;
                    }

                    FirebaseDatabase.getInstance().getReference()
                            .child(USER_LIST)
                            .child(StartingActivity.firebaseUserUid)
                            .child("roomKey")
                            .setValue(roomName);

                    // Begins notifications for the user
                    if (context != null) {
                        DailyNotification notification = new DailyNotification(context);
                        notification.beginDailyNotifications();
                    }

                    Intent intent =  new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);

                }
            });
        }

        public void setContext(Context context) {
            this.context = context;
        }
    }


    DatabaseReference fireBase;
    List<String> users = new ArrayList<>();
    ArrayAdapter adapter;
    DatabaseReference mFirebaseDatabaseReference;
    FirebaseRecyclerAdapter<User, UserViewHolder> mFirebaseAdapter;
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);



        fireBase = FirebaseDatabase.getInstance().getReference();
        getAllUsersFromFirebase();

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.recycler_user_single_item,
                UserListActivity.UserViewHolder.class,
                mFirebaseDatabaseReference.child(USER_LIST)) {

            @Override
            protected void populateViewHolder(final UserListActivity.UserViewHolder viewHolder,
                                              User user, int position) {
                if (user.name != null) {
                    viewHolder.username.setText(user.name);
                    viewHolder.username.setVisibility(ImageView.VISIBLE);
                    viewHolder.setContext(getApplicationContext());
                }
            }

        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);


    }
    public void getAllUsersFromFirebase() {
        Log.d("firebase", "getting all users from firebase");
        fireBase.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren()
                                .iterator();
                        users = new ArrayList<>();
                        while (dataSnapshots.hasNext()) {
                            DataSnapshot dataSnapshotChild = dataSnapshots.next();
                            User user = dataSnapshotChild.getValue(User.class);
                            Log.d("user",  "this is a user" + user.name);
                            if (!TextUtils.equals(user.name,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user.name);
                                Log.d("users", "current user " + user.name);
                            }
                        }
                        // All users are retrieved except the one who is currently logged
                        // in device.
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
    }


}
