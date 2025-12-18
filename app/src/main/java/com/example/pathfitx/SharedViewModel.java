package com.example.pathfitx;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SharedViewModel extends ViewModel {

    private static final String TAG = "SharedViewModel";

    private final MutableLiveData<DocumentSnapshot> userSnapshot = new MutableLiveData<>();
    private final MutableLiveData<QuerySnapshot> historySnapshot = new MutableLiveData<>();
    private final MutableLiveData<DocumentSnapshot> workoutSnapshot = new MutableLiveData<>();
    private final MutableLiveData<Date> registrationDate = new MutableLiveData<>();

    private ListenerRegistration userListener;
    private ListenerRegistration historyListener;
    private ListenerRegistration workoutListener;

    public LiveData<DocumentSnapshot> getUserSnapshot() {
        return userSnapshot;
    }

    public LiveData<QuerySnapshot> getHistorySnapshot() {
        return historySnapshot;
    }

    public LiveData<DocumentSnapshot> getWorkoutSnapshot() {
        return workoutSnapshot;
    }

    public LiveData<Date> getRegistrationDate() {
        return registrationDate;
    }

    public String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    public void attachListeners() {
        Log.d(TAG, "attachListeners() called.");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Cannot attach listeners, currentUser is null.");
            return;
        }
        String userId = currentUser.getUid();
        Log.d(TAG, "Attaching listeners for user ID: " + userId);

        if (userListener == null) {
            Log.d(TAG, "userListener is null, creating new one.");
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId);
            userListener = userDocRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.e(TAG, "Error fetching user snapshot", e);
                    userSnapshot.setValue(null);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "User snapshot received. Data: " + snapshot.getData());
                    userSnapshot.setValue(snapshot);
                    if (currentUser.getMetadata() != null) {
                        registrationDate.setValue(new Date(currentUser.getMetadata().getCreationTimestamp()));
                    }
                } else {
                    Log.w(TAG, "User snapshot is null or does not exist.");
                    userSnapshot.setValue(null);
                }
            });
        } else {
            Log.d(TAG, "userListener already exists.");
        }

        if (historyListener == null) {
            Log.d(TAG, "historyListener is null, creating new one.");
            Query historyQuery = FirebaseFirestore.getInstance().collection("users").document(userId).collection("history").orderBy("timestamp", Query.Direction.DESCENDING);
            historyListener = historyQuery.addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.e(TAG, "Error fetching history snapshot", e);
                    return;
                }
                Log.d(TAG, "History snapshot received.");
                historySnapshot.setValue(snapshots);
            });
        } else {
             Log.d(TAG, "historyListener already exists.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadWorkoutForDate(String dateString) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        if (workoutListener != null) {
            workoutListener.remove();
        }

        DocumentReference workoutDocRef = FirebaseFirestore.getInstance().collection("users").document(userId).collection("workouts").document(dateString);
        workoutListener = workoutDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                workoutSnapshot.postValue(null);
                return;
            }
            workoutSnapshot.postValue(snapshot);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared() called. Removing listeners.");
        if (userListener != null) userListener.remove();
        if (historyListener != null) historyListener.remove();
        if (workoutListener != null) workoutListener.remove();
    }
}
