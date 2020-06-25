package com.lockon.xebird;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lockon.xebird.db.BirdBaseDataBase;
import com.lockon.xebird.db.BirdData;
import com.lockon.xebird.db.BirdRecord;
import com.lockon.xebird.db.BirdRecordDao;
import com.lockon.xebird.db.BirdRecordDataBase;
import com.lockon.xebird.db.Checklist;
import com.lockon.xebird.other.ButtonListener;
import com.lockon.xebird.other.History;
import com.lockon.xebird.other.ItemAdapter;
import com.lockon.xebird.other.XeBirdHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of birds to add a bird record.
 */
public class BirdlistFragment extends Fragment {
    private static final String TAG = "BirdlistFragment";


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CHECKLIST_ID = "checklistId";

    private int mColumnCount = 1;
    public String checklistId;

    public static  XeBirdHandler.BirdlistHandler birdlistHandler;
    public MyBirdRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BirdlistFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BirdlistFragment newInstance(int columnCount, String checklistId) {
        BirdlistFragment fragment = new BirdlistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putSerializable(ARG_CHECKLIST_ID, checklistId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            checklistId = getArguments().getString(ARG_CHECKLIST_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView!");

        BirdRecordDataBase bd = BirdRecordDataBase.getInstance(requireContext());
        checklistId =  getArguments().getString("checklistId");


        View view = inflater.inflate(R.layout.fragment_birdlist_list,
                container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyBirdRecyclerViewAdapter(this, new ArrayList<BirdData>());
            recyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText edittext = view.findViewById(R.id.textview_edit);
        edittext.setText(History.initInstance(getContext()).getLatestInput());

        birdlistHandler = new XeBirdHandler.BirdlistHandler(this);
        view.findViewById(R.id.button_search).setOnClickListener(
                new ButtonListener(view, birdlistHandler, getContext()));
    }

    @Override
    public void onDetach() {
        BirdBaseDataBase.getInstance(this.getContext()).close();
        birdlistHandler.removeMessages(XeBirdHandler.SETLIST);
        birdlistHandler.removeMessages(XeBirdHandler.SETNULLTEXT);
        super.onDetach();
    }

    final public String getTAG() {
        return TAG;
    }
}