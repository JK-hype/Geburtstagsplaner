package com.example.Geburtstagsplaner.fragement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
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
import com.example.Geburtstagsplaner.util.ImportExportCSV;
import com.example.Geburtstagsplaner.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Calendar extends Fragment {

    private ImageButton btnAddDate;
    private Button btnListAllEntries;
    private CalendarView cvCalendar;
    private ListView listView;
    private Databasehelper databasehelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.calendar, container, false);

        btnAddDate = view.findViewById(R.id.btn_addData);
        btnListAllEntries = view.findViewById(R.id.btn_listAllEntries);
        cvCalendar = view.findViewById(R.id.calenderview);
        listView = view.findViewById(R.id.lv_entries_of_date);
        databasehelper = new Databasehelper(getContext());

        String title = "Geburtstagsplaner";
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle(title);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.overflow_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.doImport) {
            ImportExportCSV.doImport(getActivity());
        } else if (id == R.id.doExport) {
            ImportExportCSV.doExport(getActivity());
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        setListView(month);

        btnAddDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BirthdayCreator birthdayCreator = new BirthdayCreator();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, birthdayCreator);
                fragmentTransaction.addToBackStack("addBirthday");
                fragmentTransaction.commit();
            }
        });

        btnListAllEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllEntries allEntries = new AllEntries();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, allEntries);
                fragmentTransaction.addToBackStack("listAllEntries");
                fragmentTransaction.commit();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Birthday birthday = (Birthday) parent.getItemAtPosition(position);
                BirthdayEditor birthdayEditor = new BirthdayEditor(birthday);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) Objects.requireNonNull(getContext())).
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_fragments, birthdayEditor);
                fragmentTransaction.addToBackStack("editEntry");
                fragmentTransaction.commit();
            }
        });

        cvCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month += 1;
                setListView(month);
            }
        });

    }

    private void setListView(int month) {
        ArrayList<Birthday> listOfBirthdays = databasehelper.getBirthdaysByMonth(month);
        listOfBirthdays.sort(new Comparator<Birthday>() {
            @Override
            public int compare(Birthday o1, Birthday o2) {
                return o1.getDay() - o2.getDay();
            }
        });

        //set Adapter for ListView
        ArrayAdapter<Birthday> adapter = new ArrayAdapter<Birthday>(getContext(),
                R.layout.adapter_view_layout, listOfBirthdays) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                String name = getItem(position).getName();
                String date = getItem(position).getDate();

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.adapter_view_layout, parent, false);

                //attach AdapterViewLayout to Adapter
                TextView tvName = convertView.findViewById(R.id.txt_adapter_view_name);
                TextView tvDate = convertView.findViewById(R.id.txt_adapter_view_birthday);

                tvName.setText(name);
                tvDate.setText(date);

                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }
}
