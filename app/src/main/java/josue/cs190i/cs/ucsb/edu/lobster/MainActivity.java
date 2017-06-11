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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;


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
    private OnReadyListener readyListener;
    private OnButtonListener buttonListener;


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
        CircleImageView userImageView;

        public NoteViewHolder(View view) {
            super(view);
            person_name = (TextView) view.findViewById(R.id.person_name);
            note_content = (TextView) view.findViewById(R.id.note_content);
            note_picture = (ImageView) view.findViewById(R.id.note_picture);
            note_category = (TextView) view.findViewById(R.id.note_category);
            note_time = (TextView) view.findViewById(R.id.note_date_time);
            note_key = (TextView) view.findViewById(R.id.note_key);
            userImageView = (CircleImageView) view.findViewById(R.id.profileImage);

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
        AlertDialog.Builder alert = new AlertDialog.Builder(context,R.style.MyDialogTheme);
        alert.setTitle("HEY!");
        alert.setMessage("Do you want to delete this note? Your loved one might cry :( ");
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


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Log.d("home", "home pressed");
                    mMessageRecyclerView.getRecycledViewPool().clear();
                    mFirebaseAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                            Note.class,
                            R.layout.recycler_note_single_item,
                            MainActivity.NoteViewHolder.class,
                            mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD)) {


                        @Override
                        protected void populateViewHolder(final MainActivity.NoteViewHolder viewHolder,
                                                          Note note, int position) {

//                            if (note.getUserPhotoUrl() == null) {
//                                viewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                                        R.drawable.ic_account_circle_black_36dp));
//                            } else {
//                                Glide.with(MainActivity.this)
//                                        .load(note.getUserPhotoUrl())
//                                        .into(viewHolder.userImageView);
//                            }

                            if (note.getContent() != null) {
                                Log.d("populcate content", "viewholder content" + note.getContent());

                                viewHolder.note_content.setText(note.getContent());
                                viewHolder.person_name.setText(note.getPerson_name());
                                viewHolder.note_category.setText(note.getCategory());
                                viewHolder.note_time.setText(note.getTime());
                                viewHolder.note_picture.setVisibility(View.GONE);
                                viewHolder.note_key.setText(note.getNoteKey());

                                if (position % 2 == 0)
                                    viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.even));
                                else
                                    viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.odd));
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
                            Log.d("before", "before on button listener");
                           // buttonListener.onButton();
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
                            buttonListener.onButton();
                        }
                    });

                    return true;
                case R.id.navigation_dashboard:
                    Log.d("you", "pressed you");
                    mMessageRecyclerView.getRecycledViewPool().clear();
                    mFirebaseAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                            Note.class,
                            R.layout.recycler_note_single_item,
                            MainActivity.NoteViewHolder.class,
                            mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD)) {


                        @Override
                        protected void populateViewHolder(final MainActivity.NoteViewHolder viewHolder,
                                                          Note note, int position) {

//                            if (note.getUserPhotoUrl() == null) {
//                                viewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                                        R.drawable.ic_account_circle_black_36dp));
//                            } else {
//                                Glide.with(MainActivity.this)
//                                        .load(note.getUserPhotoUrl())
//                                        .into(viewHolder.userImageView);
//                            }

                            if (note.getPerson_name().equals(StartingActivity.mUsername)) {
                                viewHolder.note_content.setText(note.getContent());
                                viewHolder.person_name.setText(note.getPerson_name());
                                viewHolder.note_category.setText(note.getCategory());
                                viewHolder.note_time.setText(note.getTime());
                                viewHolder.note_picture.setVisibility(View.GONE);
                                viewHolder.note_key.setText(note.getNoteKey());
                                if (note.getPictureUrl() != null) {
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

                            } else {
                                viewHolder.note_content.setVisibility(View.GONE);
                                viewHolder.person_name.setVisibility(View.GONE);
                                viewHolder.note_category.setVisibility(View.GONE);
                                viewHolder.note_time.setVisibility(View.GONE);
                                viewHolder.note_picture.setVisibility(View.GONE);
                                viewHolder.note_key.setVisibility(View.GONE);
                                viewHolder.userImageView.setVisibility(View.GONE);
                            }
                            Log.d("before", "before on button listener");
                           // buttonListener.onButton();
                        }};
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
                            buttonListener.onButton();
                        }
                    });

                   //mMessageRecyclerView.setAdapter(mFirebaseAdapter);


                    return true;


                case R.id.navigation_notifications:
                    Log.d("lobster", "lobster button pressed");
                    mMessageRecyclerView.getRecycledViewPool().clear();
                    mFirebaseAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                            Note.class,
                            R.layout.recycler_note_single_item,
                            MainActivity.NoteViewHolder.class,
                            mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD)) {


                        @Override
                        protected void populateViewHolder(final MainActivity.NoteViewHolder viewHolder,
                                                          Note note, int position) {
//                            if (note.getUserPhotoUrl() == null) {
//                                viewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
//                                        R.drawable.ic_account_circle_black_36dp));
//                            } else {
//                                Glide.with(MainActivity.this)
//                                        .load(note.getUserPhotoUrl())
//                                        .into(viewHolder.userImageView);
//                            }
                            if (!note.getPerson_name().equals(StartingActivity.mUsername)) {
                                viewHolder.note_content.setText(note.getContent());
                                viewHolder.person_name.setText(note.getPerson_name());
                                viewHolder.note_category.setText(note.getCategory());
                                viewHolder.note_time.setText(note.getTime());
                                viewHolder.note_picture.setVisibility(View.GONE);
                                viewHolder.note_key.setText(note.getNoteKey());
                                if (note.getPictureUrl() != null) {
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

                            } else {
                                viewHolder.note_content.setVisibility(View.GONE);
                                viewHolder.person_name.setVisibility(View.GONE);
                                viewHolder.note_category.setVisibility(View.GONE);
                                viewHolder.note_time.setVisibility(View.GONE);
                                viewHolder.note_picture.setVisibility(View.GONE);
                                viewHolder.note_key.setVisibility(View.GONE);
                                viewHolder.userImageView.setVisibility(View.GONE);
                            }

                        }};
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
                            Log.d("before", "before on button listener");
                            buttonListener.onButton();
                        }
                    });



                   // mMessageRecyclerView.setAdapter(mFirebaseAdapter);
                    return true;
            }


            return false;
        }

    };

    public interface OnReadyListener {
        public void onReady();
    }


    public interface OnButtonListener{
        public void onButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

       //mTextMessage = (TextView) findViewById(R.id.message);
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


        mMessageRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(false);



        buttonListener = new OnButtonListener() {
            @Override
            public void onButton() {
                Log.d("insidd", "inside on button listener");
                mMessageRecyclerView.setAdapter(mFirebaseAdapter);
            }
        };

        readyListener = new OnReadyListener() {
            @Override
            public void onReady() {
                mFirebaseAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(
                        Note.class,
                        R.layout.recycler_note_single_item,
                        MainActivity.NoteViewHolder.class,
                        mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD)) {


                    @Override
                    protected void populateViewHolder(final MainActivity.NoteViewHolder viewHolder,
                                                      Note note, int position) {

                        if (note.getUserPhotoUrl() == null) {
                            viewHolder.userImageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,
                                    R.drawable.ic_account_circle_black_36dp));
                        } else {
                            Glide.with(MainActivity.this)
                                    .load(note.getUserPhotoUrl())
                                    .into(viewHolder.userImageView);
                        }

                        if (note.getContent() != null) {
                            Log.d("populcate content", "viewholder content" + note.getContent());

                            viewHolder.note_content.setText(note.getContent());
                            viewHolder.person_name.setText(note.getPerson_name());
                            viewHolder.note_category.setText(note.getCategory());
                            viewHolder.note_time.setText(note.getTime());
                            viewHolder.note_picture.setVisibility(View.GONE);
                            viewHolder.note_key.setText(note.getNoteKey());

                            if (position % 2 == 0)
                                viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.even));
                            else
                                viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.odd));

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

                mMessageRecyclerView.setAdapter(mFirebaseAdapter);
            }
        };


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.d("before adapter", "before adapter, after reference");
        FirebaseDatabase.getInstance().getReference().child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("roomKey")) {
                            User user = snapshot.getValue(User.class);
                            ROOMKEY_CHILD = user.getRoomKey();
                            ROOMKEY_CHILD = ROOMKEY_CHILD.replace(".", " ");
                            //isReady = true;
                            Log.d("getting", "getting room key" + ROOMKEY_CHILD);
                            readyListener.onReady();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);


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
            case R.id.about:
                Log.d("about", "about was clicked");
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
                return true;
            case R.id.sign_out_menu:
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                mFirebaseUser = null;
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.new_lobster:
                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(mFirebaseUser.getUid())
                        .child("roomKey")
                        .setValue(null);
                Intent intent2 = new Intent(MainActivity.this, StartingActivity.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



}