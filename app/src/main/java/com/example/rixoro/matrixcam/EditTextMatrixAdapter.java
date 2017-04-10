package com.example.rixoro.matrixcam;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rixoro on 4/4/2017.
 */

public class EditTextMatrixAdapter extends BaseAdapter {
    ArrayList<EditText> edtxtList;
    private Context mContext;

    public EditTextMatrixAdapter(ArrayList<EditText> list, Context c){
        super();
        mContext = c;
        edtxtList = list;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EditText edtxt;
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
        edtxt.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        return edtxt;
    }

}
