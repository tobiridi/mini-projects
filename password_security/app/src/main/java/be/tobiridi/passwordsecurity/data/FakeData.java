package be.tobiridi.passwordsecurity.data;

import java.util.List;

public final class FakeData {
    private String name;
    private String email;
    private String password;

    public FakeData (String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public static List<FakeData> getFakeData() {
        return List.of(
                new FakeData("Quick", "ginopouki@hotmail.fr", "darkorbit6141&"),
                new FakeData("Hotmail", "ginopouki@hotmail.fr", "darkorbit6141&"),
                new FakeData("Facebook", "ginopouki@hotmail.fr", "darkorbit6141&")
        );
    }

}

