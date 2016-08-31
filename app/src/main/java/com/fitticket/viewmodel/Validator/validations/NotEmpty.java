package com.fitticket.viewmodel.Validator.validations;


import android.content.Context;
import android.text.TextUtils;

import com.fitticket.R;

public class NotEmpty extends BaseValidation {

    public static Validation build(Context context) {
        return new NotEmpty(context);
    }

    private NotEmpty(Context context) {
        super(context);
    }

    @Override
    public String getErrorMessage() {
    	//((SignupActivity) mContext).openLoginFragment();
        return mContext.getString(R.string.zvalidations_empty);
    }

    @Override
    public boolean isValid(String text) {
        return !TextUtils.isEmpty(text);
    }
}