package com.example.mypets.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mypets.R;

import java.util.Locale;

public class FragmentUserInfo extends Fragment {

    private TextView tvLanguage, tvAccountInfo, tvSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        initView(view);

        tvLanguage.setOnClickListener(v -> showLanguageDialog());

        tvAccountInfo.setOnClickListener(v -> {
            // Chuyển sang màn hình thông tin tài khoản hoặc hiển thị thông tin tài khoản
        });

        tvSupport.setOnClickListener(v -> {
            // Chuyển sang màn hình ủng hộ hoặc thực hiện chức năng khác
        });

        return view;
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "Tiếng Việt"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    if (which == 0) {
                        // Chọn Tiếng Anh
                        setLocale("en");
                    } else {
                        // Chọn Tiếng Việt
                        setLocale("vi");
                    }
                });

        builder.create().show();
    }

    private void initView(View view) {
        tvLanguage = view.findViewById(R.id.tv_language);
        tvAccountInfo = view.findViewById(R.id.tv_account_info);
        tvSupport = view.findViewById(R.id.tv_support);
    }

    private void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        Intent intent = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent);
    }

}
