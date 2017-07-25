package orwir.starter.logic.model;

import java.io.Serializable;


public class User implements Serializable {

    private boolean anonymous;

    public User(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

}
