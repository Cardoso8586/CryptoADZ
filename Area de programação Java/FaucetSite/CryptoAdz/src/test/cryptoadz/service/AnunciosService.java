package com.cryptoadz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cryptoadz.model.Anuncios;
import com.cryptoadz.repository.AnuncioRepository;
import com.cryptoadz.repository.AnuncioVisualizacaoRepository;

import jakarta.transaction.Transactional;

@Service
public class AnunciosService {

    @Autowired
    private AnuncioRepository anuncioRepository;
    
    
    @Autowired
    private AnuncioVisualizacaoRepository anuncioVisualizacaoRepository;
    
    @Autowired
    private AnuncioVisualizacaoService anuncioVisualizacaoService;  // aqui

    @Transactional
    public void excluirAnuncio(Long id) {


     // Depois delete o anúncio
        anuncioVisualizacaoRepository.deletarVisualizacoesPorAnuncioId(id);
        anuncioRepository.deleteById(id);
    }

    public Anuncios buscarPorId(Long anuncioId) {
        return anuncioRepository.findById(anuncioId)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));
    }

    public List<Anuncios> buscarAnunciosDisponiveisParaUsuario(String username) {
        List<Anuncios> todosAnuncios = anuncioRepository.findAll();

        return todosAnuncios.stream()
            .filter(anuncio -> !anuncioVisualizacaoService.estaBloqueado(username, anuncio.getId()))
            .toList();
    }
}

   

