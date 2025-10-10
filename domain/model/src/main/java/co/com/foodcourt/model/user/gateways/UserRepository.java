package co.com.foodcourt.model.user.gateways;

import co.com.foodcourt.model.user.User;

public interface UserRepository {
    User getUserById (Long userID);
}
