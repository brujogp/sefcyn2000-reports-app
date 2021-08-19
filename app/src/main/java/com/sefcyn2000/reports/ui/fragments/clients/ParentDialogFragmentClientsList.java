package com.sefcyn2000.reports.ui.fragments.clients;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.sefcyn2000.reports.R;

public class ParentDialogFragmentClientsList extends DialogFragment {
    private static ParentDialogFragmentClientsList instance = getInstance();

    public ParentDialogFragmentClientsList() {
    }

    public static ParentDialogFragmentClientsList getInstance() {
        if (instance == null) {
            instance = new ParentDialogFragmentClientsList();
        }
        return instance;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_float_select_client, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentManager fragmentManager = getChildFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragment_client_list_container, ListClientsFragment.class, null)
                .commit();

    }
}
