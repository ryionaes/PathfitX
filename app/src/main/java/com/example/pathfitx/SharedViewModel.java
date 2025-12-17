package com.example.pathfitx;

import android.os.Build;
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

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<DocumentSnapshot> userSnapshot = new MutableLiveData<>();
    private final MutableLiveData<QuerySnapshot> historySnapshot = new MutableLiveData<>();
    private final MutableLiveData<DocumentSnapshot> workoutSnapshot = new MutableLiveData<>();

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

    public String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    public void attachListeners() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        // Attach user listener if not already attached
        if (userListener == null) {
            DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId);
            userListener = userDocRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) return;
                if (snapshot != null && snapshot.exists()) {
                    userSnapshot.setValue(snapshot);
                }
            });
        }

        // Attach history listener if not already attached
        if (historyListener == null) {
            Query historyQuery = FirebaseFirestore.getInstance().collection("users").document(userId).collection("history").orderBy("timestamp", Query.Direction.DESCENDING);
            historyListener = historyQuery.addSnapshotListener((snapshots, e) -> {
                if (e != null) return;
                historySnapshot.setValue(snapshots);
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void loadWorkoutForDate(String dateString) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userId = currentUser.getUid();

        // Remove previous listener to avoid multiple listeners on different dates
        if (workoutListener != null) {
            workoutListener.remove();
        }

        DocumentReference workoutDocRef = FirebaseFirestore.getInstance().collection("users").document(userId).collection("workouts").document(dateString);
        workoutListener = workoutDocRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                // On error, post a null value to indicate no data or an issue
                workoutSnapshot.postValue(null);
                return;
            }
            workoutSnapshot.postValue(snapshot);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userListener != null) userListener.remove();
        if (historyListener != null) historyListener.remove();
        if (workoutListener != null) workoutListener.remove();
    }
}
