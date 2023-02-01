package com.example.Geburtstagsplaner.util;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.Geburtstagsplaner.R;
import com.example.Geburtstagsplaner.pojo.Birthday;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;


public class ImportExportCSV {

    private static final ArrayList<Birthday> listOfBirthdays = new ArrayList<>();
    private static Databasehelper databasehelper;

    public static void doImport(Activity activity) {
        checkPermission(activity);

        //custom class FileChooser, allows the user to choose a file by searching through directories
        FileChooser fileChooser = new FileChooser(activity);
        fileChooser.setExtension("csv");
        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(File file) {
                readFile(activity, file);
                importToDatabase(activity);
            }
        });
        fileChooser.showDialog();
    }

    private static void readFile(Activity activity, File file) {
        listOfBirthdays.clear();
        CSVReader csvReader = null;
        String[] row;

        try {
            Reader reader = Files.newBufferedReader(file.getAbsoluteFile().toPath());
            CSVParser parser = new CSVParserBuilder().withSeparator(',').withIgnoreQuotations(true).
                    build();
            csvReader = new CSVReaderBuilder(reader).withSkipLines(1).withCSVParser(parser).build();
        } catch (IOException e) {
            ShortToast.makeToast(activity, "Die Excel Datei konnte nicht gelesen werden.");
        }
        if (csvReader != null) {
            try {
                while ((row = csvReader.readNext()) != null) {
                    //reads out file and adds to listOfBirthdays
                    listOfBirthdays.add(new Birthday(row[0], Integer.parseInt(row[1]), Integer.parseInt
                            (row[2]), Integer.parseInt(row[3])));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                ShortToast.makeToast(activity, "Die Excel Datei hat zu wenige Zeilen.");
            } catch (NumberFormatException e) {
                ShortToast.makeToast(activity, "Das Datum hat ein falsches Format");
            } catch (Exception e) {
                e.printStackTrace();
                ShortToast.makeToast(activity, "Die Excel Datei ist beschädigt.");
            }
            try {
                csvReader.close();
            } catch (Exception e) {
                ShortToast.makeToast(activity, "Die Excel Datei konnte nicht geschlossen werden.");
            }
        }
    }

    private static void importToDatabase(Activity activity) {
        databasehelper = new Databasehelper(activity);
        ArrayList<Birthday> dbBirthdays = databasehelper.getAllBirthdays();
        listOfBirthdays.removeAll(dbBirthdays);
        int i = 1;
        for (Birthday birthday : listOfBirthdays) {
            //breaks if entry is formatted wrong
            if (birthday.getYear() == 0 || birthday.getName().equals("")) {
                ShortToast.makeToast(activity, "Die Excel Datei enthält unzulässige Einträge " +
                        "in Zeile " + i + ".");
                break;
            } else {
                databasehelper.addBirthday(birthday);
            }
            i++;
        }
    }

    public static void doExport(Activity activity) {
        checkPermission(activity);

        CSVWriter csvWriter = null;
        File file;

        databasehelper = new Databasehelper(activity);
        ArrayList<Birthday> dbBirthdays = databasehelper.getAllBirthdays();

        file = new File(activity.getExternalFilesDir("Geburtstage").
                getAbsolutePath() + "/Geburtstage.csv");
        try {
            if (file.createNewFile()) {
                ShortToast.makeToast(activity, "Eine neue Excel Datei wurde angelegt.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter(file.getAbsolutePath());
            csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            ArrayList<String[]> allData = new ArrayList<String[]>();
            allData.add(new String[]{"Name", "Jahr", "Monat", "Tag"});
            for (Birthday birthday : dbBirthdays) {
                String[] row = {birthday.getName(), String.valueOf(birthday.getYear()), String.valueOf(
                        birthday.getMonth()), String.valueOf(birthday.getDay())};
                allData.add(row);
            }
            csvWriter.writeAll(allData);
            ShortToast.makeToast(activity, "Die Excel Datei wurde in " + file.getAbsolutePath() + " gespeichert.");
        } catch (Exception e) {
            ShortToast.makeToast(activity, "Die Excel Datei konnte nicht geschrieben werden.");
            e.printStackTrace();
        }
        try {
            csvWriter.close();
        } catch (IOException | NullPointerException e) {
            ShortToast.makeToast(activity, "Die Excel Datei konnte nicht geschlossen werden.");
            e.printStackTrace();
        }

        attachToEmail(activity, file);
    }

    private static void attachToEmail(Activity activity, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        LayoutInflater inflater = activity.getLayoutInflater();
        //inflates custom view of dialog
        View view = inflater.inflate(R.layout.writting_email, null);
        final EditText email = view.findViewById(R.id.email);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(true);
        builder.setTitle("Email");
        builder.setMessage("Willst du eine Email mit der Excel Datei senden?");
        builder.setView(view).
                setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //redirects to email app with address, file and text
                        intent.setType("vnd.android.cursor.dir/email");
                        String[] to = {email.getText().toString()};
                        intent.putExtra(Intent.EXTRA_EMAIL, to);
                        intent.putExtra(Intent.EXTRA_TEXT, "Die Excel Tabelle mit den " +
                                "Geburtstagen.");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Geburtstage");
                        //needs to be done with FileProvider, see Manifest
                        intent.putExtra(Intent.EXTRA_STREAM, FileProvider.
                                getUriForFile(activity, activity.getApplicationContext().
                                        getPackageName() + ".provider", file));
                        try {
                            activity.startActivity(intent);
                        } catch (android.content.ActivityNotFoundException e) {
                            ShortToast.makeToast(activity, "Kein Email client vorhanden.");
                        }
                    }
                }).setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void checkPermission(Activity activity) {
        if (!(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }
}
