package kr.hhplus.be.server.core.provider.lock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LockResource {

	SEAT("seat");

	public final String value;
}
