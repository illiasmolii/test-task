package com.testtask;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service where you should implement your business methods.
 */
@Service
public class UserTransfersService {

    private final UserApi userApi;
    private final TransferApi transferApi;

    @Autowired
    public UserTransfersService(UserApi userApi, TransferApi transferApi) {
        this.userApi = userApi;
        this.transferApi = transferApi;
    }

    /**
     * EXAMPLE
     * <p>
     * Find all the users that are beneficiary in transactions
     * with amount more than or equal to first param
     * and were requested prior to time provided as a second parameter
     */
    public Collection<User> getUsersByBusinessCriteria(BigDecimal minAmount, ZonedDateTime beforeTime) {
        Set<Transfer> allTransfers = transferApi.getAllTransfers();
        List<Long> beneficiaries = allTransfers.stream()
                .filter(transfer -> transfer.getAmount().compareTo(minAmount) >= 0)
                .filter(transfer -> transfer.getRequestTime().isBefore(beforeTime))
                .map(Transfer::getBeneficiaryUserId)
                .collect(Collectors.toList());
        return userApi.getByIdIn(beneficiaries);
    }

    /**
     * TODO 1. Find a successful transfer with the largest amount
     * // Return null if there are no successful transfers
     */
    public Transfer findLargestSuccessfulTransfer() {
        return transferApi.getAllTransfers().stream()
                .filter(transfer -> transfer.getStatus() == TransferStatus.SUCCESSFUL)
                .max(Comparator.comparing(Transfer::getAmount))
                .orElse(null);

    }

    /**
     * TODO 2. Find all the rejected transfers performed by user which name is provided as a parameter
     * // in case of no such user, return empty collection
     */
    public Collection<Transfer> getRejectedForUser(String userName) {
        return userApi.findByName(userName).map(value -> transferApi.getByIdIn(value.getTransfersIds()).stream()
                .filter(transfer -> transfer.getStatus() == TransferStatus.REJECTED)
                .collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    /**
     * TODO 3. Calculate the total sum of transfers for user which name provided as a param
     * // where he/she was a sender for the last year.
     * // In case of no such user, throw {@link NoSuchUserException}
     * // with the message "User with ${userName} does not exist".
     * // Put userName parameter instead of ${userName} in the message.
     */
    public BigDecimal getSentLastYear(String userName) {
        User user = userApi.findByName(userName).orElseThrow(() -> new NoSuchUserException("User with " + userName + " does not exist"));
        return transferApi.getByIdIn(user.getTransfersIds()).stream()
                .filter(transfer -> transfer.getRequestTime().isAfter(ZonedDateTime.now().minusYears(1)))
                .map(Transfer::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

