package com.example.acatch.model;


import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {

    final static String userCollection = "users";
    final static String rideCollection = "rides";



    private ModelFirebase() {}

    //--------------------------------------User--------------------------------------

    public interface GetAllUsersListener { public void onComplete(List<User> users);}

    public static void getAllUsers(Long since, GetAllUsersListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userCollection)
                .whereGreaterThanOrEqualTo(User.LAST_UPDATED,new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<User> list = new LinkedList<User>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(User.create(document.getData()));
                            }
                        } else {}
                        listener.onComplete(list);
                    }
                });
    }

    //Save and signUp:
    public static void saveUser(User user, Model.OnCompleteListener listener) {

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    user.setId(firebaseUser.getUid());
                    save(user,listener);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    listener.onComplete(false);
                }
            });
    }
    public static void save(User user ,Model.OnCompleteListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(userCollection).document(user.getId()).set(user.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Model.instance.setUser(user);
                        listener.onComplete(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(false);
            }
        });
    }

    public static void login(String email, String password, Model.OnCompleteListener listener) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            db.collection(userCollection).document(task.getResult().getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Model.instance.setUser(User.create(documentSnapshot.getData()));
                                }
                            });
                            listener.onComplete(true);
                        }
                        else
                            listener.onComplete(false);
                    }
                });
    }

    public static void isLoggedIn(Model.OnCompleteListener listener) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            db.collection(userCollection).document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Model.instance.setUser(User.create(documentSnapshot.getData()));
                }
            });
            Model.instance.loadingState.setValue(Model.LoadingState.loading);
            listener.onComplete(true);
        }
    }

    public static void signOut(){
        Model.instance.setUser(null);
        FirebaseAuth.getInstance().signOut();
    }

//    public static void getCurrentUser(Model.OnCompleteListener listener)
//    {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = mAuth.getCurrentUser();
//
//        db.collection(userCollection).document(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Model.instance.setUser(User.create(documentSnapshot.getData()),()->listener.onComplete());
//            }
//        });
//    }


    //--------------------------------------Ride--------------------------------------

    public interface GetAllRidesListener { public void onComplete(List<Ride> rides);}

    public static void getAllRides(Long since, GetAllRidesListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(rideCollection)
                .whereGreaterThanOrEqualTo(User.LAST_UPDATED,new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Ride> list = new LinkedList<Ride>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(Ride.create(document.getData()));
                            }
                        } else {}
                        listener.onComplete(list);
                    }
                });
    }

    public static void updateRide(Ride ride ,Model.OnCompleteListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(rideCollection).document(ride.getId()).set(ride.toJson())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onComplete(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(false);
            }
        });
    }

    public static void saveRide(Ride ride ,Model.OnCompleteListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(rideCollection).document(ride.id).set(ride.toJson())
                .addOnSuccessListener(aVoid -> listener.onComplete(true))
                .addOnFailureListener(e -> listener.onComplete(false));
    }



    //--------------------------------SaveImages in storage----------------------------

    public static void uploadImage(Bitmap imageBmp, String name, final Model.UpLoadImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef;
        imagesRef = storage.getReference().child("pictures/").child(name);
        //Convert image:
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // Now we upload the data to firebase storage:
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete("");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Getting from fireBase the image url:
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }
}
