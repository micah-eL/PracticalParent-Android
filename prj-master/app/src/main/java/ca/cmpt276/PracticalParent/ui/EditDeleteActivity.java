package ca.cmpt276.PracticalParent.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.FileOutputStream;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import ca.cmpt276.PracticalParent.R;
import ca.cmpt276.PracticalParent.model.Child;
import ca.cmpt276.PracticalParent.model.ChildManager;
import ca.cmpt276.PracticalParent.model.HistoryManager;
import ca.cmpt276.PracticalParent.model.PrefConfig;
import ca.cmpt276.PracticalParent.model.Task;
import ca.cmpt276.PracticalParent.model.TaskManager;

/**
 * Edit and/or Delete a child from the list view.
 */
public class EditDeleteActivity extends AppCompatActivity {
    private ChildManager children;
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private TextView txtSelectedChildInfo;
    private EditText etxtName;
    private String tempTaskName;
    private Button btnEdit,btnDelete,btnCancel,btnChangePhoto,btnCamera;

    private final String TAG = getClass().getSimpleName();
    private ImageView mPicture;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_PERMISSION_CODE = 267;
    private static final int TAKE_PHOTO = 189;
    private static final int CHOOSE_PHOTO = 385;
    private static final int CHECK_MIN_SDK = 23;
    Child child = null;

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    public static Intent makeLaunchIntent(Context context){
        return (new Intent(context, EditDeleteActivity.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete);
        ActionBar ab= getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        children = ChildManager.getInstance();
        taskManager = TaskManager.getInstance();
        historyManager = HistoryManager.getInstance();

        if (Build.VERSION.SDK_INT >= CHECK_MIN_SDK) {
            if (checkSelfPermission(PERMISSION_WRITE_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION_WRITE_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }

        mPicture=findViewById(R.id.childPhoto);
        txtSelectedChildInfo = findViewById(R.id.txt_selected_child_info);
        etxtName = findViewById(R.id.etxt_name);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnCancel = findViewById(R.id.btn_cancel);
        btnChangePhoto = findViewById(R.id.btnChangePhoto); //Make it similar to previous ones or change the previous ones
        btnCamera=findViewById(R.id.btnChangePhotoCamera);
        Intent intent = getIntent();
        int selectedChildIndex = intent.getIntExtra("Position", 0);

        child = children.get(selectedChildIndex);
        String path=child.get_photo_path();
        // default image
        if (child.child_no_pic()) {
            mPicture.setImageResource(R.drawable.default_image);
        }
        // select a different image
        else{
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            mPicture.setImageBitmap(bitmap);
        }


        String information = children.getInfo(selectedChildIndex);

        // Display the selected child on the screen
        txtSelectedChildInfo.setText(getString(R.string.selected_child_name, information));

        setupButtonEdit();
        setupButtonDelete();
        setupButtonCancel();
        setupButtonChangePhoto();
    }

    private void setupButtonEdit() {
        btnEdit.setOnClickListener((view)->{
            Toast.makeText(this, getString(R.string.edited_toast), Toast.LENGTH_SHORT).show();

            Context context =  getApplicationContext();

            children = ChildManager.getInstance();
            // Get the intent that started us to find the parameter (extra)
            Intent intent = getIntent();
            int selectedChildIndex = intent.getIntExtra("Position",-1);


            String newName = etxtName.getText().toString();

            if(newName.length()>0) {
                children.edit(selectedChildIndex, newName);
            }
            PrefConfig.writeListInPref(getApplicationContext(), children.childList);
            PrefConfig.writeChildOrderListInPref(getApplicationContext(),children.indexList);

            finish();
        });

    }

    private void setupButtonDelete() {
        btnDelete.setOnClickListener((view)->{

            Context context =  getApplicationContext();

            children = ChildManager.getInstance();
            // Get the intent that started us to find the parameter (extra)
            Intent intent = getIntent();
            int selectedChildIndex = intent.getIntExtra("Position",-1);

            //deal with any cases of indexing out of bounds within our child queue in FlipCoinActivity after deleting a child.
            SharedPreferences sharedPref = getSharedPreferences("currChildIndex", Context.MODE_PRIVATE);
            children.removeIndex(selectedChildIndex);
            SharedPreferences.Editor editor = sharedPref.edit();
            if(children.indexList.size()>0){
                int curr_child_index = children.getCurrentIndex(0);
                editor.putInt(FlipCoinActivity.CURR_CHILD_INDEX, curr_child_index);
                editor.commit();
            }


            //deal with any cases of indexing out of bounds within each task's child queue in TaskActivity after deleting a child.
            if (taskManager.getNumTask() != 0){
                int tmpNumTasks = taskManager.getNumTask();
                int indexTask;

                for (indexTask = 0; indexTask < tmpNumTasks; indexTask++){ //perform checks for incorrect indexing for each task
                    Task tmp = taskManager.get(indexTask);
                    tempTaskName = tmp.getCurrentTaskKey();
                    sharedPref = getSharedPreferences("currentChildIndex", Context.MODE_PRIVATE);
                    int indexChildInTask = sharedPref.getInt(tempTaskName, 0);

                    if (indexChildInTask > selectedChildIndex && indexChildInTask != 0){
                        indexChildInTask--;
                    }
                    else if (indexChildInTask == selectedChildIndex && indexChildInTask == children.getNumChildren() - 1){
                        indexChildInTask = 0;
                    }

                    editor = sharedPref.edit();
                    editor.putInt(tempTaskName, indexChildInTask);
                    editor.commit();
                }

            }

            Child selectedChild = children.get(selectedChildIndex);
            String removedChildName = children.getInfo(selectedChildIndex);
            // Add the new child to the childrenList
            children.remove(selectedChild);
            PrefConfig.writeListInPref(getApplicationContext(), children.childList);
            PrefConfig.writeChildOrderListInPref(getApplicationContext(), children.indexList);
            Toast.makeText(context, getString(R.string.selected_child_deleted, removedChildName), Toast.LENGTH_SHORT).show();

            finish();
        });
    }

    private void setupButtonCancel() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context =  getApplicationContext();
                Toast.makeText(context, getString(R.string.cancel_edit),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupButtonChangePhoto(){
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAlbum();
            }
            //
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
    }

    private void openAlbum() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PHOTO);
    }

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePhotoIntent, TAKE_PHOTO);
        }
    }

    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "onRequestPermissionsResult: permission granted");
        } else {
            Log.i(TAG, "onRequestPermissionsResult: permission denied");
            Toast.makeText(this, getString(R.string.photo_access_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        String path=saveBitmap(this,bitmap);
                        displayImage(path);
                }
                break;
            case CHOOSE_PHOTO:
                if (data == null) {
                    return;
                }
                Log.i(TAG, "onActivityResult: ImageUriFromAlbum: " + data.getData());
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mPicture.setImageBitmap(bitmap);
            child.setPhoto_path(imagePath);
        } else {
            Toast.makeText(this, getString(R.string.failed_to_get_image), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}