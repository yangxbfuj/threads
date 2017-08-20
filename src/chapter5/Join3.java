package chapter5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import utils.PrintUtil;

/**
 * 在文档中查找一个词
 * 1.一个文档任务.它将遍历文档中的每一行来查找这个词
 * 2.一个行任务.它将在文档的一部分当中查找这个词
 */
public class Join3 {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        Document document = new Document();
        String[][] doc = document.generateDocument(100, 1000, "the");
        DocumentTask task = new DocumentTask(doc, 0, 100, "the");
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(task);
        forkJoinPool.shutdown();
        PrintUtil.println("Main : 查找到的数量为 " + task.get());
    }
}

class Document {
    private String words[] = {
            "the", "hello", "goodbye", "packt", "java", "thread", "pool", "random", "class", "main"
    };

    String[][] generateDocument(int numLines, int numWords, String word) {
        int counter = 0;
        String document[][] = new String[numLines][numWords];
        Random random = new Random();
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < numWords; j++) {
                int index = random.nextInt(words.length);
                document[i][j] = words[index];
                if (document[i][j].equals(word))
                    counter++;
            }
        }
        PrintUtil.println("DocumentMock : 单词 " + word + " 出现了 " + counter + " 次");
        return document;
    }
}

class DocumentTask extends RecursiveTask<Integer> {

    private String[][] document;
    private int start, end;
    private String word;

    DocumentTask(String[][] document, int start, int end, String word) {
        this.document = document;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {
        int ret = 0;
        if (end - start < 10) {
            ret = processLines(document, start, end, word);
        } else {
            int mid = (start + end) / 2;
            DocumentTask task1 = new DocumentTask(document, start, mid, word);
            DocumentTask task2 = new DocumentTask(document, mid, end, word);
            task1.fork();
            task2.fork();
            //invokeAll(task1, task2);
            // 合并任务的结果
            try {
                ret = task1.get() + task2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private Integer processLines(String[][] document, int start, int end, String word) {
        List<LineTask> lineTasks = new ArrayList<>();
        for (int i = start; i < end; i++) {
            LineTask task = new LineTask(document[i], 0, document[i].length, word);
            lineTasks.add(task);
            task.fork();
        }
        //invokeAll(lineTasks);
        int ret = 0;
        // 合并任务的结果
        for (LineTask task : lineTasks) {
            try {
                ret += task.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}

class LineTask extends RecursiveTask<Integer> {

    private String[] line;
    private int start, end;
    private String word;

    LineTask(String[] line, int start, int end, String word) {
        this.line = line;
        this.start = start;
        this.end = end;
        this.word = word;
    }

    @Override
    protected Integer compute() {
        Integer ret = 0;
        if (end - start < 100)
            ret = count(line, start, end, word);
        else {
            int mid = (start + end) / 2;
            LineTask lineTask1 = new LineTask(line, start, mid, word);
            LineTask lineTask2 = new LineTask(line, mid, end, word);
            invokeAll(lineTask1, lineTask2);
            try {
                ret = lineTask1.get() + lineTask2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    private int count(String[] line, int start, int end, String word) {
        int ret = 0;
        for (int i = start; i < end; i++) {
            if (line[i].equals(word))
                ret++;
        }
        return ret;
    }
}