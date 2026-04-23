package flc;

import flc.data.DataLoader;
import flc.model.*;
import flc.report.ReportGenerator;
import flc.service.Timetable;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class FLCSystemTest {

    // Fresh instances per test class to avoid state bleed ////
    private ExerciseType yoga;
    private ExerciseType zumba;
    private ExerciseType boxFit;
    private Member alice;
    private Member bob;
    private Member carla;
    private Member dave;
    private Member emma;
    private Lesson yogaSatMorn;
    private Lesson zumbaSatAfter;
    private Lesson zumbaSatMorn;
    private Timetable timetable;
    private ReportGenerator reportGen;

    @BeforeEach
    void setUp() {
        yoga    = new ExerciseType("Yoga",    12.00);
        zumba   = new ExerciseType("Zumba",   10.00);
        boxFit  = new ExerciseType("Box Fit", 15.00);

        alice = new Member("Alice", "alice@test.com");
        bob   = new Member("Bob",   "bob@test.com");
        carla = new Member("Carla", "carla@test.com");
        dave  = new Member("Dave",  "dave@test.com");
        emma  = new Member("Emma",  "emma@test.com");

        yogaSatMorn   = new Lesson(yoga,  Day.SATURDAY, TimeSlot.MORNING,   1);
        zumbaSatAfter = new Lesson(zumba, Day.SATURDAY, TimeSlot.AFTERNOON, 1);
        zumbaSatMorn  = new Lesson(zumba, Day.SATURDAY, TimeSlot.MORNING,   1);

        timetable = new Timetable();
        timetable.addLesson(yogaSatMorn);
        timetable.addLesson(zumbaSatAfter);

        reportGen = new ReportGenerator(timetable);
    }

    // ── Booking tests ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Member can book an available lesson")
    void testMakeBookingSuccess() {
        Booking b = alice.makeBooking(yogaSatMorn, 1);
        assertNotNull(b, "Booking should succeed for an available lesson");
        assertEquals(1, yogaSatMorn.getMemberCount());
        assertEquals(1, alice.getBookings().size());
    }

    @Test
    @DisplayName("Booking fails when lesson is full (capacity = 4)")
    void testBookingFailsWhenFull() {
        alice.makeBooking(yogaSatMorn, 1);
        bob.makeBooking(yogaSatMorn,   1);
        carla.makeBooking(yogaSatMorn, 1);
        dave.makeBooking(yogaSatMorn,  1);

        assertTrue(yogaSatMorn.isFull(), "Lesson should be full after 4 bookings");

        Booking extra = emma.makeBooking(yogaSatMorn, 1);
        assertNull(extra, "5th booking should be rejected");
        assertEquals(4, yogaSatMorn.getMemberCount());
    }

    @Test
    @DisplayName("Duplicate booking by same member is rejected")
    void testDuplicateBookingRejected() {
        alice.makeBooking(yogaSatMorn, 1);
        Booking dup = alice.makeBooking(yogaSatMorn, 1);
        assertNull(dup, "Duplicate booking should return null");
        assertEquals(1, yogaSatMorn.getMemberCount());
    }

    @Test
    @DisplayName("Time conflict prevents double-booking same slot")
    void testTimeConflictPreventsBooking() {
        alice.makeBooking(yogaSatMorn, 1);
        // zumbaSatMorn is also Saturday Morning week 1 → conflict
        Booking conflict = alice.makeBooking(zumbaSatMorn, 1);
        assertNull(conflict, "Booking should fail due to time conflict");
    }

    @Test
    @DisplayName("Member can book multiple non-conflicting lessons")
    void testBookMultipleNonConflictingLessons() {
        Booking b1 = alice.makeBooking(yogaSatMorn,   1);
        Booking b2 = alice.makeBooking(zumbaSatAfter, 1);
        assertNotNull(b1);
        assertNotNull(b2);
        assertEquals(2, alice.getBookings().size());
    }

    // ── Change booking tests ──────────────────────────────────────────────

    @Test
    @DisplayName("Member can change booking to available lesson")
    void testChangeBookingSuccess() {
        Booking oldBooking = alice.makeBooking(yogaSatMorn, 1);
        assertNotNull(oldBooking);

        boolean changed = alice.changeBooking(oldBooking, zumbaSatAfter);
        assertTrue(changed, "Change booking should succeed");

        assertEquals(0, yogaSatMorn.getMemberCount(), "Old lesson should have 0 members");
        assertEquals(1, zumbaSatAfter.getMemberCount(), "New lesson should have 1 member");
    }

    @Test
    @DisplayName("Change booking fails when new lesson is full")
    void testChangeBookingFailsWhenFull() {
        alice.makeBooking(yogaSatMorn,   1);
        bob.makeBooking(zumbaSatAfter,   1);
        carla.makeBooking(zumbaSatAfter, 1);
        dave.makeBooking(zumbaSatAfter,  1);
        emma.makeBooking(zumbaSatAfter,  1);

        Booking aliceBooking = alice.getBookings().get(0);
        boolean changed = alice.changeBooking(aliceBooking, zumbaSatAfter);
        assertFalse(changed, "Should not change booking to a full lesson");
    }

    // ── Review tests ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Member can leave a review after attending a lesson")
    void testAddReviewSuccess() {
        alice.makeBooking(yogaSatMorn, 1);
        Review r = alice.addReview(yogaSatMorn, 5, "Excellent!");
        assertNotNull(r);
        assertEquals(5, r.getRating());
        assertEquals(1, yogaSatMorn.getReviews().size());
    }

    @Test
    @DisplayName("Review is rejected if member did not attend the lesson")
    void testReviewRejectedIfNotAttended() {
        Review r = alice.addReview(yogaSatMorn, 4, "Sneaky review");
        assertNull(r, "Review should be null if member did not book the lesson");
    }

    @Test
    @DisplayName("Invalid rating throws exception")
    void testInvalidRatingThrows() {
        alice.makeBooking(yogaSatMorn, 1);
        assertThrows(IllegalArgumentException.class,
                () -> alice.addReview(yogaSatMorn, 6, "Out of range"),
                "Rating > 5 should throw");
        assertThrows(IllegalArgumentException.class,
                () -> alice.addReview(yogaSatMorn, 0, "Out of range"),
                "Rating < 1 should throw");
    }

    @Test
    @DisplayName("Average rating is calculated correctly")
    void testAverageRating() {
        alice.makeBooking(yogaSatMorn, 1);
        bob.makeBooking(yogaSatMorn, 1);
        alice.addReview(yogaSatMorn, 4, "Good");
        bob.addReview(yogaSatMorn, 2, "Not great");
        assertEquals(3.0, yogaSatMorn.getAverageRating(), 0.001);
    }

    @Test
    @DisplayName("Average rating returns 0 when no reviews exist")
    void testAverageRatingNoReviews() {
        assertEquals(0.0, yogaSatMorn.getAverageRating(), 0.001);
    }

    // ── Timetable lookup tests ────────────────────────────────────────────

    @Test
    @DisplayName("Timetable returns correct lessons by day")
    void testGetLessonsByDay() {
        List<Lesson> satLessons = timetable.getLessonsByDay(Day.SATURDAY);
        assertEquals(2, satLessons.size(), "Both lessons are on Saturday");
    }

    @Test
    @DisplayName("Timetable returns correct lessons by exercise name")
    void testGetLessonsByExerciseName() {
        List<Lesson> yogaLessons = timetable.getLessonsByExerciseName("Yoga");
        assertEquals(1, yogaLessons.size());

        List<Lesson> zumbaLessons = timetable.getLessonsByExerciseName("Zumba");
        assertEquals(1, zumbaLessons.size());
    }

    @Test
    @DisplayName("Timetable returns only available (non-full) lessons")
    void testGetAvailableLessons() {
        alice.makeBooking(yogaSatMorn, 1);
        bob.makeBooking(yogaSatMorn,   1);
        carla.makeBooking(yogaSatMorn, 1);
        dave.makeBooking(yogaSatMorn,  1);

        List<Lesson> available = timetable.getAvailableLessons();
        assertFalse(available.contains(yogaSatMorn), "Full lesson should not appear");
        assertTrue(available.contains(zumbaSatAfter), "Non-full lesson should appear");
    }

    // ── Report tests ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Report: income is price × member count")
    void testCalcIncome() {
        alice.makeBooking(yogaSatMorn, 1);
        bob.makeBooking(yogaSatMorn,   1);
        double income = reportGen.calcIncome(yogaSatMorn);
        assertEquals(24.00, income, 0.001, "2 members × £12 = £24");
    }

    @Test
    @DisplayName("Report: member count matches bookings")
    void testGetMemberCount() {
        alice.makeBooking(yogaSatMorn, 1);
        assertEquals(1, reportGen.getMemberCount(yogaSatMorn));
    }

    @Test
    @DisplayName("Full data load: 48 lessons are created")
    void testDataLoaderCreates48Lessons() {
        Timetable t = DataLoader.loadTimetable();
        assertEquals(48, t.getAllLessons().size(), "8 weeks × 2 days × 3 slots = 48 lessons");
    }

    @Test
    @DisplayName("Full data load: reviews are assigned to lessons")
    void testDataLoaderReviewsPresent() {
        Timetable t = DataLoader.loadTimetable();
        long lessonsWithReviews = t.getAllLessons().stream()
                .filter(l -> !l.getReviews().isEmpty())
                .count();
        assertTrue(lessonsWithReviews >= 5, "At least 5 lessons should have reviews");
    }
}
