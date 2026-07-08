package hexlet.code.pages.tasks;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Task {
    private String title;
    private String content;
    private String assigneeEmail;
    private String statusName;
    private List<String> labels = new ArrayList<>();

    public Task(String title, String content, String assigneeEmail, String statusName) {
        this.title = title;
        this.content = content;
        this.assigneeEmail = assigneeEmail;
        this.statusName = statusName;
    }

    public Task(String title, String content, String assigneeEmail, String statusName, List<String> labels) {
        this(title, content, assigneeEmail, statusName);
        if (labels != null) {
            this.labels = new ArrayList<>(labels);
        }
    }
}
