package flc.model;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private static int counter = 1;
    private String lessonId;
    private ExerciseType exerciseType;
    private Day day;
    private TimeSlot timeSlot;
    private int week;
    private static final int CAPACITY = 4;
    private List<Member> members;
    private List<Review> reviews;
//////
    public Lesson(ExerciseType exerciseType, Day day, TimeSlot timeSlot, int week) {
        this.lessonId     = "L" + (counter++);
        this.exerciseType = exerciseType;
        this.day          = day;
        this.timeSlot     = timeSlot;
        this.week         = week;
        this.members      = new ArrayList<>();
        this.reviews      = new ArrayList<>();
    }

    // ── Member management ──────────────────────────────────────────────────
    public boolean addMember(Member member) {
        if (isFull()) return false;
        if (members.contains(member)) return false;
        members.add(member);
        return true;
    }

    public boolean removeMember(Member member) {
        return members.remove(member);
    }

    public boolean hasMember(Member member) {
        return members.contains(member);
    }

    public boolean isFull() {
        return members.size() >= CAPACITY;
    }

    public int getMemberCount() { return members.size(); }
    public int getCapacity()    { return CAPACITY; }
    public List<Member> getMembers() { return new ArrayList<>(members); }

    // ── Review management ──────────────────────────────────────────────────
    public void addReview(Review review) {
        reviews.add(review);
    }

    public List<Review> getReviews() { return new ArrayList<>(reviews); }

    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public String       getLessonId()     { return lessonId; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public Day          getDay()          { return day; }
    public TimeSlot     getTimeSlot()     { return timeSlot; }
    public int          getWeek()         { return week; }

    @Override
    public String toString() {
        return String.format("Lesson[%s] %s | %s %s | Week %d | %d/%d members | Avg rating: %.1f",
                lessonId, exerciseType.getName(), day, timeSlot,
                week, members.size(), CAPACITY, getAverageRating());
    }
}
