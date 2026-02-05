package be.tobiridi.passwordsecurity.data.repositories;


import be.tobiridi.passwordsecurity.data.datasources.local.AuthenticationLocalDataSource;

/**
 * Centralize for the ui layer how manipulate the authentication of the user to access at the app.
 */
public class AuthenticationRepository {
    private static AuthenticationLocalDataSource authDataSource;

    public AuthenticationRepository(AuthenticationLocalDataSource dataSource) {
        if(authDataSource == null) {
            authDataSource = dataSource;
        }
    }

    public byte getMaxAuthAttempts() {
        return authDataSource.getMaxAuthAttempts();
    }

    /**
     * This method will clear all data used for the application like a <em>reset</em>.
     * <br/>
     * <strong>Please be careful when you use this method !</strong>
     */
    public void destroyAllData() {
        authDataSource.clearAllData();
    }
}
