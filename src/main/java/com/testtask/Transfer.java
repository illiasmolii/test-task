package com.testtask;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import lombok.Value;

/**
 * A business entity representing transfer of the money between bank clients.
 */
@Value
public class Transfer {
	/**
	 * Unique identifier of the transfer
	 */
	private Long id;
	/**
	 * A link to {@link User#getId()}
	 */
	private Long senderUserId;
	/**
	 * A link to {@link User#getId()}
	 */
	private Long beneficiaryUserId;
	private TransferStatus status;
	private BigDecimal amount;
	private ZonedDateTime requestTime;
}
