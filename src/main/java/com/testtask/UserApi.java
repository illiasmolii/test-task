package com.testtask;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Representing API of the external service for working with users accounts.
 *
 * You should not care about actual implementation of the interface,
 * it will be provided by the Spring Framework.
 */
public interface UserApi {
	/**
	 * Retrieves all the users.
	 */
	Collection<User> getAllUsers();
	/**
	 * Retrieves all the users by {@link User#getId()}
	 */
	Collection<User> getByIdIn(List<Long> userIds);
	/**
	 * Retrieves user by name.
	 */
	Optional<User> findByName(String name);
}
