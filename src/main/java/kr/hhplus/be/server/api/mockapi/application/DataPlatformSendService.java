package kr.hhplus.be.server.api.mockapi.application;

import kr.hhplus.be.server.api.mockapi.application.port.in.ReservationCreatedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataPlatformSendService {

	public void sendReservation(ReservationCreatedDto dto) {
		log.info("Sending Reservation to DataPlatform");
	}
}
