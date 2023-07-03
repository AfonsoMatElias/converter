package io.java.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



public class PosicaoPautalDto {
    private Long id;

    private String codigo;

    private String designacao;
    
    private String unidade;

    private BigDecimal ct_di;

    private BigDecimal ct_iva;
    
    private BigDecimal ct_iec;
    
    private BigDecimal ct_ega;

    private LocalDateTime dataCriacao;

    private PosicaoPautalDto posicaoPautal; 
    
    private List<PosicaoPautalDto> posicoesPautais;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDesignacao() {
        return designacao;
    }

    public void setDesignacao(String designacao) {
        this.designacao = designacao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getCt_di() {
        return ct_di;
    }

    public void setCt_di(BigDecimal ct_di) {
        this.ct_di = ct_di;
    }

    public BigDecimal getCt_iva() {
        return ct_iva;
    }

    public void setCt_iva(BigDecimal ct_iva) {
        this.ct_iva = ct_iva;
    }

    public BigDecimal getCt_iec() {
        return ct_iec;
    }

    public void setCt_iec(BigDecimal ct_iec) {
        this.ct_iec = ct_iec;
    }

    public BigDecimal getCt_ega() {
        return ct_ega;
    }

    public void setCt_ega(BigDecimal ct_ega) {
        this.ct_ega = ct_ega;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public PosicaoPautalDto getPosicaoPautal() {
        return posicaoPautal;
    }

    public void setPosicaoPautal(PosicaoPautalDto posicaoPautal) {
        this.posicaoPautal = posicaoPautal;
    }

    public List<PosicaoPautalDto> getPosicoesPautais() {
        return posicoesPautais;
    }

    public void setPosicoesPautais(List<PosicaoPautalDto> posicoesPautais) {
        this.posicoesPautais = posicoesPautais;
    } 
}