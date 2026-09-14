package task3;

/**
 * Request for pipeline.
 * 
 * @param id         request ID for printing result
 * @param operandA   first operand
 * @param operandB   second operand (!= 0)
 * @param operation  "ADD", "MUL", "DIV"
 */
public record Request(int id, double operandA, double operandB, String operation) {

    public static Request of(int id, double a, double b, String op) {
        return new Request(id, a, b, op);
    }
}
