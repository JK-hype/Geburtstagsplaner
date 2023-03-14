package com.example.Geburtstagsplaner.fragement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.Geburtstagsplaner.pojo.Birthday;
import com.example.Geburtstagsplaner.util.Databasehelper;
import com.example.Geburtstagsplaner.R;
import com.example.Geburtstagsplaner.util.ShortToast;

import java.util.Objects;

public class BirthdayEditor extends Fragment {

    private int id;
    private EditText txtName;
    private EditText txtDateField;
    private Button btnEditEntry;
    private Button btnDeleteEntry;
    private Databasehelper databasehelper;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private final Birthday birthday;

    public BirthdayEditor(Birthday birthday) {
        this.birthday = birthday;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_entry, container, false);

        txtName = view.findViewById(R.id.txt_edit_name);
        txtDateField = view.findViewById(R.id.txt_edit_dateField);
        btnEditEntry = view.findViewById(R.id.btn_editEntry);
        btnDeleteEntry = view.findViewById(R.id.btn_deleteEntry);

        String title = "Geburtstag ändern";
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity()))
                .getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar())
                .setTitle(title);

        txtName.setHint(birthday.getName());
        txtDateField.setHint(birthday.getDate());

        databasehelper = new Databasehelper(getContext());

        id = databasehelper.getIdByBirthday(birthday);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = birthday.getYear();
                int month = birthday.getMonth();
                int day = birthday.getDay();
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
                txtDateField.setText(date);
            }
        };

        btnEditEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtName.getText().toString();
                if (!name.isEmpty()) {
                    birthday.setName(txtName.getText().toString());
                }
                if (databasehelper.updateBirthdayById(birthday, id)) {
                    ShortToast.makeToast(getContext(), "Eintrag geändert.");
                }

            }
        });

        btnDeleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(true);
                builder.setTitle("Löschen");
                builder.setMessage("Willst du " + birthday.getName() + ", " + birthday.getDate() + " löschen?");
                builder.setPositiveButton("Ja",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (databasehelper.deleteBirthdayById(id)) {
                                    ShortToast.makeToast(getContext(), birthday.getName()
                                            + "," + birthday.getDate() + " wurde gelöscht.");
                                    AllEntries allEntries = new AllEntries();
                                    FragmentTransaction fragmentTransaction = ((FragmentActivity)
                                            Objects.requireNonNull(getContext()))
                                            .getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.container_fragments, allEntries);
                                    fragmentTransaction.addToBackStack("ListAllEntries");
                                    fragmentTransaction.commit();
                                } else {
                                    ShortToast.makeToast(getContext(), birthday.getName()
                                            + ", " + birthday.getDate() + " konnte nicht gelöscht werden.");
                                }
                            }
                        });
                builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
