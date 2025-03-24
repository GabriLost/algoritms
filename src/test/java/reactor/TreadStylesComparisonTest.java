package reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Slf4j
public class TreadStylesComparisonTest {

    @Test
    public void imperative() {
        String user = getUserFromRemoteServer("user");
        List<String> favorites = getFavoritesFromRemoteService(user);
        List<String> recommended = getRecommendedFromRemoteService(user);
        log.info("\n {} \n {} \n {}", user, favorites, recommended);
    }

    @Test
    public void reactive() {
        var result = getMonoUser("user")
                .flatMap(user ->
                        Mono.zip(
                                getMonoFavorites(user),
                                getMonoRecommended(user),
                                Mono.just(user)))
                .subscribeOn(Schedulers.parallel())
                .block();
        log.info("\n {} \n {} \n {}", result.getT1(), result.getT2(), result.getT3());
    }

    @Test
    public void asyncTreads() {
//        var executor = Executors.newFixedThreadPool(2);
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        CompletableFuture.supplyAsync(() ->
                        getUserFromRemoteServer("user"), executor)
                .thenApply(user -> {
                    var favTask = CompletableFuture.supplyAsync(() -> getFavoritesFromRemoteService(user), executor);
                    var recTask = CompletableFuture.supplyAsync(() -> getRecommendedFromRemoteService(user), executor);

                    // Объединяем все задачи и ждем их завершения
                    var allTasks = CompletableFuture.allOf(favTask, recTask)
                            .thenRun(() -> {
                                try {
                                    log.info("\n {} \n {} \n {}", user, favTask.get(), recTask.get());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                    // Ждем завершения всех задач
                    allTasks.join();
                    return user;
                })
                .join();

        executor.shutdown();
    }

    private Mono<String> getMonoUser(String user) {
        return Mono.fromCallable(() -> getUserFromRemoteServer(user));
    }

    private Mono<List<String>> getMonoRecommended(String user) {
        return Mono.fromCallable(() -> getRecommendedFromRemoteService(user))
                .subscribeOn(Schedulers.parallel());
    }

    private Mono<List<String>> getMonoFavorites(String user) {
        return Mono.fromCallable(() -> getFavoritesFromRemoteService(user))
                .subscribeOn(Schedulers.parallel());
    }

    private String getUserFromRemoteServer(String user) {
        System.out.println(Thread.currentThread().getName());
        log.info("user request start");
        sleep(1000);
        log.info("user request end");
        return user + Math.round(Math.random() * 100);
    }

    private List<String> getFavoritesFromRemoteService(String user) {
        System.out.println(Thread.currentThread().getName());
        log.info("fav request start");
        sleep(1000);
        log.info("fav request end");
        return List.of(
                user + "fav1",
                user + "fav2",
                user + "fav3",
                user + "fav4",
                user + "fav5");
    }

    private List<String> getRecommendedFromRemoteService(String user) {
        System.out.println(Thread.currentThread().getName());
        log.info("fav recommended start");
        sleep(1000);
        log.info("fav recommended end");
        return List.of(
                user + "rec1",
                user + "rec2",
                user + "rec3");
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}

