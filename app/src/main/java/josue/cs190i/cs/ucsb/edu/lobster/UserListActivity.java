package josue.cs190i.cs.ucsb.edu.lobster;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;

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



                    Intent intent =  new Intent(view.getContext(), MainActivity.class);

                    view.getContext().startActivity(intent);

                }
            });
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
                startActivity(new Intent(this, SignInActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
