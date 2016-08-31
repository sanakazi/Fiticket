package com.fitticket.viewmodel.Validator;

import android.widget.EditText;


public class FieldValidationException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EditText mTextView;

    public FieldValidationException(String message, EditText textView) {
        super(message);
        mTextView = textView;
    }

    public EditText getTextView() {
        return mTextView;
    }
}