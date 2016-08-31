package com.fitticket.viewmodel.Validator;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


public class FormUtils {

    public static void showKeyboard(Context context, TextView textView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            // only will trigger it if no physical keyboard is open
            imm.showSoftInput(textView, 0);
        }
    }

    public static void hideKeyboard(Context context, TextView textView) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            // only will trigger it if no physical keyboard is open
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        }
    }
}