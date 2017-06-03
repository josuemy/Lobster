package josue.cs190i.cs.ucsb.edu.lobster;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
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
    Bitmap bm;
    String mCurrentPhotoPath;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.create_note_fragment, container, false);

        final Spinner categoryView = (Spinner) v.findViewById(R.id.spinner);
        final Button button = (Button) v.findViewById(R.id.button);

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
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = null;
                try {
                    f = createImageFile();
                }
                catch (IOException ex){}

                //takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                new Intent[]{takePhotoIntent}
                        );
                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        });

        return v;
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

        //catch(Exception ex){}

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("act result", "inside on activity result where data is not null");
                    Uri imageUri = data.getData();
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                        ImageView imageView = (ImageView) getView().findViewById(R.id.add_image_view);
                        imageView.setImageBitmap(bm);
                    } catch (IOException e) {
                        System.err.println("Caught IOException: " + e.getMessage());
                    }

            }
            else{
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView imageView = (ImageView) getView().findViewById(R.id.add_image_view);
                imageView.setImageBitmap(imageBitmap);
            }

        }

    }
}
