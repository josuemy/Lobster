package josue.cs190i.cs.ucsb.edu.lobster;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String USER_LIST = "users";
    private static final String MESSAGES = "messages";
    private static final String ROOMS = "rooms";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    public static String roomName;
    String room;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

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
                        DailyNotificationManager notification = new DailyNotificationManager(context);
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



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        room = null;
        Log.d("current user", "id" + mFirebaseUser.getUid());
        mFirebaseDatabaseReference.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("roomKey")){

                            User user = snapshot.getValue(User.class);
                            room = user.getRoomKey();
                            Log.d("room", "current room" + room);
                            roomName = room;
                            Intent intent = new Intent(UserListActivity.this, MainActivity.class);
                            startActivity(intent);
                            }
                        }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.users_recycler_view);






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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                DailyNotificationManager notification = new DailyNotificationManager(this);
                notification.turnOffNotifications();
                startActivity(new Intent(this, SignInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
