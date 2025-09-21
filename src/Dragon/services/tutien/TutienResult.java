package Dragon.services.tutien;

/**
 * Kết quả của các thao tác Tu Tiên
 */
public class TutienResult {

    private final boolean success;
    private final String message;

    private TutienResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static TutienResult success(String message) {
        return new TutienResult(true, message);
    }

    public static TutienResult failure(String message) {
        return new TutienResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
