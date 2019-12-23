package com.androiddevs.firebasestoragerecyclerviewjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.grpc.Context;

public class AddImageActivity extends AppCompatActivity {

    ImageView ivImage;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        ivImage = findViewById(R.id.ivImage);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGalleryForImagePicking();
            }
        });
        Button btnUpload = findViewById(R.id.btnUpload);
        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etDescription = findViewById(R.id.etDescription);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String desc = etDescription.getText().toString();
                if (!title.isEmpty() && !desc.isEmpty()) {
                    String mimeType = MimeTypeMap.getSingleton()
                            .getExtensionFromMimeType(
                                    getContentResolver().getType(imageUri));
                    uploadFile(title, desc, mimeType);
                }
            }
        });
    }

    private void uploadFile(final String title, final String desc, String mimeType) {
        final StorageReference ref = FirebaseStorage.getInstance()
                .getReference(title + "." + mimeType);
        ref.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> downloadTask) {
                                    if (downloadTask.isSuccessful()) {
                                        Uri uri = downloadTask.getResult();
                                        uploadImageDataToFirestore(title, desc, uri.toString());
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Something went wrong: "
                                                + downloadTask.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong: "
                                    + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0 && data != null) {
            imageUri = data.getData();
            ivImage.setImageURI(imageUri);
        }
    }

    private void startGalleryForImagePicking() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void uploadImageDataToFirestore(String title, String desc, String url) {
        CollectionReference db = FirebaseFirestore.getInstance().collection("images");
        ImageData imageData = new ImageData(title, desc, url);
        db.add(imageData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),
                            "Successfully uploaded image", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong: " + task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
