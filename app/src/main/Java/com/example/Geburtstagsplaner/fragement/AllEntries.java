package com.example.Geburtstagsplaner.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.Geburtstagsplaner.pojo.Birthday;
import com.example.Geburtstagsplaner.util.Databasehelper;
import com.example.Geburtstagsplaner.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class AllEntries extends Fragment {

    private ListView listView;
    private Databasehelper databasehelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_all_entries, container, false);

        listView = view.findViewById(R.id.lv_allEntries);
        databasehelper = new Databasehelper(getActivity());

        String title = "Alle Geburtstage";
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(title);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Birthday> listOfBirthdays = databasehelper.getAllBirthdays();
        System.out.println(listOfBirthdays.size());
        listOfBirthdays.sort(new Comparator<Birthday>() {
            @Override
            public int compare(Birthday o1, Birthday o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        ArrayAdapter<Birthday> adapter = new ArrayAdapter<Birthday>(getContext(),
                R.layout.adapter_view_layout, listOfBirthdays) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                String name = getItem(position).getName();
                String date = getItem(position).getDate();

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.adapter_view_layout, parent, false);

                TextView tv_name = convertView.findViewById(R.id.txt_adapter_view_name);
                TextView tv_date = convertView.findViewById(R.id.txt_adapter_view_birthday);

                tv_name.setText(name);
                tv_date.setText(date);

                return convertView;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Birthday birthday = (Birthday) parent.getItemAtPosition(position);
                BirthdayEditor birthdayEditor = new BirthdayEditor(birthday);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, birthdayEditor);
                fragmentTransaction.addToBackStack("EditEntry");
                fragmentTransaction.commit();
            }
        });
    }
}
