import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        // 创建Scanner对象来读取控制台输入
        Scanner scanner = new Scanner(System.in);
        // 提示用户输入
        System.out.println("请输入第一个操作数：");

        String op1 = scanner.next();
        System.out.println("请输入第二个操作数：");
        String op2 = scanner.next();
        System.out.println("请输入操作符(+-*/)：");
        String operatorString = scanner.next();
        // 根据操作符进行计算
        OperatorFactory operatorFactory = new OperatorFactory();
        Operator operator = operatorFactory.createOperator(operatorString);
        double result = operator.apply(op1, op2);
        System.out.println("结果是：" + result);
        scanner.close();
    }
}
