package mk.edu.ukim.feit.gjorgjim.unitechnet.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mk.edu.ukim.feit.gjorgjim.unitechnet.models.User;

/**
 * Created by gjmarkov on 15.5.2018.
 */

public class DatabaseService {

  private DatabaseReference reference;

  private static final DatabaseService ourInstance = new DatabaseService();

  public static DatabaseService getInstance() {
    return ourInstance;
  }

  private DatabaseService() {
    reference = FirebaseDatabase.getInstance().getReference();
  }

  public DatabaseReference usersReference() {
    return reference.child("users");
  }

  public DatabaseReference userReference(String UID) {
    return reference.child("users").child(UID);
  }
}
