package com.example.Geburtstagsplaner.fragement;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.Geburtstagsplaner.pojo.Birthday;
import com.example.Geburtstagsplaner.util.Databasehelper;
import com.example.Geburtstagsplaner.R;
import com.example.Geburtstagsplaner.util.ShortToast;

import java.util.Calendar;
import java.util.Objects;

public class BirthdayCreation extends Fragment {

    private android.widget.EditText name;
    private TextView dateField;
    private Button saveData;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private Birthday birthday;
    private Birthday tempBirthday;
    private Databasehelper databasehelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_birthday, container, false);

        name = view.findViewById(R.id.txt_name);
        dateField = view.findViewById(R.id.txt_birthday);
        saveData = view.findViewById(R.id.btn_saveData);
        databasehelper = new Databasehelper(getActivity());

        String title = "Geburtstag hinzufügen";
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(title);

        birthday = new Birthday();
        tempBirthday = new Birthday();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if (tempBirthday.getYear() != 0) {
                    year = birthday.getYear();
                    month = birthday.getMonth();
                    day = birthday.getDay();
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener,
                        year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month += 1;
                birthday.setDate(year, month, day);
                if (birthday.getYear() == 0) {
                    ShortToast.makeToast(getContext(), "Gib ein zulässiges Datum ein.");
                    return;
                }
                String date = birthday.getDate();
                dateField.setText(date);
            }
        };

        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birthday.getYear() == 0) {
                    ShortToast.makeToast(getContext(), "Gib ein zulässiges Datum ein.");
                } else {
                    birthday.setName(name.getText().toString());
                    if (birthday.getName().equals("")) {
                        ShortToast.makeToast(getContext(), "Gib einen zulässigen Name ein.");
                    } else {
                        if (databasehelper.getIdByBirthday(birthday) != -1) {
                            ShortToast.makeToast(getContext(), "Dieser Eintrag besteht bereits.");
                        } else {
                            if (databasehelper.addBirthday(birthday)) {
                                ShortToast.makeToast(getContext(), "Geburtstag wurde hinzugefügt.");
                                tempBirthday = birthday.clone();
                                birthday = new Birthday();
                            } else {
                                ShortToast.makeToast(getContext(), "Geburtstag konnte nicht hinzugefügt werden.");
                            }
                        }
                    }
                }
            }
        });
    }
}
