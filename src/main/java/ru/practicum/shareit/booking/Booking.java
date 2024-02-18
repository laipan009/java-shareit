package ru.practicum.shareit.booking;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * TODO Sprint add-bookings.
 */

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_date")
    private LocalDateTime start;

    @Column(name = "end_date")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus bookingStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", item=" + item +
                ", booker=" + booker +
                ", bookingStatus=" + bookingStatus +
                '}';
    }
}