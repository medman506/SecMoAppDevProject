package ims.fhj.at.emergencyalerter.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ims.fhj.at.emergencyalerter.model.Contact;


public class DatabaseUtil extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "EmergencyAlerterDB";
    //Table Name
    private static final String CONTACTS_TABLE = "EmergencyContacts";
    //Column Name
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TEL = "age";

    private static DatabaseUtil mInstance = null;

    public static DatabaseUtil getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseUtil(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseUtil(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + CONTACTS_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_TEL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE);
        onCreate(db);
    }

    //Insert Value
    public void adddata(String name,String tel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TEL, tel);
        db.insert(CONTACTS_TABLE, null, values);
        db.close();
    }

    //Get Row Count
    public int getCount() {
        String countQuery = "SELECT  * FROM " + CONTACTS_TABLE;
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    //Delete Query
    public void removeContact(int id) {
        Log.i("DB","Remove");
        String countQuery = "DELETE FROM " + CONTACTS_TABLE + " where " + KEY_ID + "= " + id ;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(countQuery);
    }

    //Get contacct list
    public ArrayList<Contact> getContacts(){
        String selectQuery = "SELECT  * FROM " + CONTACTS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTelephoneNumber(cursor.getString(2));
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        return contacts;
    }
}
