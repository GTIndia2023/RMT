package RMT.Exceptions;

public class SkillAdditionException extends RuntimeException{
    public SkillAdditionException(String message) {
        super(message);
    }

    public SkillAdditionException(String message, Throwable cause) {
        super(message, cause);
    }
}
