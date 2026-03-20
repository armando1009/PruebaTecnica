package com.prueba.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identificador;
    private Double monto;
    private Double comision;
    private String nombre;
    private String codigoRespuesta;
    private String mensajeRespuesta;
    private LocalDateTime fecha;

    public Transaccion() {
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }

    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public Double getComision() { return comision; }
    public void setComision(Double comision) { this.comision = comision; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCodigoRespuesta() { return codigoRespuesta; }
    public void setCodigoRespuesta(String codigoRespuesta) { this.codigoRespuesta = codigoRespuesta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public LocalDateTime getFecha() { return fecha; }
}