package flc.model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private static int counter = 1;
    private String memberId;
    private String name;
    private String email;
    private List<Booking> bookings;

    public Member(String name, String email) {
        this.memberId = "M" + (counter++);
        this.name     = name;
        this.email    = email;
        this.bookings = new ArrayList<>();
    }

    // ── Booking ────────────────────────────────────────────────────────────
    /**
     * Books a lesson for the given week.
     * Returns the Booking on success, null if the lesson is full,
     * already booked, or has a time conflict.
     *///
    public Booking makeBooking(Lesson lesson, int week) {
        if (lesson.isFull()) return null;
        if (lesson.hasMember(this)) return null;
        if (hasTimeConflict(lesson, week)) return null;

        Booking b = new Booking(this, lesson, week);
        bookings.add(b);
        lesson.addMember(this);
        return b;
    }

    /**
     * Changes an existing booking to a new lesson (same week).
     * Fails if newLesson is full or there is a time conflict with another booking.
     */
    public boolean changeBooking(Booking booking, Lesson newLesson) {
        if (!bookings.contains(booking)) return false;
        if (newLesson.isFull()) return false;
        if (newLesson.hasMember(this)) return false;

        // Check conflict ignoring the old booking
        for (Booking b : bookings) {
            if (b == booking) continue;
            if (b.getWeek() == booking.getWeek()
                    && b.getLesson().getDay() == newLesson.getDay()
                    && b.getLesson().getTimeSlot() == newLesson.getTimeSlot()) {
                return false;
            }
        }

        booking.getLesson().removeMember(this);
        newLesson.addMember(this);

        // Create a replacement booking object
        Booking newBooking = new Booking(this, newLesson, booking.getWeek());
        bookings.remove(booking);
        bookings.add(newBooking);
        return true;
    }

    /**
     * Cancels a booking and removes the member from the lesson.
     */
    public boolean cancelBooking(Booking booking) {
        if (!bookings.remove(booking)) return false;
        booking.getLesson().removeMember(this);
        return true;
    }

    // ── Review ─────────────────────────────────────────────────────────────
    /**
     * Adds a review for a lesson the member has attended.
     * Returns the Review, or null if the member was not booked for that lesson.
     */
    public Review addReview(Lesson lesson, int rating, String comment) {
        boolean attended = bookings.stream()
                .anyMatch(b -> b.getLesson().equals(lesson));
        if (!attended) return null;

        Review review = new Review(this, lesson, rating, comment);
        lesson.addReview(review);
        return review;
    }

    // ── Helpers ────────────────────────────────────────────────────────────
    private boolean hasTimeConflict(Lesson newLesson, int week) {
        for (Booking b : bookings) {
            if (b.getWeek() == week
                    && b.getLesson().getDay() == newLesson.getDay()
                    && b.getLesson().getTimeSlot() == newLesson.getTimeSlot()) {
                return true;
            }
        }
        return false;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String        getMemberId() { return memberId; }
    public String        getName()     { return name; }
    public String        getEmail()    { return email; }
    public List<Booking> getBookings() { return new ArrayList<>(bookings); }

    @Override
    public String toString() {
        return String.format("Member[%s] %s (%s)", memberId, name, email);
    }
}
