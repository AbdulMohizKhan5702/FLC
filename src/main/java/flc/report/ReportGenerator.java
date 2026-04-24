package flc.report;

import flc.model.Lesson;
import flc.service.Timetable;

import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private Timetable timetable;

    public ReportGenerator(Timetable timetable) {
        this.timetable = timetable;
    }

    /**
     * Report 1: attendance and average rating per lesson, grouped by week and day.////////
     */
    public void printAttendanceReport() {
        System.out.println("\n========================================");
        System.out.println("  ATTENDANCE & RATING REPORT");
        System.out.println("========================================");

        List<Lesson> all = timetable.getAllLessons();
        // Get distinct weeks sorted
        List<Integer> weeks = all.stream()
                .map(Lesson::getWeek).distinct().sorted()
                .collect(Collectors.toList());

        for (int week : weeks) {
            System.out.println("\n--- Week " + week + " ---");
            List<Lesson> weekLessons = timetable.getLessonsByWeek(week);
            weekLessons.sort(Comparator
                    .comparing(Lesson::getDay)
                    .thenComparing(Lesson::getTimeSlot));

            for (Lesson lesson : weekLessons) {
                System.out.printf("  %-12s %-10s %-10s | Members: %d/%d | Avg Rating: %s%n",
                        lesson.getExerciseType().getName(),
                        lesson.getDay(),
                        lesson.getTimeSlot(),
                        lesson.getMemberCount(),
                        lesson.getCapacity(),
                        lesson.getReviews().isEmpty()
                                ? "No reviews"
                                : String.format("%.1f/5", lesson.getAverageRating()));
            }
        }
        System.out.println("========================================\n");
    }

    /**
     * Report 2: which exercise type generated the highest total income.
     */
    public void printHighestIncomeReport() {
        System.out.println("\n========================================");
        System.out.println("  HIGHEST INCOME REPORT");
        System.out.println("========================================");

        Map<String, Double> incomeMap = new LinkedHashMap<>();

        for (Lesson lesson : timetable.getAllLessons()) {
            String exerciseName = lesson.getExerciseType().getName();
            double income = calcIncome(lesson);
            incomeMap.merge(exerciseName, income, Double::sum);
        }

        // Print all
        System.out.println("\n  Income by exercise type:");
        incomeMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-15s £%.2f%n", e.getKey(), e.getValue()));

        // Highlight the top earner
        incomeMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> {
                    System.out.println("\n  ★ Highest income: " + e.getKey()
                            + " → £" + String.format("%.2f", e.getValue()));
                });

        System.out.println("========================================\n");
    }

    /** Income for a single lesson = price × number of members booked. */
    public double calcIncome(Lesson lesson) {
        return lesson.getExerciseType().getPrice() * lesson.getMemberCount();
    }

    /** Average rating for a single lesson. */
    public double calcAvgRating(Lesson lesson) {
        return lesson.getAverageRating();
    }

    /** Member count for a single lesson. */
    public int getMemberCount(Lesson lesson) {
        return lesson.getMemberCount();
    }
}
