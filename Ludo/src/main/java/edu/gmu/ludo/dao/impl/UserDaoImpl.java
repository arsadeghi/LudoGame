package edu.gmu.ludo.dao.impl;

import org.springframework.stereotype.Repository;

import edu.gmu.ludo.dao.UserDao;
import edu.gmu.ludo.entity.User;

@Repository
public class UserDaoImpl extends AbstractDaoImpl<User, String> implements UserDao {

	protected UserDaoImpl() {
		super(User.class);
	}

	@Override
	public User findByUsername(String username) {
		String hql = "FROM User WHERE username = :username";
		return (User) getCurrentSession().createQuery(hql).setString("username", username).uniqueResult();
	}
}
