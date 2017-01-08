package ims.fhj.at.emergencyalerter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ims.fhj.at.emergencyalerter.R;
import ims.fhj.at.emergencyalerter.adapter.ContactViewAdapter;
import ims.fhj.at.emergencyalerter.model.Contact;
import ims.fhj.at.emergencyalerter.util.App;
import ims.fhj.at.emergencyalerter.util.DatabaseUtil;

public class SettingsActivity extends AppCompatActivity {

    private Button btnContact;
    private Button btnNumber;
    private EditText tvNumber;

    private static final int RESULT_PICK_CONTACT = 1001;
    //Custom list for displaying tasks
    private ListView list;
    private ArrayList<Contact> contactList;
    private ContactViewAdapter adapter;

    private SharedPreferences pref;
    private DatabaseUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // get list from layout
        list = (ListView) findViewById(R.id.contacts_list_view);
        setupListViewListener();

        // Getting Tasks  from DB
        dbUtil = DatabaseUtil.getInstance(getApplicationContext());
        contactList = dbUtil.getContacts();
        // Add Tasks to Listview
        adapter = new ContactViewAdapter(this, contactList);
        list.setAdapter(adapter);

        btnContact = (Button) findViewById(R.id.addContact_btn);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact(v);
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor edit = pref.edit();

        tvNumber = (EditText) findViewById(R.id.emergencyNumber_input);
        tvNumber.setFocusableInTouchMode(false);
        tvNumber.setFocusable(false);
        tvNumber.setFocusableInTouchMode(true);
        tvNumber.setFocusable(true);
        tvNumber.setText(pref.getString(App.SETTING_EMGERGENCY_NUMBER,App.EMERGENCY_PHONE_NUMBER_DEFAULT));

        btnNumber = (Button) findViewById(R.id.setNumber_btn);
        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.putString(App.SETTING_EMGERGENCY_NUMBER, tvNumber.getText().toString());
                edit.commit();

                // show toast that number has been saved
                Toast.makeText(getBaseContext(), "Emergency number was saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupListViewListener() {

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("SETTINGS","LOOOONG Click");
                Contact toRemove = adapter.getItem(position);
                dbUtil.removeContact(toRemove.getId());
                adapter.remove(toRemove);
                adapter.notifyDataSetChanged();

                return true;
            }
        });

    }


    public void pickContact(View v) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("Settings", "Failed to pick contact");
        }
    }

    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     *
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // save to db and update list
            dbUtil.adddata(name,phoneNo);
            refreshList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void refreshList(){
        contactList = dbUtil.getContacts();
        adapter.clear();
        adapter.addAll(contactList);
        adapter.notifyDataSetChanged();
    }

}
