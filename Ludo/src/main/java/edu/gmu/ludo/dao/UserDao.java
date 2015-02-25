package edu.gmu.ludo.dao;

import edu.gmu.ludo.entity.User;

public interface UserDao extends AbstractDao<User, String> {

	User findByUsername(String username);
}
