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
    public double apply(String op1, String op2) {
        double op1Double = parseOperand(op1);
        double op2Double = parseOperand(op2);
        return op1Double + op2Double;
    }
}

class SubOperator extends Operator {
    public double apply(String op1, String op2) {
        double op1Double = parseOperand(op1);
        double op2Double = parseOperand(op2);
        return op1Double - op2Double;
    }
}

class MulOperator extends Operator {
    public double apply(String op1, String op2) {
        double op1Double = parseOperand(op1);
        double op2Double = parseOperand(op2);
        return op1Double * op2Double;
    }
}

class DivOperator extends Operator {
    public double apply(String op1, String op2) {
        double op1Double = parseOperand(op1);
        double op2Double = parseOperand(op2);
        if (op2Double == 0) {
            throw new IllegalArgumentException("除数不能为0");
        }
        return op1Double / op2Double;
    }
}