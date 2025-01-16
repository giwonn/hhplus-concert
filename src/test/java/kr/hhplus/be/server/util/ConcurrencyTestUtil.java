package kr.hhplus.be.server.util;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConcurrencyTestUtil {

	public static Result run(List<Supplier<?>> suppliers) throws InterruptedException {
		List<Callable<Void>> tasks = suppliers.stream().map(supplier -> (Callable<Void>) () -> {
			supplier.get();
			return null;
		}).toList();

		ExecutorService executorService = Executors.newFixedThreadPool(tasks.size());
		List<Future<Void>> futures = executorService.invokeAll(tasks);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		// when
		for (Future<Void> future : futures) {
			try {
				future.get();
				successCount.incrementAndGet();
			} catch (Exception e) {
				failCount.incrementAndGet();
			}
		}
		executorService.shutdown();

		return new Result(successCount.get(), failCount.get());
	}

	public record Result(
			int successCount,
			int failCount
	) {}

}
