package com.suuka.cleaning.repository;

import com.suuka.cleaning.bookings.entity.Booking;
import com.suuka.cleaning.bookings.repository.BookingRepository;
import com.suuka.cleaning.common.enums.BookingStatus;
import com.suuka.cleaning.common.enums.Role;
import com.suuka.cleaning.users.entity.User;
import com.suuka.cleaning.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryPersistenceTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void usersCanBeSavedAndRetrieved() {
        User user = new User();
        user.setFullName("Persistent Client");
        user.setEmail("persistent.client@suukaclean.local");
        user.setPasswordHash("hashed-password");
        user.setRole(Role.CLIENT);
        user.setBranch("HQ");
        user.setZone("Central");
        user.setAccountVerified(true);

        userRepository.saveAndFlush(user);

        assertThat(userRepository.findByEmailIgnoreCase("PERSISTENT.CLIENT@SUUKACLEAN.LOCAL"))
                .isPresent()
                .get()
                .extracting(User::getFullName)
                .isEqualTo("Persistent Client");
    }

    @Test
    void bookingDashboardCountsComeFromRepositoryRows() {
        User client = new User();
        client.setFullName("Dashboard Client");
        client.setEmail("dashboard.client@suukaclean.local");
        client.setPasswordHash("hashed-password");
        client.setRole(Role.CLIENT);
        client.setAccountVerified(true);
        client = userRepository.saveAndFlush(client);

        Booking booking = new Booking();
        booking.setClientId(client.getId());
        booking.setServiceType("Home Cleaning");
        booking.setPropertyAddress("Kampala Road");
        booking.setDurationHours(3);
        booking.setBookingStatus(BookingStatus.PENDING);

        bookingRepository.saveAndFlush(booking);

        assertThat(bookingRepository.countByClientId(client.getId())).isEqualTo(1);
        assertThat(bookingRepository.countByClientIdAndBookingStatus(client.getId(), BookingStatus.PENDING)).isEqualTo(1);
    }
}
