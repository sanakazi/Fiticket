package com.fitticket.viewmodel.Validator.validations;

import android.content.Context;
import android.util.Patterns;

import com.fitticket.R;

public class IsEmail extends BaseValidation {
	
    private IsEmail(Context context) {
        super(context);
    }

    public static Validation build(Context context) {
        return new IsEmail(context);
    }

    @Override
    public String getErrorMessage() {
        return mContext.getString(R.string.zvalidations_not_email);
    }

    @Override
    public boolean isValid(String text) {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches();
        		
        		 
    }
}