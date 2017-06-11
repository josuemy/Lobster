package josue.cs190i.cs.ucsb.edu.lobster;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener  {


    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Note, NoteViewHolder> mFirebaseAdapter;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String ROOM_CHILD = "rooms";
    private static String ROOMKEY_CHILD = UserListActivity.roomName;
    private static final String MESSAGES_CHILD = "messages";

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView person_name;
        TextView note_content;
        TextView note_time;
        TextView note_category;
        TextView note_key;
        ImageView note_picture;

        public NoteViewHolder(View view) {
            super(view);
            person_name = (TextView) view.findViewById(R.id.person_name);
            note_content = (TextView) view.findViewById(R.id.note_content);
            note_picture = (ImageView) view.findViewById(R.id.note_picture);
            note_category = (TextView) view.findViewById(R.id.note_category);
            note_time = (TextView) view.findViewById(R.id.note_date_time);
            note_key = (TextView) view.findViewById(R.id.note_key);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showAlertDialog(view.getContext(),note_key.getText().toString());
                    return true;
                }
            });
        }

    }

    public static void showAlertDialog(Context context, final String note_Key){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("HEY!");
        alert.setMessage("Are you sure to delete this note? Your loved one might cry :( ");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                FirebaseDatabase.getInstance().getReference()
                        .child(ROOM_CHILD)
                        .child(ROOMKEY_CHILD)
                        .child(MESSAGES_CHILD)
                        .child(note_Key)
                        .setValue(null);
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();
    }

    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FloatingActionButton  fb = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                AddEntryFragment fragment = new AddEntryFragment(MainActivity.this);
                fragment.show(fm, "Dialog Fragment");

            }
        });

        //initialize recycler view and linear layout
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
//        Log.d("ROOM NAME is", ROOMKEY_CHILD);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("before adapter", "before adapter, after reference");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                Note.class,
                R.layout.recycler_note_single_item,
                MainActivity.NoteViewHolder.class,
                mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD)) {


            @Override
            protected void populateViewHolder(final MainActivity.NoteViewHolder viewHolder,
                                              Note note, int position) {
                Log.d("populate", "inside populate view holder");
                if (note.getContent() != null) {
                    Log.d("populcate content", "inside populate view holder populate content");
                    viewHolder.note_content.setText(note.getContent());
                    viewHolder.person_name.setText(note.getPerson_name());
                    viewHolder.note_category.setText(note.getCategory());
                    viewHolder.note_time.setText(note.getTime());
                    viewHolder.note_picture.setVisibility(View.GONE);
                    viewHolder.note_key.setText(note.getNoteKey());
                }

                if(note.getPictureUrl() != null){
                    viewHolder.note_picture.setVisibility(ImageView.VISIBLE);
                    String imageUrl = note.getPictureUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.note_picture.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.note_picture);
                                        } else {
                                            Log.w("TAG", "Getting download url was not successful.",
                                                    task.getException());
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.note_picture.getContext())
                                .load(note.getPictureUrl())
                                .into(viewHolder.note_picture);
                    }

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
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed(){

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

    private void causeCrash() {
        throw new NullPointerException("Fake null pointer exception");
    }


}