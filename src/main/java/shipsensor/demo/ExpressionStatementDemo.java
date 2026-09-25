package shipsensor.demo;

/**
 * 表达式 vs 语句 —— 教学示例（正式版）
 *
 * <p>内容迁移自仓库根部的 jshell 草稿 {@code hjcnv.java}（无包名、纯课堂笔记）。
 * 保留其教学价值：for 循环分号陷阱、expression 有值 / statement 无值。</p>
 */
public class ExpressionStatementDemo {

    public static void main(String[] args) {

        int total = 0;   // expression: 有值
        int i;           // statement:  无值

        // 经典陷阱：for 后的分号让循环体变成空语句，i 直接走到 6
        for (i = 0; i <= 5; i++) ;
        {
            total += i;  // 这个块只执行一次
        }

        System.out.println(total);
        // 期望 15，实际 21 —— 因为循环体的分号使累加发生在循环之后

        // jshell 速查：/list  /vars  /exit  /history  /method
        // /save mysnippets.jsh   /jshell --startup mysnippets.jsh
        // local version control system - vcs -> use database to track all the changed files
    }
}
