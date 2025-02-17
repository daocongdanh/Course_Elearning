package group.project.cursusonlinecoursemanagement.shared.exception.handler;

import java.util.Date;

public record ErrorMessage(int statusCode, Date timestamp, String message, String description) {

}
