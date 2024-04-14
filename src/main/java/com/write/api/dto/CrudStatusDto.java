package com.write.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CrudStatusDto {

	public enum CrudStatus {
		CREATED, DELETED
	}
		
	@JsonProperty("status")
	private CrudStatus status;
}
