package com.zio.docsguard;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zio.docsguard.DataBase.DocsDAO;
import com.zio.docsguard.DataBase.DocsDB;
import com.zio.docsguard.DataBase.DocsEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_PICK = 250;
    FloatingActionButton fab;
    String Name = "";
    DocsDAO docsDao;
    AdapterFiles adapter;
    List<ModelFiles> list;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if (uri != null) {
                        try {
                            copyFile(uri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        Context context = this;

        TextView tv = findViewById(R.id.titletext);

        DocsDB db = Room.databaseBuilder(context, DocsDB.class, "DOCS").allowMainThreadQueries().build();
        docsDao = db.docsdao();

        List<DocsEntity> docslist = docsDao.getAll();

        RecyclerView rvDocs = (RecyclerView) findViewById(R.id.doclist);
        list = new ArrayList<>();
        String[] files = context.fileList();
        Uri uri = null;
        //for (String file : files) list.add(new ModelFiles(file, uri));
        for (DocsEntity x : docslist) list.add(new ModelFiles(x.getName(), uri));

        adapter = new AdapterFiles(context, list);
        rvDocs.setAdapter(adapter);
        rvDocs.setLayoutManager(new LinearLayoutManager(this));

        PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.aadhar:
                        Name = "Aadhar";
                        break;
                    case R.id.pan:
                        Name = "PAN Card";
                        break;
                    case R.id.mtwelve:
                        Name = "Mark sheet 12th";
                        break;
                    case R.id.mten:
                        Name = "Mark sheet 10th";
                        break;
                    case R.id.drlicence:
                        Name = "Driving Licence";
                        break;
                    case R.id.resume:
                        Name = "My Resume";
                        break;
                    default:
                        Name = "";

                }
                if (Name.isEmpty())
                    Toast.makeText(context, "Something Went Wrong Try later", Toast.LENGTH_SHORT).show();
                else
                    mGetContent.launch("application/*");
                return true;
            }
        };

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                PopupMenu popup = new PopupMenu(context, fab);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.docs_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(listener);
                popup.show();
            }
        });
    }

    private void copyFile(Uri inp) throws IOException {
        if (Name.isEmpty()) {
            Toast.makeText(this, "Something Went Wrong\nPlease try again later", Toast.LENGTH_SHORT).show();
            return;
        }
        InputStream iStream = getContentResolver().openInputStream(inp);
        byte[] inputData = getBytes(iStream);

        try (FileOutputStream fos = this.openFileOutput(Name + ".pdf", Context.MODE_PRIVATE)) {
            fos.write(inputData);
        }

        AddAndUpdate(inp);

    }

    private void AddAndUpdate(Uri inp) {
        DocsEntity entity = new DocsEntity();
        entity.setName(Name);
        entity.setUri(inp.toString());
        docsDao.insertAll(entity);

        list.add(new ModelFiles(Name, null));
        adapter.notifyItemInserted(list.size() - 1);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void openPop() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.popup_details, null, false), 500, 500, true);

        pw.setAnimationStyle(androidx.appcompat.R.style.Animation_AppCompat_Dialog);
        pw.showAtLocation(this.findViewById(R.id.main), Gravity.CENTER, 0, 0);
    }

}