package flc.ui;

import flc.data.DataLoader;
import flc.model.*;
import flc.report.ReportGenerator;
import flc.service.Timetable;

import java.util.*;

/**
 * Simple console UI for the FLC booking system.///
 */
public class FLCConsoleApp {
    private Timetable timetable;
    private List<Member> members;
    private ReportGenerator reportGen;
    private Scanner scanner;

    public FLCConsoleApp() {
        this.timetable = DataLoader.loadTimetable();
        this.members   = DataLoader.loadMembers();
        this.reportGen = new ReportGenerator(timetable);
        this.scanner   = new Scanner(System.in);
    }

    public void start() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   Furzefield Leisure Centre (FLC)        ║");
        System.out.println("║   Group Booking System                   ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> viewTimetableByDay();
                case 2 -> viewTimetableByExercise();
                case 3 -> makeBooking();
                case 4 -> changeBooking();
                case 5 -> addReview();
                case 6 -> reportGen.printAttendanceReport();
                case 7 -> reportGen.printHighestIncomeReport();
                case 8 -> running = false;
                default -> System.out.println("  Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void printMainMenu() {
        System.out.println("\n──────────────────────────────────────");
        System.out.println("  1. View timetable by day");
        System.out.println("  2. View timetable by exercise");
        System.out.println("  3. Book a lesson");
        System.out.println("  4. Change a booking");
        System.out.println("  5. Add a review");
        System.out.println("  6. Print attendance & rating report");
        System.out.println("  7. Print highest income report");
        System.out.println("  8. Exit");
        System.out.println("──────────────────────────────────────");
    }

    private void viewTimetableByDay() {
        System.out.println("\nSelect day: 1=SATURDAY  2=SUNDAY");
        int d = readInt("> ");
        Day day = (d == 1) ? Day.SATURDAY : Day.SUNDAY;
        System.out.println("\nEnter week (1-8): ");
        int week = readInt("> ");

        List<Lesson> lessons = timetable.getLessonsByDayAndWeek(day, week);
        if (lessons.isEmpty()) {
            System.out.println("No lessons found.");
            return;
        }
        System.out.println("\n" + day + " – Week " + week + ":");
        lessons.forEach(l -> System.out.println("  " + l));
    }

    private void viewTimetableByExercise() {
        System.out.print("\nEnter exercise name (Yoga/Zumba/Aquacise/Box Fit): ");
        String name = scanner.nextLine().trim();
        List<Lesson> lessons = timetable.getLessonsByExerciseName(name);
        if (lessons.isEmpty()) {
            System.out.println("No lessons found for: " + name);
            return;
        }
        System.out.println("\nAll lessons for " + name + ":");
        lessons.forEach(l -> System.out.println("  " + l));
    }

    private void makeBooking() {
        Member member = selectMember();
        if (member == null) return;

        System.out.println("\nSelect day: 1=SATURDAY  2=SUNDAY");
        int d = readInt("> ");
        Day day = (d == 1) ? Day.SATURDAY : Day.SUNDAY;
        System.out.print("Enter week (1-8): ");
        int week = readInt("> ");

        List<Lesson> available = timetable.getLessonsByDayAndWeek(day, week)
                .stream().filter(l -> !l.isFull()).toList();
        if (available.isEmpty()) {
            System.out.println("No available lessons for that day/week.");
            return;
        }
        System.out.println("\nAvailable lessons:");
        for (int i = 0; i < available.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + available.get(i));
        }
        int idx = readInt("Select lesson: ") - 1;
        if (idx < 0 || idx >= available.size()) { System.out.println("Invalid."); return; }

        Lesson lesson = available.get(idx);
        Booking booking = member.makeBooking(lesson, week);
        if (booking == null) {
            System.out.println("Booking failed (time conflict or already booked).");
        } else {
            System.out.println("Booking confirmed: " + booking);
        }
    }

    private void changeBooking() {
        Member member = selectMember();
        if (member == null) return;

        List<Booking> bookings = member.getBookings();
        if (bookings.isEmpty()) { System.out.println("No bookings found."); return; }

        System.out.println("\nCurrent bookings:");
        for (int i = 0; i < bookings.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + bookings.get(i));
        }
        int bIdx = readInt("Select booking to change: ") - 1;
        if (bIdx < 0 || bIdx >= bookings.size()) { System.out.println("Invalid."); return; }
        Booking oldBooking = bookings.get(bIdx);

        // Show available lessons for same week
        List<Lesson> available = timetable.getLessonsByWeek(oldBooking.getWeek())
                .stream().filter(l -> !l.isFull() && !l.equals(oldBooking.getLesson())).toList();
        if (available.isEmpty()) { System.out.println("No alternative lessons available."); return; }

        System.out.println("\nAvailable lessons for week " + oldBooking.getWeek() + ":");
        for (int i = 0; i < available.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + available.get(i));
        }
        int lIdx = readInt("Select new lesson: ") - 1;
        if (lIdx < 0 || lIdx >= available.size()) { System.out.println("Invalid."); return; }

        boolean success = member.changeBooking(oldBooking, available.get(lIdx));
        System.out.println(success ? "Booking changed successfully." : "Change failed (conflict or lesson full).");
    }

    private void addReview() {
        Member member = selectMember();
        if (member == null) return;

        List<Booking> bookings = member.getBookings();
        if (bookings.isEmpty()) { System.out.println("No attended lessons to review."); return; }

        System.out.println("\nSelect lesson to review:");
        for (int i = 0; i < bookings.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + bookings.get(i).getLesson());
        }
        int idx = readInt("Select: ") - 1;
        if (idx < 0 || idx >= bookings.size()) { System.out.println("Invalid."); return; }

        Lesson lesson = bookings.get(idx).getLesson();
        int rating = readInt("Rating (1=Very dissatisfied … 5=Very Satisfied): ");
        System.out.print("Comment: ");
        String comment = scanner.nextLine().trim();

        Review review = member.addReview(lesson, rating, comment);
        System.out.println(review != null ? "Review added: " + review : "Review failed.");
    }

    private Member selectMember() {
        System.out.println("\nSelect member:");
        for (int i = 0; i < members.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + members.get(i).getName());
        }
        int idx = readInt("> ") - 1;
        if (idx < 0 || idx >= members.size()) { System.out.println("Invalid."); return null; }
        return members.get(idx);
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        new FLCConsoleApp().start();
    }
}
