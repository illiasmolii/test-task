package com.testtask;

import java.util.List;

import lombok.Value;

/**
 * A business entity representing client of the bank.
 */
@Value
public class User {
	/**
	 * Unique identifier of the user.
	 */
	private Long id;
	/**
	 * Name of the user. Considered unique inside the application.
	 */
	private String name;
	/**
	 * All the transactions where user was a sender or beneficiary
	 * Link to {@link Transfer#getId()}
	 */
	private List<Long> transfersIds;
}
