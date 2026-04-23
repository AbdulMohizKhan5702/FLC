package flc.data;

import flc.model.*;
import flc.service.Timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Pre-loads 8 weeks × 2 days × 3 slots = 48 lessons across 4 exercise types,
 * 10 members, and 20+ reviews as required by the specification.
 */
public class DataLoader {

    public static Timetable loadTimetable() {
        Timetable timetable = new Timetable();

        // ── Exercise types ────────────────────────────────────────────────
        ExerciseType yoga     = new ExerciseType("Yoga",     12.00);
        ExerciseType zumba    = new ExerciseType("Zumba",    10.00);
        ExerciseType aquacise = new ExerciseType("Aquacise",  9.00);
        ExerciseType boxFit   = new ExerciseType("Box Fit",  15.00);

        ExerciseType[] satTypes = {yoga,  zumba,    aquacise};
        ExerciseType[] sunTypes = {boxFit, yoga,    zumba};

        // ── Generate 8 weeks of lessons (2 days × 3 slots = 6 per week) ──
        TimeSlot[] slots = TimeSlot.values();
        for (int week = 1; week <= 8; week++) {
            for (int s = 0; s < 3; s++) {
                timetable.addLesson(new Lesson(satTypes[s], Day.SATURDAY, slots[s], week));
                timetable.addLesson(new Lesson(sunTypes[s], Day.SUNDAY,   slots[s], week));
            }
        }

        // ── Members ───────────────────────────────────────────────────────
        Member alice   = new Member("Alice",   "alice@email.com");
        Member bob     = new Member("Bob",     "bob@email.com");
        Member carla   = new Member("Carla",   "carla@email.com");
        Member dave    = new Member("Dave",    "dave@email.com");
        Member emma    = new Member("Emma",    "emma@email.com");
        Member felix   = new Member("Felix",   "felix@email.com");
        Member grace   = new Member("Grace",   "grace@email.com");
        Member harry   = new Member("Harry",   "harry@email.com");
        Member isla    = new Member("Isla",    "isla@email.com");
        Member james   = new Member("James",   "james@email.com");

        // ── Bookings: spread members across week 1 lessons ────────────────
        List<Lesson> all = timetable.getAllLessons();

        // Week 1 Saturday Morning = Yoga (index 0)
        // Week 1 Saturday Afternoon = Zumba (index 1)
        // Week 1 Saturday Evening = Aquacise (index 2)
        // Week 1 Sunday Morning = Box Fit (index 3)
        // Week 1 Sunday Afternoon = Yoga (index 4)
        // Week 1 Sunday Evening = Zumba (index 5)

        Lesson w1SatMorn  = all.get(0);   // Yoga Sat Morning Wk1
        Lesson w1SatAfter = all.get(1);   // Zumba Sat Afternoon Wk1
        Lesson w1SatEve   = all.get(2);   // Aquacise Sat Evening Wk1
        Lesson w1SunMorn  = all.get(3);   // Box Fit Sun Morning Wk1
        Lesson w1SunAfter = all.get(4);   // Yoga Sun Afternoon Wk1
        Lesson w1SunEve   = all.get(5);   // Zumba Sun Evening Wk1

        // Week 2
        Lesson w2SatMorn  = all.get(6);
        Lesson w2SatAfter = all.get(7);
        Lesson w2SunMorn  = all.get(9);

        alice.makeBooking(w1SatMorn,  1);
        alice.makeBooking(w1SunAfter, 1);
        bob.makeBooking(w1SatMorn,    1);
        bob.makeBooking(w1SunMorn,    1);
        carla.makeBooking(w1SatMorn,  1);
        carla.makeBooking(w1SatEve,   1);
        dave.makeBooking(w1SatMorn,   1);   // fills Yoga Sat Morning
        emma.makeBooking(w1SatAfter,  1);
        felix.makeBooking(w1SatAfter, 1);
        grace.makeBooking(w1SatAfter, 1);
        harry.makeBooking(w1SatAfter, 1);   // fills Zumba Sat Afternoon
        isla.makeBooking(w1SatEve,    1);
        isla.makeBooking(w1SunMorn,   1);
        james.makeBooking(w1SatEve,   1);
        james.makeBooking(w1SunEve,   1);
        alice.makeBooking(w1SunMorn,  1);   // time-conflict safe (diff day/slot)
        bob.makeBooking(w1SunAfter,   1);
        emma.makeBooking(w1SunEve,    1);

        // Week 2 bookings
        alice.makeBooking(w2SatMorn,  2);
        bob.makeBooking(w2SatMorn,    2);
        carla.makeBooking(w2SatAfter, 2);
        dave.makeBooking(w2SunMorn,   2);
        emma.makeBooking(w2SunMorn,   2);

        // ── Reviews (≥ 20 as required) ────────────────────────────────────
        alice.addReview(w1SatMorn,  5, "Absolutely loved this yoga session!");
        bob.addReview(w1SatMorn,    4, "Great instructor, very calming.");
        carla.addReview(w1SatMorn,  4, "Good session, could be longer.");
        dave.addReview(w1SatMorn,   3, "It was okay, nothing special.");

        emma.addReview(w1SatAfter,  5, "Best Zumba class I've been to!");
        felix.addReview(w1SatAfter, 4, "Very energetic, loved it.");
        grace.addReview(w1SatAfter, 5, "Such a fun workout.");
        harry.addReview(w1SatAfter, 3, "Decent but a bit too fast.");

        carla.addReview(w1SatEve,   4, "Aquacise is surprisingly intense.");
        isla.addReview(w1SatEve,    5, "Perfect for my joints.");
        james.addReview(w1SatEve,   4, "Really enjoyed it.");

        bob.addReview(w1SunMorn,    5, "Box Fit is incredible!");
        isla.addReview(w1SunMorn,   4, "Hard but rewarding.");
        alice.addReview(w1SunMorn,  4, "Challenging in a good way.");

        alice.addReview(w1SunAfter, 5, "Love Sunday Yoga, very relaxing.");
        bob.addReview(w1SunAfter,   4, "Peaceful and well-paced.");

        james.addReview(w1SunEve,   3, "Zumba evening was average.");
        emma.addReview(w1SunEve,    4, "Good way to end the weekend.");

        alice.addReview(w2SatMorn,  5, "Even better second week!");
        bob.addReview(w2SatMorn,    5, "Consistently excellent Yoga.");
        carla.addReview(w2SatAfter, 4, "Great Zumba, good energy.");

        return timetable;
    }

    public static List<Member> loadMembers() {
        // Return fresh member instances (same names) for use in tests
        List<Member> members = new ArrayList<>();
        members.add(new Member("Alice",  "alice@email.com"));
        members.add(new Member("Bob",    "bob@email.com"));
        members.add(new Member("Carla",  "carla@email.com"));
        members.add(new Member("Dave",   "dave@email.com"));
        members.add(new Member("Emma",   "emma@email.com"));
        members.add(new Member("Felix",  "felix@email.com"));
        members.add(new Member("Grace",  "grace@email.com"));
        members.add(new Member("Harry",  "harry@email.com"));
        members.add(new Member("Isla",   "isla@email.com"));
        members.add(new Member("James",  "james@email.com"));
        return members;
    }
}
