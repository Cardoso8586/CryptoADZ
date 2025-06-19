package com.cryptoadz.service;

import com.cryptoadz.model.Aviso;
import com.cryptoadz.repository.AvisoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;

    public AvisoService(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    public List<Aviso> listarTodos() {
        return avisoRepository.findAll();
    }

    public Optional<Aviso> buscarPorId(Long id) {
        return avisoRepository.findById(id);
    }

    public Aviso salvar(Aviso aviso) {
        return avisoRepository.save(aviso);
    }

    public Aviso atualizar(Long id, Aviso avisoAtualizado) {
        return avisoRepository.findById(id).map(aviso -> {
            aviso.setTitulo(avisoAtualizado.getTitulo());
            aviso.setMensagem(avisoAtualizado.getMensagem());
            aviso.setDataInicio(avisoAtualizado.getDataInicio());
            aviso.setDataValidade(avisoAtualizado.getDataValidade());
            return avisoRepository.save(aviso);
        }).orElseThrow(() -> new RuntimeException("Aviso não encontrado"));
    }

    public void deletar(Long id) {
        avisoRepository.deleteById(id);
    }

    /**
     * 🔥 Tarefa agendada para excluir avisos vencidos diariamente
     * Executa todo dia às 00:00
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deletarAvisosVencidos() {
        System.out.println("🧹 Limpando avisos vencidos...");
        avisoRepository.deleteByDataValidadeBefore(LocalDate.now());
    }
}
