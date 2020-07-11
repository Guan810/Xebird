package com.lockon.xebird.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lockon.xebird.R;
import com.lockon.xebird.ViewAdapters.ExploreShowBirdnameRecyclerViewAdapter;
import com.lockon.xebird.ViewModels.ExploreShowBirdNameViewModel;
import com.lockon.xebird.db.BirdBaseDataBase;
import com.lockon.xebird.db.Entities.BirdData;
import com.lockon.xebird.other.History;

import java.util.ArrayList;
import java.util.List;

public class ExploreShowBirdNameFragment extends Fragment {
    private static final String TAG = "InfoShownameFragment";

    private ExploreShowBirdNameViewModel mViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ExploreShowBirdnameRecyclerViewAdapter mAdapter;
    private BirdBaseDataBase bd;
    private EditText edittext;

    public static ExploreShowBirdNameFragment newInstance() {
        return new ExploreShowBirdNameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ExploreShowBirdNameViewModel.class);

        final Observer<List<BirdData>> listObserver = new Observer<List<BirdData>>() {
            @Override
            public void onChanged(List<BirdData> s) {
                mAdapter.changeList(s);
            }
        };

        mViewModel.getBirdDatas().observe(this, listObserver);

        final Observer<Editable> editObserver = new Observer<Editable>() {
            @Override
            public void onChanged(Editable editable) {
                if (editable == null) {
                    mViewModel.getBirdDatas().postValue(new ArrayList<BirdData>());
                } else {
                    String s = editable.toString();
                    List<BirdData> whatget = bd.myDao().findByNameCN(s.trim());
                    mViewModel.getBirdDatas().postValue(whatget);
                }
                History.initInstance(requireContext()).put(editable);
            }
        };

        mViewModel.getEditText().observe(this, editObserver);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explore_show_bird_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bd = BirdBaseDataBase.getInstance(requireContext());

        edittext = view.findViewById(R.id.textview_edit);
        edittext.setText(History.initInstance(getContext()).getLatestInput());

        recyclerView = view.findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ExploreShowBirdnameRecyclerViewAdapter(mViewModel.getBirdDatas().getValue(), this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL));

        view.findViewById(R.id.button_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.getEditText().postValue(edittext.getText());
            }
        });

    }

    public static String getTAG() {
        return TAG;
    }
}