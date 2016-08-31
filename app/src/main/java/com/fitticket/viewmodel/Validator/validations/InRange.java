package com.fitticket.viewmodel.Validator.validations;

import android.content.Context;

import com.fitticket.R;

public class InRange extends BaseValidation {

    private int mMin;
    private int mMax;

    private InRange(Context context, int min, int max) {
        super(context);
        mMin = min;
        mMax = max;
    }

    public static Validation build(Context context, int min, int max) {
        return new InRange(context, min, max);
    }

    @Override
    public String getErrorMessage() {
        return mContext.getString(R.string.zvalidations_not_in_range, mMin, mMax);
    }

    @Override
    public boolean isValid(String text) {
        try {
            int value = Integer.parseInt(text);
            if ((value > mMin) && (value < mMax)) {
                return true;
            }
        } catch (NumberFormatException ignored) {
        }
        return false;
    }
}