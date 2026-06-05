package com.hange.booking.booking.service;

import java.util.List;

import com.hange.booking.booking.dto.service.RequestCreateServiceDTO;
import com.hange.booking.booking.dto.service.RequestUpdateServiceDTO;
import com.hange.booking.booking.dto.service.ResponseServiceDTO;

public interface ServiceService {

	List<ResponseServiceDTO> getAll();

	ResponseServiceDTO getById(Long id);

	ResponseServiceDTO getBySlug(String slug);

	ResponseServiceDTO create(RequestCreateServiceDTO request);

	ResponseServiceDTO update(Long id, RequestUpdateServiceDTO request);

	void delete(Long id);
}