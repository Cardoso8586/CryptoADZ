package com.cryptoadz.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tentativas_cadastro")
public class TentativaCadastro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ip;

    @Column(nullable = false)
    private Integer tentativas;

    @Column(nullable = false)
    private Boolean bloqueado;

    // Construtor padrão
    public TentativaCadastro() {
        this.tentativas = 0;
        this.bloqueado = false;
    }

    // Construtor com IP
    public TentativaCadastro(String ip) {
        this();
        this.ip = ip;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Integer getTentativas() { return tentativas; }
    public void setTentativas(Integer tentativas) { this.tentativas = tentativas; }

    public Boolean getBloqueado() { return bloqueado; }
    public void setBloqueado(Boolean bloqueado) { this.bloqueado = bloqueado; }

    // equals e hashCode baseados no id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TentativaCadastro)) return false;
        TentativaCadastro that = (TentativaCadastro) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TentativaCadastro{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", tentativas=" + tentativas +
                ", bloqueado=" + bloqueado +
                '}';
    }
}

