package com.example.rixoro.matrixcam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Bitmap originalImage;
    private Bitmap currentImage;
    private Bitmap oldImage;

    private HashMap<String, EditText> edtxtMatrix;
    private ArrayList<EditText> editTexts;
    private GridView mLayout;
    private GridLayout.LayoutParams glp;
    private int matrixDimension;
    private RadioGroup dimens;
    private EditTextMatrixAdapter editTextMatrixAdapter;
    private String photoFilename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        originalImage = null;
        mImageView = (ImageView) findViewById(R.id.photo);

        dispatchTakePictureIntent();

        edtxtMatrix = new HashMap<>();
        editTexts = new ArrayList<>();
        mLayout = (GridView) findViewById(R.id.matrix_grid);
        matrixDimension = 0;
        dimens = (RadioGroup) findViewById(R.id.radio_group);
        dimens.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rdbtn = (RadioButton) findViewById(checkedId);
                matrixDimension = Integer.parseInt(rdbtn.getText().toString().substring(0,1));
                updateGridUI(matrixDimension);
                editTextMatrixAdapter = new EditTextMatrixAdapter(editTexts, MainActivity.this);
                mLayout.setAdapter(editTextMatrixAdapter);
                mLayout.setNumColumns(matrixDimension);
            }
        });

    }


    private void updateGridUI(int dimen){
        editTexts.clear();
        for(int i=0;i<dimen;i++){
            for(int j=0;j<dimen;j++){
                EditText edtxt = new EditText(this);
                editTexts.add(edtxt);
            }
        }
    }

    private int[][] getFilter(){
        int[][] filter = new int[matrixDimension][matrixDimension];

        for(int i=0;i<matrixDimension;i++) {
            for (int j = 0; j < matrixDimension; j++) {
                String str = editTexts.get(i * matrixDimension + j).getText().toString();
                if(str!=null)
                    filter[i][j] = Integer.parseInt(str);
                else
                    filter[i][j] = 0;
            }
        }

        return filter;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            originalImage = imageBitmap;
            currentImage = imageBitmap;
            mImageView.setImageBitmap(currentImage);
        }
    }

    public void retakePhoto(View view) {
        dispatchTakePictureIntent();
    }

    private void applyFilter(int w, int l, int[][] filter) {
        Bitmap newImg = currentImage.copy(Bitmap.Config.ARGB_8888, true);;

        int div = 0;
        for(int[] a:filter)
            for(int i:a)
                div += i;
        if(div==0)
            div = 1;

        for(int i=0;i<l;i++){
            for(int j=0;j<w;j++){
                int avg = 0;
                int ravg = 0;
                int gavg = 0;
                int bavg = 0;
                for(int x=0;x<filter.length;x++){
                    for(int y=0;y<filter[0].length;y++){
                        int m = i - (x-matrixDimension);
                        int n = j - (y-matrixDimension);
                        int p;

                        if(m<0 && n<0)
                            p = currentImage.getPixel(0,0);
                        else if(m>=l && n>=w)
                            p = currentImage.getPixel(l-1,w-1);
                        else if(m>=l && n<0)
                            p = currentImage.getPixel(l-1,0);
                        else if(m<0 && n>=w)
                            p = currentImage.getPixel(0,w-1);
                        else if(!(m<0) && n<0)
                            p = currentImage.getPixel(m,0);
                        else if(m<0 && !(n<0))
                            p = currentImage.getPixel(0,n);
                        else if(!(m>=l) && n>=w)
                            p = currentImage.getPixel(m,w-1);
                        else if(m>=l && !(n>=w))
                            p = currentImage.getPixel(l-1,n);
                        else
                            p = currentImage.getPixel(m,n);

                        int f = filter[x][y];

                        int a = Color.alpha(p);
                        int r = Color.red(p);
                        int g = Color.green(p);
                        int b = Color.blue(p);

                        avg += f*a;
                        ravg += f*r;
                        gavg += f*g;
                        bavg += f*b;
                    }
                }
                avg = avg/div;
                ravg = ravg/div;
                gavg = gavg/div;
                bavg = bavg/div;
                if(avg>255)
                    avg = 255;
                else if(avg<0)
                    avg = 0;
                if(ravg>255)
                    ravg = 255;
                else if(ravg<0)
                    ravg = 0;
                if(gavg>255)
                    gavg = 255;
                else if(gavg<0)
                    gavg = 0;
                if(bavg>255)
                    bavg = 255;
                else if(bavg<0)
                    bavg = 0;

                newImg.setPixel(i, j, Color.argb(avg, ravg, gavg, bavg));
                oldImage = currentImage;
                currentImage = newImg;
            }
        }

    }

    public void savePhoto(View view) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("SAVE", "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            currentImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            MediaStore.Images.Media.insertImage(getContentResolver(), currentImage, null , null);
            //fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("SAVE", "File not found: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("SAVE", "Error accessing file: " + e.getMessage());
        }
    }

    public void revertChanges(View view) {
        currentImage = originalImage;
        mImageView.setImageBitmap(currentImage);
    }

    public void applyMatrix(View view) {
        if(matrixDimension==0){
            Toast.makeText(this, "Please select a matrix size.", Toast.LENGTH_SHORT).show();
        }
        else{
            int x = currentImage.getWidth();
            int y = currentImage.getHeight();
            int[][] filter = editTextMatrixAdapter.getFilter();

            for(int[] ia:filter) {
                for (int i:ia) {
                    String str = Integer.toString(i);
//                    Log.d("EDTXT", str);
                }
            }

            applyFilter(y, x, filter);
            mImageView.setImageBitmap(currentImage);

        }
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        photoFilename ="MI_"+ timeStamp;

        View viewGrp = getLayoutInflater().inflate(R.layout.save_photo_dialog, (ViewGroup) findViewById(R.id.main_activity), false);

        final EditText edtxtname = (EditText) viewGrp.findViewById(R.id.edtxt_save_photo);
        edtxtname.setText(photoFilename);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setTitle("Save New Image").setView(viewGrp)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        photoFilename = edtxtname.getText().toString() +".jpg";
                    }
                });
        alertBuilder.show();
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + photoFilename);
        return mediaFile;
    }
}
