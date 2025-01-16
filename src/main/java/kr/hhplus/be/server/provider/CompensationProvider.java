package kr.hhplus.be.server.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Slf4j
@Component
public class CompensationProvider {

	public <T> T handle(MainTask<T> mainTask) {
		CompensationStack compensations = new CompensationStack();

		try {
			return mainTask.execute(compensations);
		} catch (Exception exception) {
			if (!compensations.isEmpty()) {
				log.warn("보상 트랜잭션 시작: {}", exception.getStackTrace()[0]);
				long startTime = System.currentTimeMillis();
				compensations.runAll();
				long endTime = System.currentTimeMillis();
				log.warn("보상 트랜잭션 끝: {}, 소요 시간: {}ms", exception.getStackTrace()[0], (endTime - startTime));
			}
			throw exception;
		}
	}

	@FunctionalInterface
	public interface MainTask<T> {
		T execute(CompensationStack compensations);
	}

	public static class CompensationStack {
		private final Deque<Runnable> tasks = new ArrayDeque<>();

		CompensationStack() {}

		public void add(Runnable task) {
			tasks.push(task);
		}

		private boolean isEmpty() {
			return tasks.isEmpty();
		}

		private void runAll() {
			while (!tasks.isEmpty()) {
				Runnable task = tasks.pop();
				task.run();
			}
		}
	}

}
