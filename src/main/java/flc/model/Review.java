package flc.model;

public class Review {
    private static int counter = 1;
    private String reviewId;
    private Member member;
    private Lesson lesson;
    private int rating;     // 1–5
    private String comment;

    public Review(Member member, Lesson lesson, int rating, String comment) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        this.reviewId = "R" + (counter++);
        this.member  = member;
        this.lesson  = lesson;
        this.rating  = rating;
        this.comment = comment;
    }

    public String  getReviewId() { return reviewId; }
    public Member  getMember()   { return member; }
    public Lesson  getLesson()   { return lesson; }
    public int     getRating()   { return rating; }
    public String  getComment()  { return comment; }

    private static final String[] LABELS = {"", "Very dissatisfied", "Dissatisfied", "Ok", "Satisfied", "Very Satisfied"};
    public String getRatingLabel() { return LABELS[rating]; }

    @Override
    public String toString() {
        return String.format("Review[%s] by %s | %d/5 – %s | \"%s\"",
                reviewId, member.getName(), rating, getRatingLabel(), comment);
    }
}
