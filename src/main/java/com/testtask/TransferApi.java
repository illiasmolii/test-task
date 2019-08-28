package com.testtask;

import java.util.List;
import java.util.Set;

/**
 * Representing API of the external service for working with bank transfers.
 *
 * You should not care about actual implementation of the interface,
 * it will be provided by the Spring Framework.
 */
public interface TransferApi {
	/**
	 * Retrieves all the users.
	 */
	Set<Transfer> getAllTransfers();

	/**
	 * Retrieves transfers by {@link Transfer#getId()}
	 */
	Set<Transfer> getByIdIn(List<Long> transferIds);

	/**
	 * Retrieves the transfers with specific status.
	 */
	Set<Transfer> getByStatus(TransferStatus status);
}
