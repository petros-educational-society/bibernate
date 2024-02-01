package com.petros.bibernate.session.impl;

import com.petros.bibernate.datasource.DataSourceImpl;
import com.petros.bibernate.entity.*;
import com.petros.bibernate.session.Session;
import lombok.RequiredArgsConstructor;
import org.h2.jdbc.JdbcSQLNonTransientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor
class SessionImplTest {

    private static final String DB_URL = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1;INIT=RUNSCRIPT FROM 'classpath:data/BB-02_Create_test_tables.sql';";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa";
    private Session session;

    @BeforeAll
    public void init() {
        try (DataSourceImpl dataSource = new DataSourceImpl(DB_URL, DB_USER, DB_PASSWORD)) {
            session = new SessionImpl(dataSource);
        }
    }

    @Test
    void findExistingUser() {
        User user = session.find(User.class, 1);
        assertEquals("Ken", user.getName());
        assertEquals("ken@gmail.com", user.getEmail());
    }

    @Test
    void findExistingAddress() {
        Address address = session.find(Address.class, 11);
        assertEquals("Odessa", address.getCity());
        assertEquals("R.Luksemburg", address.getStreet());
    }

    @Test
    void findNonExistingUser() {
        Exception exception = assertThrows(JdbcSQLNonTransientException.class,
                () -> session.find(User.class, 1000));

        assertEquals("No data is available [2000-224]", exception.getMessage());
    }

    @Test
    void findNonExistingAddress() {
        Exception exception = assertThrows(JdbcSQLNonTransientException.class,
                () -> session.find(Address.class, 1000));

        assertEquals("No data is available [2000-224]", exception.getMessage());
    }

    @Test
    void getOrderAddress() {
        Order order = session.find(Order.class, 1);
        Address address = order.getAddress();
        assertEquals(10L, address.getId());
        assertEquals("Riga", address.getCity());
        assertEquals("Rebenstrasse", address.getStreet());
    }

    @Test
    void getAddressOrders() {
        Address address = session.find(Address.class, 10L);
        List<Order> orders = address.getOrders();
        assertEquals(3, orders.size());
    }

    @Test
    void getUserPaymentsWithoutRelatedFieldInPaymentEntity() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> session.find(UserWithPayments.class, 1).getPayments());
        assertEquals("Can't find related field in class com.petros.bibernate.entity.Payment for class com.petros.bibernate.entity.UserWithPayments",
                exception.getMessage());
    }

    @Test
    void getPaymentsCardUser() {
        PaymentCard paymentCard = session.find(PaymentCard.class, 1);
        User user = paymentCard.getUser();
        assertEquals(1L, user.getId());
        assertEquals("Ken", user.getName());
        assertEquals("ken@gmail.com", user.getEmail());
    }

    @Test
    void getBuyersUsers() {
        User user = session.find(User.class, 2);
        Set<Buyer> buyers = user.getBuyers();
        assertEquals(1, buyers.size());
        assertTrue(buyers.stream().anyMatch(b -> b.getName().equals("Alex") && b.getPhone().equals("380665624786")));
    }

    @Test
    void getUserBuyers() {
        Buyer buyer = session.find(Buyer.class, 1);
        Set<User> users = buyer.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    void createOrderWithAddress() {
        Address address = new Address(13L, "Kiev", "Shevchenka");
        Order order = new Order(4L, "Paper", BigDecimal.valueOf(1.04), address);
        address.addOrder(order);
        session.insert(order);
        session.close();
        Order createdOrder = session.find(Order.class, 4L);

        assertEquals(4L, createdOrder.getId());
        assertEquals("Paper", createdOrder.getName());
        assertEquals(BigDecimal.valueOf(1.04), createdOrder.getPrice());

        assertEquals(13L, createdOrder.getAddress().getId());

        Address createdAddress = session.find(Address.class, 13L);
        assertEquals(address.getCity(), createdAddress.getCity());
        assertEquals(address.getStreet(), createdAddress.getStreet());
    }

    @Test
    void createOrderWithoutAddress() {
        Order order = new Order(5L, "Paper", BigDecimal.valueOf(1.05), null);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {
                    session.insert(order);
                    session.close();
                });
        assertEquals("class com.petros.bibernate.entity.Address is mandatory field for entity class com.petros.bibernate.entity.Order",
                exception.getMessage());
    }
}