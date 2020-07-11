package com.lockon.xebird.ViewModels;

import android.text.Editable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lockon.xebird.db.Entities.BirdData;

import java.util.List;

public class ExploreShowBirdNameViewModel extends ViewModel {
    private MutableLiveData<List<BirdData>> BirdDatas;
    private MutableLiveData<Editable> EditText;

    public MutableLiveData<List<BirdData>> getBirdDatas() {
        if (BirdDatas == null) {
            BirdDatas = new MutableLiveData<>();
        }
        loadBirdDatas();
        return BirdDatas;
    }

    public MutableLiveData<Editable> getEditText() {
        if (EditText == null) {
            EditText = new MutableLiveData<>();
        }
        return EditText;
    }

    private void loadBirdDatas() {

    }
}