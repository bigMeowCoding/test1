import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        // 创建Scanner对象来读取控制台输入
        Scanner scanner = new Scanner(System.in);
        // 提示用户输入
        System.out.println("请输入第一个操作数：");
        double op1 = scanner.nextDouble();
        System.out.println("请输入第二个操作数：");
        double op2 = scanner.nextDouble();
        System.out.println("请输入操作符(+-*/)：");
        String operator = scanner.next();
        // 根据操作符进行计算
        switch (operator) {
            case "+":
                System.out.println("结果是：" + (op1 + op2));
                break;
            case "-":
                System.out.println("结果是：" + (op1 - op2));
                break;
            case "*":
                System.out.println("结果是：" + (op1 * op2));
                break;
            case "/":
                System.out.println("结果是：" + (op1 / op2));
                break;
            default:
                System.out.println("无效的操作符" + operator);
        }
        scanner.close();
    }
}
