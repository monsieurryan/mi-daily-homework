package com.example.addcontactsdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private FloatingActionButton refreshButton;
    private ContentObserver contactsObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        refreshButton = findViewById(R.id.refreshButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter();
        recyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(v -> {
            loadContacts();
            Toast.makeText(this, "联系人列表已刷新", Toast.LENGTH_SHORT).show();
        });

        // 创建联系人数据变化观察者
        contactsObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                loadContacts();
            }
        };

        checkPermissionAndLoadContacts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerContactsObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterContactsObserver();
    }

    private void registerContactsObserver() {
        ContentResolver contentResolver = getContentResolver();
        // 监听联系人数据变化
        contentResolver.registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI,
                true,
                contactsObserver
        );
        // 监听联系人电话号码变化
        contentResolver.registerContentObserver(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                true,
                contactsObserver
        );
        // 监听联系人数据变化（包括删除）
        contentResolver.registerContentObserver(
                ContactsContract.RawContacts.CONTENT_URI,
                true,
                contactsObserver
        );
    }

    private void unregisterContactsObserver() {
        getContentResolver().unregisterContentObserver(contactsObserver);
    }

    private void checkPermissionAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts();
                registerContactsObserver();
            } else {
                Toast.makeText(this, "需要联系人权限才能显示联系人列表", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            while (cursor.moveToNext()) {
                String name = nameColumnIndex >= 0 ? cursor.getString(nameColumnIndex) : "";
                String phoneNumber = phoneColumnIndex >= 0 ? cursor.getString(phoneColumnIndex) : "";
                
                if (!name.isEmpty() || !phoneNumber.isEmpty()) {
                    contacts.add(new Contact(name, phoneNumber));
                }
            }
            cursor.close();
        }

        adapter.setContacts(contacts);
    }
}