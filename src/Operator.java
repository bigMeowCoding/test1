public class Operator {
    protected double parseOperand(String operand) {
        try {
            return Double.parseDouble(operand);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无效的操作数", e);
        }
    }

    public double apply(String op1, String op2) {
        return 0;
    }
}

