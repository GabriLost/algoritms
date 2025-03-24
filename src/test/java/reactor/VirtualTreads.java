package reactor;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class VirtualTreads {

    @Test
    public void compareTreads() {

        /*
            Почему бы нам всегда не использовать виртуальные потоки?
         */

        // Создаем пул потоков
        int taskCount = 100_000;
        // на 100_000 у меня полностью выжрат процессор и 10гб оперативки,
        // всё задачи зависли, тест идёт 2 минуты
        // на виртуальных потоках 2,5 секунды

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            // Запускаем несколько задач в виртуальных потоках
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        // Имитация работы
                        System.out.println("Task " + taskId + " is starting.");
                        Thread.sleep(1000); // Задержка на 1 секунду
                        System.out.println("Task " + taskId + " is completed.");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Task " + taskId + " was interrupted.");
                    }
                });
            }

            // Завершаем пул
            executor.shutdown();
        }
    }

    @Test
    public void somethingLikeReactiveStreams() {
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        // Создаем несколько независимых задач
        var task1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Starting task 1");
            sleep(1000); // Имитация работы
            return "Result from task 1";
        }, executor);

        var task2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Starting task 2");
            sleep(1500); // Имитация работы
            return "Result from task 2";
        }, executor);

        var task3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Starting task 3");
            sleep(2000); // Имитация работы
            return "Result from task 3";
        }, executor);

        // Объединяем все задачи и ждем их завершения
        var allTasks = CompletableFuture.allOf(task1, task2, task3)
                .thenRun(() -> {
                    try {
                        System.out.println("Final results:");
                        System.out.println(task1.get());
                        System.out.println(task2.get());
                        System.out.println(task3.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        // Ждем завершения всех задач
        allTasks.join();
        executor.shutdown();
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

