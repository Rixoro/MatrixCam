package com.example.rixoro.matrixcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;
    private Bitmap photoBM;

    private HashMap<String, EditText> edtxtMatrix;
    private ArrayList<EditText> editTexts;
    private GridView mLayout;
    private GridLayout.LayoutParams glp;
    private int matrixDimension;
    private RadioGroup dimens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoBM = null;
        mImageView = (ImageView) findViewById(R.id.photo);

        dispatchTakePictureIntent();

        edtxtMatrix = new HashMap<>();
        editTexts = new ArrayList<>();
        mLayout = (GridView) findViewById(R.id.matrix_grid);
        matrixDimension = 3;
        dimens = (RadioGroup) findViewById(R.id.radio_group);
        dimens.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rdbtn = (RadioButton) findViewById(checkedId);
                matrixDimension = Integer.parseInt(rdbtn.getText().toString().substring(0,1));
                updateGridUI(matrixDimension);
                mLayout.setAdapter(new EditTextMatrixAdapter(editTexts, MainActivity.this));
                mLayout.setNumColumns(matrixDimension);
            }
        });
        
    }


    private void updateGridUI(int dimen){
        editTexts.clear();
        for(int i=0;i<dimen;i++){
            for(int j=0;j<dimen;j++){
                EditText edtxt = new EditText(this);
                edtxtMatrix.put(i+":"+j, edtxt);
                editTexts.add(edtxt);
            }
        }
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
            photoBM = imageBitmap;
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void takePicture(View view) {
        dispatchTakePictureIntent();
    }
}
