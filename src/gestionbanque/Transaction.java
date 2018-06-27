/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

/**
 *
 * @author firas
 */
public class Transaction {
    
    private Compte_bancaire source,destinataire;
    private String dateTransaction;
    private double montant;
    
    public Transaction(Compte_bancaire source,Compte_bancaire destinataire,String dateTransaction,double montant)
    {
        this.source=source;
        this.destinataire=destinataire;
        this.dateTransaction=dateTransaction;
        this.montant=montant;
    }

    /**
     * @return the source
     */
    public Compte_bancaire getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Compte_bancaire source) {
        this.source = source;
    }

    /**
     * @return the destinataire
     */
    public Compte_bancaire getDestinataire() {
        return destinataire;
    }

    /**
     * @param destinataire the destinataire to set
     */
    public void setDestinataire(Compte_bancaire destinataire) {
        this.destinataire = destinataire;
    }

    /**
     * @return the dateTransaction
     */
    public String getDateTransaction() {
        return dateTransaction;
    }

    /**
     * @param dateTransaction the dateTransaction to set
     */
    public void setDateTransaction(String dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    /**
     * @return the montant
     */
    public double getMontant() {
        return montant;
    }

    /**
     * @param montant the montant to set
     */
    public void setMontant(double montant) {
        this.montant = montant;
    }
    
    
    
}
