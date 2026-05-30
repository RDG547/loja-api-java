package br.com.entrega.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Entidade {
    private long id;
    private final LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    protected Entidade() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void marcarAtualizacao() {
        this.atualizadoEm = LocalDateTime.now();
    }

    protected Map<String, Object> baseMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", id);
        map.put("criadoEm", format(criadoEm));
        map.put("atualizadoEm", format(atualizadoEm));
        return map;
    }

    private String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public abstract Map<String, Object> toMap();
}
