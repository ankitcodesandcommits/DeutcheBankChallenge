package com.dws.challenge.domain;
import java.math.BigDecimal;

import lombok.Data;

@Data

public class TransferRequest {
	
	private Account accountFromId;
    private Account accountToId;
    private BigDecimal amount;

	
	
	
}
