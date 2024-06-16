public class OperatorFactory {
    Operator createOperator(String operator) {
        return switch (operator) {
            case "+" -> new AddOperator();
            case "-" -> new SubOperator();
            case "*" -> new MulOperator();
            case "/" -> new DivOperator();
            default -> throw new IllegalArgumentException("无效的操作符" + operator);
        };
    }
}

class AddOperator extends Operator {
    public double apply(double op1, double op2) {
        return op1 + op2;
    }
}

class SubOperator extends Operator {
    public double apply(double op1, double op2) {
        return op1 - op2;
    }
}

class MulOperator extends Operator {
    public double apply(double op1, double op2) {
        return op1 * op2;
    }
}

class DivOperator extends Operator {
    public double apply(double op1, double op2) {
        return op1 / op2;
    }
}