package josue.cs190i.cs.ucsb.edu.lobster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.LayoutInflaterCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Danielle on 6/2/2017.
 */

public class AddEntryFragment extends DialogFragment {
    public int SELECT_PICTURE = 1;
    public int GALLERY_PICTURE = 0;
    private Bitmap bm;
    private String mCurrentPhotoPath;
    private Spinner categoryView;
    private Button button;
    private Button button_cam;
    private Button save_button;
    private EditText editText;
    private ImageView imageView;
    private DatabaseReference mFirebaseDatabaseReference;
    private Uri currentPhotoUri;
    private String mDownloadUrl;
    private Note new_note;
    private MainActivity mainActivity;
    private static final String ROOM_CHILD = "rooms";
    private static final String ROOMKEY_CHILD = UserListActivity.roomName;
    private static final String MESSAGES_CHILD = "messages";
    private static final String LOADING_IMAGE_URL = "http://i.imgur.com/DDA1om1.gifv";
    private String mUserPhotoUrl;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    public AddEntryFragment(){}
    public AddEntryFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.create_note_fragment, container, false);

        categoryView = (Spinner) v.findViewById(R.id.spinner);
        button = (Button) v.findViewById(R.id.button);
        button_cam = (Button) v.findViewById(R.id.button2);
        save_button = (Button) v.findViewById(R.id.save_button);
        editText = (EditText) v.findViewById(R.id.editText);
        imageView = (ImageView) v.findViewById(R.id.add_image_view);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser.getPhotoUrl() != null) {
            mUserPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        List<String> categories = new ArrayList<String>();
        categories.add("Daily Entry");
        categories.add("Other");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, categories);
        categoryView.setAdapter(dataAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(pickIntent, GALLERY_PICTURE);
            }
        });

        button_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("inside save", "Inside save button on click listener");
                //(String name, String content, String time, Bitmap picture)
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                 new_note = new Note(StartingActivity.mUsername, editText.getText().toString(), currentDateTimeString,
                        null, categoryView.getSelectedItem().toString());
                new_note.setUserPhotoUrl(mUserPhotoUrl);

                if (imageView.getDrawable() != null) {
                    new_note.setPictureUrl(LOADING_IMAGE_URL);
                    new_note.setNoteKey(mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD).push().getKey());
                    mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD).child(new_note.getNoteKey())
                            .setValue(new_note, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(StartingActivity.mUsername)
                                                        .child(key)
                                                        .child(currentPhotoUri.getLastPathSegment());

                                        putImageInStorage(storageReference, currentPhotoUri, key);
                                        //new_note.setPictureUrl(mDownloadUrl);
                                    } else {
                                        Log.w("TAG", "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
//                    new_note = new Note("Danielle", editText.getText().toString(), currentDateTimeString,
//                            ((BitmapDrawable) imageView.getDrawable()).getBitmap(), categoryView.getSelectedItem().toString());
                } else {
                    //  The push() method adds an automatically generated ID to the pushed object's path.
                    //  These IDs are sequential which ensures that the new messages will be added to the end of the list.
                    new_note.setNoteKey(mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD).push().getKey());
                    mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD).child(new_note.getNoteKey()).setValue(new_note);
                }
                dismiss();
            }
        });


        return v;
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener( this.mainActivity,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            mDownloadUrl = task.getResult().getDownloadUrl().toString();
//                            Note new_note = new Note(StartingActivity.mUsername, editText.getText().toString(), currentDateTimeString,
//                                    null, categoryView.getSelectedItem().toString());
//                            Note note =\
//                                    new Note(StartingActivity.mUsername,
//                                            task.getResult().getDownloadUrl()
//                                                    .toString());
                            new_note.setPictureUrl(mDownloadUrl);
                            mFirebaseDatabaseReference.child(ROOM_CHILD).child(ROOMKEY_CHILD).child(MESSAGES_CHILD).child(key)
                                    .setValue(new_note);
                        } else {
                            Log.w("TAG", "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "josue.cs190i.cs.ucsb.edu.lobster",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, SELECT_PICTURE);
            }
        }
    }

    public File createImageFile() throws IOException{
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICTURE && resultCode == RESULT_OK){
            currentPhotoUri = data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), currentPhotoUri);
                imageView = (ImageView) getView().findViewById(R.id.add_image_view);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            File f = new File(mCurrentPhotoPath);
            currentPhotoUri = Uri.fromFile(f);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(null);
            imageView.setImageURI(currentPhotoUri);

        }

    }
}
