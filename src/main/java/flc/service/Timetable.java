package flc.service;

import flc.model.Day;
import flc.model.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Timetable {
    private List<Lesson> lessons;

    public Timetable() {
        this.lessons = new ArrayList<>();
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public List<Lesson> getAllLessons() {
        return new ArrayList<>(lessons);
    }

    public List<Lesson> getLessonsByDay(Day day) {
        return lessons.stream()
                .filter(l -> l.getDay() == day)
                .collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByExerciseName(String name) {
        return lessons.stream()
                .filter(l -> l.getExerciseType().getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByWeek(int week) {
        return lessons.stream()
                .filter(l -> l.getWeek() == week)
                .collect(Collectors.toList());
    }

    public List<Lesson> getAvailableLessons() {
        return lessons.stream()
                .filter(l -> !l.isFull())
                .collect(Collectors.toList());
    }

    public List<Lesson> getLessonsByDayAndWeek(Day day, int week) {
        return lessons.stream()
                .filter(l -> l.getDay() == day && l.getWeek() == week)
                .collect(Collectors.toList());
    }
}
