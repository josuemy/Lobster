package josue.cs190i.cs.ucsb.edu.lobster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
    Bitmap bm;
    String mCurrentPhotoPath;
    Uri selectedImageUri;
    Spinner categoryView;
    Button button;
    Button button_cam;
    Button save_button;
    EditText editText;
    ImageView imageView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.create_note_fragment, container, false);

        categoryView = (Spinner) v.findViewById(R.id.spinner);
        button = (Button) v.findViewById(R.id.button);
        button_cam = (Button) v.findViewById(R.id.button2);
        save_button = (Button) v.findViewById(R.id.save_button);
        editText = (EditText) v.findViewById(R.id.editText);
        imageView = (ImageView) v.findViewById(R.id.add_image_view);

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
                //(String name, String content, String time, Bitmap picture)
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                Note new_note = new Note("Danielle", editText.getText().toString(), currentDateTimeString,
                        ((BitmapDrawable)imageView.getDrawable()).getBitmap(), categoryView.getSelectedItem().toString());

                MainActivity.getAdapter().add(new_note);
                dismiss();
            }
        });


        return v;
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
            Uri imageUri = data.getData();
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                imageView = (ImageView) getView().findViewById(R.id.add_image_view);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                System.err.println("Caught IOException: " + e.getMessage());
            }
        }
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            selectedImageUri = contentUri;
            imageView = (ImageView) getView().findViewById(R.id.add_image_view);
            imageView.setImageURI(null);
            imageView.setImageURI(selectedImageUri);

        }

    }
}
