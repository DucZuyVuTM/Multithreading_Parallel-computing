package task3;

/**
 * Last pipeline result =.
 * For both success and error.
 * 
 * @param requestId  ID of initial request
 * @param value      = 0 if error
 * @param formatted  formatted string for printing
 * @param success    true if success, false if error
 */
public record Result(int requestId, double value, String formatted, boolean success) {

    public static Result success(int id, double value) {
        return new Result(id, value, String.format("Request #%d = %.2f", id, value), true);
    }

    public static Result failure(int id, String reason) {
        return new Result(id, 0, String.format("Request #%d FAILED: %s", id, reason), false);
    }
}
