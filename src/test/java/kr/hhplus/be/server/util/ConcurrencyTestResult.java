package kr.hhplus.be.server.util;

public class ConcurrencyTestResult {
	private final int successCount;
	private final int failCount;

	ConcurrencyTestResult(int successCount, int failCount) {
		this.successCount = successCount;
		this.failCount = failCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public int getFailCount() {
		return failCount;
	}
}
