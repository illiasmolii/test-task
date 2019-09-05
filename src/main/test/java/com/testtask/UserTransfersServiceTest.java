package com.testtask;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserTransfersServiceTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Mock
    private TransferApi transferApi;

    @Mock
    private UserApi userApi;

    @InjectMocks
    private UserTransfersService userTransfersService;

    @Test
    public void shouldReturnUsersByBusinessCriteria() { //TestForExample
        long userId = 3L;
        when(transferApi.getAllTransfers()).thenReturn(Collections.singleton(new Transfer(1L,
                2L, userId,
                TransferStatus.SUCCESSFUL,
                BigDecimal.ONE,
                LocalDateTime.now().minusYears(1).atZone(ZoneId.systemDefault()))));

        userTransfersService.getUsersByBusinessCriteria(BigDecimal.ZERO, LocalDateTime.now().atZone(ZoneId.systemDefault()));

        verify(userApi, times(1)).getByIdIn(eq(Collections.singletonList(userId)));

    }

    @Test
    public void shouldFindLargestSuccessfulTransfer() {
        Transfer largestSuccessfulTransfer = new Transfer(3L, 33L, 3L, TransferStatus.SUCCESSFUL, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(3));
        when(transferApi.getAllTransfers()).thenReturn(new LinkedHashSet<>(Arrays.asList(
                new Transfer(1L, 11L, 1L, TransferStatus.SUCCESSFUL, BigDecimal.valueOf(-100.0), ZonedDateTime.now().minusWeeks(1)),
                new Transfer(2L, 22L, 2L, TransferStatus.REJECTED, BigDecimal.valueOf(200.0), ZonedDateTime.now().minusWeeks(2)),
                largestSuccessfulTransfer,
                new Transfer(4L, 44L, 4L, TransferStatus.PENDING, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(4)))));
        assertEquals(largestSuccessfulTransfer,
                userTransfersService.findLargestSuccessfulTransfer());
    }

    @Test
    public void shouldNotFindLargestSuccessfulTransfer() {
        when(transferApi.getAllTransfers()).thenReturn(new LinkedHashSet<>(Arrays.asList(
                new Transfer(1L, 11L, 1L, TransferStatus.REJECTED, BigDecimal.valueOf(-100.0), ZonedDateTime.now().minusWeeks(1)),
                new Transfer(2L, 22L, 2L, TransferStatus.REJECTED, BigDecimal.valueOf(200.0), ZonedDateTime.now().minusWeeks(2)),
                new Transfer(3L, 33L, 3L, TransferStatus.REJECTED, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(3)),
                new Transfer(4L, 44L, 4L, TransferStatus.PENDING, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(4)))));
        assertNull(userTransfersService.findLargestSuccessfulTransfer());
    }

    @Test
    public void shouldGetRejectedForUser() {
        String userName = "NameX";
        Long userId = 2L;
        List<Long> transfersIds = Arrays.asList(1L, 2L, 3L, 4L);
        Transfer firstRejectedTransfer = new Transfer(1L, userId, 1L, TransferStatus.REJECTED, BigDecimal.valueOf(300.0), ZonedDateTime.now().minusWeeks(1));
        Transfer secondRejectedTransfer = new Transfer(4L, 44L, userId, TransferStatus.REJECTED, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(4));
        when(userApi.findByName(eq(userName))).thenReturn(Optional.of(new User(userId, userName, transfersIds)));
        when(transferApi.getByIdIn(eq(transfersIds))).thenReturn(new LinkedHashSet<>(Arrays.asList(firstRejectedTransfer,
                new Transfer(2L, 22L, userId, TransferStatus.PENDING, BigDecimal.valueOf(200.0), ZonedDateTime.now().minusWeeks(2)),
                new Transfer(3L, userId, 3L, TransferStatus.SUCCESSFUL, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(3)),
                secondRejectedTransfer)));
        assertEquals(Arrays.asList(firstRejectedTransfer, secondRejectedTransfer), userTransfersService.getRejectedForUser(userName));
    }

    @Test
    public void shouldNotGetRejectedForUser() {
        String userName = "NameX";
        when(userApi.findByName(userName)).thenReturn(Optional.empty());
        assertEquals(Collections.emptyList(), userTransfersService.getRejectedForUser(userName));
    }

    @Test
    public void shouldGetSentLastYear() {
        String userName = "NameX";
        Long userId = 2L;
        BigDecimal expectedSum = BigDecimal.valueOf(250.0);
        List<Long> transfersIds = Arrays.asList(1L, 2L, 3L, 4L);
        when(userApi.findByName(userName)).thenReturn(Optional.of(new User(userId, userName, transfersIds)));
        when(transferApi.getByIdIn(transfersIds)).thenReturn(new LinkedHashSet<>(Arrays.asList(
                new Transfer(1L, userId, 1L, TransferStatus.REJECTED, BigDecimal.valueOf(300.0), ZonedDateTime.now().minusYears(2)),
                new Transfer(2L, 22L, userId, TransferStatus.PENDING, BigDecimal.valueOf(150.0), ZonedDateTime.now().minusWeeks(2)),
                new Transfer(3L, userId, 3L, TransferStatus.SUCCESSFUL, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusWeeks(3)),
                new Transfer(4L, 44L, userId, TransferStatus.REJECTED, BigDecimal.valueOf(100.0), ZonedDateTime.now().minusYears(3)))));
        assertEquals(expectedSum, userTransfersService.getSentLastYear(userName));
    }

    @Test
    public void shouldNotGetSentLastYear() {
        String userName = "NameX";
        when(userApi.findByName(userName)).thenReturn(Optional.empty());
        expectedEx.expect(NoSuchUserException.class);
        expectedEx.expectMessage("User with " + userName + " does not exist");
        userTransfersService.getSentLastYear(userName);
    }
}
