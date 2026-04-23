package flc.model;

public class Booking {
    private static int counter = 1;
    private String bookingId;
    private Member member;
    private Lesson lesson;
    private int week;

    public Booking(Member member, Lesson lesson, int week) {
        this.bookingId = "B" + (counter++);
        this.member    = member;
        this.lesson    = lesson;
        this.week      = week;
    }

    public String  getBookingId() { return bookingId; }
    public Member  getMember()    { return member; }
    public Lesson  getLesson()    { return lesson; }
    public int     getWeek()      { return week; }

    @Override
    public String toString() {
        return String.format("Booking[%s] %s → %s (Week %d)",
                bookingId, member.getName(), lesson.getExerciseType().getName(), week);
    }
}
