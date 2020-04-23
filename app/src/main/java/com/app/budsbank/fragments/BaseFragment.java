package com.app.budsbank.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.budsbank.interfaces.Communicator;

public class BaseFragment extends Fragment implements View.OnClickListener {
    protected Context mContext;
    protected LinearLayoutManager linearLayoutManager;
    protected Communicator communicator;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        communicator = (Communicator) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }

    protected void hideKeyboardOnSearch(EditText editText) {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_SEARCH)) {

                    return true;

                }
                return false;
            }
        });
    }
}

