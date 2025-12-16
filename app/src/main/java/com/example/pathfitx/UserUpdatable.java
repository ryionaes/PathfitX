package com.example.pathfitx;

import com.google.firebase.firestore.DocumentSnapshot;

public interface UserUpdatable {
    void onUserUpdate(DocumentSnapshot userSnapshot);
}
