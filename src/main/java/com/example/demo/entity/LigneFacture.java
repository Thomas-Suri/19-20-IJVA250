package com.example.demo.entity;

import javax.persistence.*;

@Entity
public class LigneFacture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Facture getFacture() {
        return facture;
    }

    public void setFacture(Facture facture) {
        this.facture = facture;
    }

    @ManyToOne
    private Facture facture;

    @ManyToOne
    private Article article;

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    @Column
    private Integer quantite;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Article getArticle(){
        return article;
    }

    public void setArticle(Article article){
        this.article = article;
    }

    public Double getSousTotal(){
        return getArticle().getPrix() * quantite;
    }
}
