package kr.hhplus.be.server.api.queue.presentation;

import kr.hhplus.be.server.api.queue.application.QueueService;
import kr.hhplus.be.server.core.annotation.logexcutiontime.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class QueueScheduler {

	private final QueueService queueService;

	@LogExecutionTime
	@Scheduled(cron = "*/3 * * * * *")
	public void activateQueueToken() {
		queueService.activateWaitingTokens();
	}

	@LogExecutionTime
	@Scheduled(cron = "0 */1 * * * *")
	public void deleteExpiredQueueTokens() {
		queueService.removeExpiredQueueTokens();
	}
}
