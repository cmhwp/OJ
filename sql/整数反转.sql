INSERT INTO my_oj.question (
    id, title, content, tags, frontendCode, logicCode, backendCode, answer, difficulty,
    submitNum, acceptedNum, judgeCase, judgeConfig, thumbNum, favourNum, userId, createTime,
    updateTime, isDelete
)
VALUES (
           1584608296940659533, '整数反转', '<p>给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。</p>',
           '["数学"]',
           'class Solution {\n    public int reverse(int x) {\n        // Implement your solution\n    }\n}',
           'class Solution {\n    public int reverse(int x) {\n        long rev = 0;\n        while (x != 0) {\n            rev = rev * 10 + x % 10;\n            x = x / 10;\n            if (rev > Integer.MAX_VALUE || rev < Integer.MIN_VALUE) {\n                return 0;\n            }\n        }\n        return (int) rev;\n    }\n}',
           'class Main {\n    public static void main(String[] args) {\n        Solution solution = new Solution();\n        int result = solution.reverse(Integer.parseInt(args[0]));\n        System.out.println(result);\n    }\n}',
           '<h3>方法一：</h3><p>这个问题可以通过长整型变量 rev 来存储反转的数字，从而避免整数溢出的问题。遍历整数的每一位数字，将其加到 rev 的当前值，并乘以 10 来移位。如果反转后的数字超过了 <code>int</code> 类型能表示的范围，函数返回 0。</p>',
           1, 606, 347,
           '[{"input":"123", "output":"321"},{"input":"-123", "output":"-321"},{"input":"120", "output":"21"},{"input":"0", "output":"0"}]',
           '{"timeLimit":2000,"memoryLimit":1024,"stackLimit":1024}',
           99, 9, 1554919853998881189, '2024-03-21 09:46:22', '2024-03-21 09:46:22', 0
       );
