package com.cryptoadz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.TentativaCadastro;
import com.cryptoadz.repository.TentativaCadastroRepository;

@Service
public class TentativaCadastroService {

    @Autowired
    private TentativaCadastroRepository tentativaCadastroRepository;

    /**
     * Busca uma tentativa pelo IP ou cria uma nova se não existir.
     */
    public TentativaCadastro getOuCriarTentativa(String ip) {
        return tentativaCadastroRepository.findByIp(ip).orElseGet(() -> {
            TentativaCadastro nova = new TentativaCadastro(ip);
            return tentativaCadastroRepository.save(nova);
        });
    }

    /**
     * Verifica se o IP está bloqueado.
     */
    public boolean isBloqueado(TentativaCadastro tentativa) {
        return tentativa.getBloqueado();
    }

    /**
     * Registra uma nova tentativa de cadastro.
     * Bloqueia automaticamente após 5 tentativas.
     */
    public void registrarTentativa(TentativaCadastro tentativa) {
        int novasTentativas = tentativa.getTentativas() + 1;
        tentativa.setTentativas(novasTentativas);

        if (novasTentativas >= 5) {
            tentativa.setBloqueado(true);
        }

        tentativaCadastroRepository.save(tentativa);
    }

    /**
     * Remove completamente a tentativa de cadastro (por IP).
     */
    public void limparTentativas(TentativaCadastro tentativa) {
        tentativaCadastroRepository.delete(tentativa);
    }
}

