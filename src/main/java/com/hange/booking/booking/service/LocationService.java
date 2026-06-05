package com.hange.booking.booking.service;

import java.util.List;

import com.hange.booking.booking.dto.location.RequestCreateLocationDTO;
import com.hange.booking.booking.dto.location.RequestUpdateLocationDTO;
import com.hange.booking.booking.dto.location.ResponseLocationDTO;

public interface LocationService {

	List<ResponseLocationDTO> getAll();

	ResponseLocationDTO getById(Long id);

	ResponseLocationDTO getBySlug(String slug);

	ResponseLocationDTO create(RequestCreateLocationDTO request);

	ResponseLocationDTO update(Long id, RequestUpdateLocationDTO request);

	void delete(Long id);
}