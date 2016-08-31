package com.fitticket.viewmodel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fitticket.R;
import com.seatgeek.placesautocomplete.PlacesApi;
import com.seatgeek.placesautocomplete.adapter.AbstractPlacesAutocompleteAdapter;
import com.seatgeek.placesautocomplete.history.DefaultAutocompleteHistoryManager;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.Place;

public class PlacesAutoCompleteAdapter extends AbstractPlacesAutocompleteAdapter {

    public PlacesAutoCompleteAdapter(final Context context, final PlacesApi api, final AutocompleteResultType resultType, final DefaultAutocompleteHistoryManager history) {
        super(context, api, resultType, history);
    }

    @Override
    protected View newView(final ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.autocomplete_listitem, parent, false);
    }

    @Override
    protected void bindView(final View view, final Place item) {
        ((TextView) view).setText(item.description);
    }
}
