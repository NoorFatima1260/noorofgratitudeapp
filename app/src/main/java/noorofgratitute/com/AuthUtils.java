package noorofgratitute.com;

import com.google.firebase.auth.FirebaseAuth;

public class AuthUtils {
    public static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}