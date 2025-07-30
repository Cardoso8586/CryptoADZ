package com.cryptoadz.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cryptoadz.dto.SaqueHistoricoDTO;
import com.cryptoadz.repository.SaqueHistoricoRepository;

@Service
public class SaqueHistoricoService {

    private final SaqueHistoricoRepository repository;

    public SaqueHistoricoService(SaqueHistoricoRepository repository) {
        this.repository = repository;
    }

    public List<SaqueHistoricoDTO> listarHistoricoSemHash(String idString) {
        Long id = Long.parseLong(idString);
        return repository.buscarSemHash(id);
    }

}
