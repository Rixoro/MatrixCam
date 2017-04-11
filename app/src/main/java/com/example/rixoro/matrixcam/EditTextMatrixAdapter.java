package com.example.rixoro.matrixcam;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rixoro on 4/4/2017.
 */

public class EditTextMatrixAdapter extends BaseAdapter {
    ArrayList<EditText> edtxtList;
    ArrayList<String> edtxtValues;
    private Context mContext;

    public EditTextMatrixAdapter(ArrayList<EditText> list, Context c){
        super();
        mContext = c;
        edtxtList = list;
        edtxtValues = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return edtxtList.size();
    }

    @Override
    public Object getItem(int position) {
        return edtxtList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int[][] getFilter(){
        int dimen = (int) Math.sqrt(edtxtList.size());
        int[][] filter = new int[dimen][dimen];

        for(int i=0;i<dimen;i++) {
            for (int j = 0; j < dimen; j++) {
                String str = edtxtValues.get(i * dimen + j);
                if(str.length()>0)
                    filter[i][j] = Integer.parseInt(str);
                else
                    filter[i][j] = 0;
            }
        }

        return filter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EditText edtxt;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            edtxt = new EditText(mContext);
            edtxt.setLayoutParams(new GridView.LayoutParams(100, 80));
            edtxt.setPaddingRelative(5,5,5,5);
        }
        else {
            edtxt = (EditText) convertView;
        }
        edtxt.setId(position);
        edtxt.setTextSize(15);
        edtxtValues.add(edtxt.getText().toString());
        edtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                edtxtValues.set(position, edtxt.getText().toString());
            }
        });
        edtxt.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        return edtxt;
    }

}
